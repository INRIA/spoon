/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.support.util.RtHelper;
import spoon.support.visitor.java.internal.AnnotationRuntimeBuilderContext;
import spoon.support.visitor.java.internal.ExecutableRuntimeBuilderContext;
import spoon.support.visitor.java.internal.PackageRuntimeBuilderContext;
import spoon.support.visitor.java.internal.RecordComponentRuntimeBuilderContext;
import spoon.support.visitor.java.internal.RuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeReferenceRuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeRuntimeBuilderContext;
import spoon.support.visitor.java.internal.VariableRuntimeBuilderContext;
import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;
/**
 * Builds Spoon model from class file using the reflection api. The Spoon model
 * contains only the declaration part (type, field, method, etc.). Everything
 * that isn't available with the reflection api is absent from the model. Those
 * models are available when {@link CtTypeReference#getTypeDeclaration()},
 * {@link CtExecutableReference#getExecutableDeclaration()} and
 * {@link CtFieldReference#getFieldDeclaration()} are called. To know when an
 * element comes from the reflection api, use {@link spoon.reflect.declaration.CtShadowable#isShadow()}.
 */
public class JavaReflectionTreeBuilder extends JavaReflectionVisitorImpl {
	private final Deque<RuntimeBuilderContext> contexts;
	private final Factory factory;

	public JavaReflectionTreeBuilder(Factory factory) {
		this.factory = factory;
		this.contexts = new ArrayDeque<>();
	}

	private void enter(RuntimeBuilderContext context) {
		contexts.push(context);
	}

	private RuntimeBuilderContext exit() {
		return contexts.pop();
	}

	/** transforms a java.lang.Class into a CtType (ie a shadow type in Spoon's parlance) */
	public <T, R extends CtType<T>> R scan(Class<T> clazz) {
		// We modify and query our modified model in this part. If another thread were to do the same
		// on the same model, things will explode (e.g. with a ParentNotInitialized exception).
		// We only synchronize in the main entrypoint, as that should be enough for normal consumers.
		// The shadow factory should not be modified in other places and nobody should be directly calling
		// the visit methods.
		synchronized (factory) {
			CtType<?> ctEnclosingClass;
			if (clazz.getEnclosingClass() != null && !clazz.isAnonymousClass()) {
				ctEnclosingClass = factory.Type().get(clazz.getEnclosingClass());
				return ctEnclosingClass.getNestedType(clazz.getSimpleName());
			} else {
				CtPackage ctPackage = getCtPackage(clazz);
				if (contexts.isEmpty()) {
					enter(new PackageRuntimeBuilderContext(ctPackage));
				}
				boolean visited = false;
				if (clazz.isAnnotation()) {
					visited = true;
					visitAnnotationClass((Class<Annotation>) clazz);
				}
				if (clazz.isInterface() && !visited) {
					visited = true;
					visitInterface(clazz);
				}
				if (clazz.isEnum() && !visited) {
					visited = true;
					visitEnum(clazz);
				}
				if (MethodHandleUtils.isRecord(clazz) && !visited) {
					visited = true;
					visitRecord(clazz);
				}
				if (!visited) {
					visitClass(clazz);
				}
				exit();
				final R type = ctPackage.getType(clazz.getSimpleName());
				if (clazz.isPrimitive() && type.getParent() instanceof CtPackage) {
					type.setParent(null); // primitive type isn't in a package.
				}
				return type;
			}
		}
	}

	private <T> CtPackage getCtPackage(Class<T> clazz) {
		Package javaPackage = clazz.getPackage();
		if (javaPackage == null && clazz.isArray()) {
			javaPackage = getArrayType(clazz).getPackage();
		}
		if (javaPackage == null) {
			return factory.Package().getRootPackage();
		}
		return factory.Package().getOrCreate(javaPackage.getName());
	}

	private static Class<?> getArrayType(Class<?> array) {
		if (array.isArray()) {
			return getArrayType(array.getComponentType());
		}
		return array;
	}

	@Override
	public void visitPackage(Package aPackage) {
		CtPackage ctPackage = factory.Package().get(aPackage.getName());
		// this is a dangerous section:
		// we DON'T want to visit packages recursively if there are cyclic annotations
		// => we only call the super method if:
		//    - the package is not known by the factory (it wasn't visited before)
		//    - the package is not in the current context stack
		if (ctPackage == null || shouldVisitPackage(ctPackage)) {
			ctPackage = factory.Package().getOrCreate(aPackage.getName());
			enter(new PackageRuntimeBuilderContext(ctPackage));
			super.visitPackage(aPackage);
			exit();
		}

		contexts.peek().addPackage(ctPackage);
	}

	// Returns whether the given package is already in the context stack
	private boolean shouldVisitPackage(CtPackage ctPackage) {
		Iterator<RuntimeBuilderContext> iterator = contexts.iterator();
		while (iterator.hasNext()) {
			RuntimeBuilderContext next = iterator.next();
			// we don't want to visit the context inserted first, as it's always
			// a PackageRuntimeBuilderContext (see scan(...)) but it does not visit
			// the package. So yes, the hasNext check is intended here
			if (iterator.hasNext() && next instanceof PackageRuntimeBuilderContext) {
				if (((PackageRuntimeBuilderContext) next).getPackage() == ctPackage) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public <T> void visitClass(Class<T> clazz) {
		final CtClass ctClass = factory.Core().createClass();
		ctClass.setSimpleName(clazz.getSimpleName());
		setModifier(ctClass, clazz.getModifiers() & Modifier.classModifiers());

		enter(new TypeRuntimeBuilderContext(clazz, ctClass) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctClass.addConstructor(ctConstructor);
			}
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				switch (role) {
					case SUPER_TYPE:
						ctClass.setSuperclass(typeReference);
						return;
				}
				super.addTypeReference(role, typeReference);
			}
		});
		super.visitClass(clazz);
		exit();

		contexts.peek().addType(ctClass);
	}

	@Override
	public <T> void visitInterface(Class<T> clazz) {
		final CtInterface<Object> ctInterface = factory.Core().createInterface();
		ctInterface.setSimpleName(clazz.getSimpleName());
		setModifier(ctInterface, clazz.getModifiers() & Modifier.classModifiers());

		enter(new TypeRuntimeBuilderContext(clazz, ctInterface));
		super.visitInterface(clazz);
		exit();

		contexts.peek().addType(ctInterface);
	}

	@Override
	public <T> void visitEnum(Class<T> clazz) {
		final CtEnum ctEnum = factory.Core().createEnum();
		ctEnum.setSimpleName(clazz.getSimpleName());
		setModifier(ctEnum, clazz.getModifiers() & Modifier.classModifiers());

		enter(new TypeRuntimeBuilderContext(clazz, ctEnum) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctEnum.addConstructor(ctConstructor);
			}

			@Override
			public void addEnumValue(CtEnumValue<?> ctEnumValue) {
				ctEnum.addEnumValue(ctEnumValue);
			}
		});
		super.visitEnum(clazz);
		exit();

		contexts.peek().addType(ctEnum);
	}

	@Override
	public <T extends Annotation> void visitAnnotationClass(Class<T> clazz) {
		final CtAnnotationType<?> ctAnnotationType = factory.Core().createAnnotationType();
		ctAnnotationType.setSimpleName(clazz.getSimpleName());
		setModifier(ctAnnotationType, clazz.getModifiers() & Modifier.classModifiers());

		enter(new TypeRuntimeBuilderContext(clazz, ctAnnotationType) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				final CtAnnotationMethod<Object> field = factory.Core().createAnnotationMethod();
				field.setSimpleName(ctMethod.getSimpleName());
				field.setModifiers(ctMethod.getModifiers());
				field.setType(ctMethod.getType());
				field.setShadow(true);
				ctAnnotationType.addMethod(field);
			}
		});
		super.visitAnnotationClass(clazz);
		exit();

		contexts.peek().addType(ctAnnotationType);
	}

	@Override
	public void visitAnnotation(final Annotation annotation) {
		final CtAnnotation<Annotation> ctAnnotation = factory.Core().createAnnotation();

		enter(new AnnotationRuntimeBuilderContext(ctAnnotation) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				try {
					Object value = annotation.annotationType().getMethod(ctMethod.getSimpleName()).invoke(annotation);

					// if there's only one element in annotation,
					// then we only put that element's value.
					// this intends to keep the same behaviour than when spooning a model
					// with @MyAnnotation(values = "myval") -> Spoon creates only a CtLiteral for "values"
					// even if the return type should be a String[]
					if (value instanceof Object[]) {
						Object[] values = (Object[]) value;
						if (values.length == 1) {
							value = values[0];
						}
					}
					ctAnnotation.addValue(ctMethod.getSimpleName(), value);
				} catch (Exception ignore) {
					ctAnnotation.addValue(ctMethod.getSimpleName(), "");
				}
			}
		});
		super.visitAnnotation(annotation);
		exit();

		contexts.peek().addAnnotation(ctAnnotation);
	}

	@Override
	public <T> void visitConstructor(Constructor<T> constructor) {
		final CtConstructor<Object> ctConstructor = factory.Core().createConstructor();
		ctConstructor.setBody(factory.Core().createBlock());
		setModifier(ctConstructor, constructor.getModifiers() & Modifier.constructorModifiers());

		enter(new ExecutableRuntimeBuilderContext(constructor, ctConstructor));
		super.visitConstructor(constructor);
		exit();

		contexts.peek().addConstructor(ctConstructor);
	}

	@Override
	public void visitMethod(RtMethod method, Annotation parent) {
		final CtMethod<Object> ctMethod = factory.Core().createMethod();
		ctMethod.setSimpleName(method.getName());
		/**
		 * java 8 static interface methods are marked as abstract but has body
		 */
		if (!Modifier.isAbstract(method.getModifiers())) {
			ctMethod.setBody(factory.Core().createBlock());
		}
		setModifier(ctMethod, method.getModifiers() & Modifier.methodModifiers());
		ctMethod.setDefaultMethod(method.isDefault());

		enter(new ExecutableRuntimeBuilderContext(method.getMethod(), ctMethod));
		super.visitMethod(method, parent);
		exit();

		contexts.peek().addMethod(ctMethod);
	}

	@Override
	public void visitField(Field field) {
		final CtField<Object> ctField = factory.Core().createField();
		ctField.setSimpleName(field.getName());
		setModifier(ctField, field.getModifiers() & Modifier.fieldModifiers());

		// we set the value of the shadow field if it is a public and static primitive value
		try {
			Set<ModifierKind> modifiers = RtHelper.getModifiers(field.getModifiers());
			if (modifiers.contains(ModifierKind.STATIC)
					&& modifiers.contains(ModifierKind.PUBLIC)
					&& (field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType()))) {
				CtExpression<Object> defaultExpression = buildExpressionForValue(field.get(null));
				ctField.setDefaultExpression(defaultExpression);
			}
		} catch (IllegalAccessException | ExceptionInInitializerError | UnsatisfiedLinkError e) {
			// ignore
		}

		enter(new VariableRuntimeBuilderContext(ctField));
		super.visitField(field);
		exit();

		contexts.peek().addField(ctField);
	}

	private CtExpression<Object> buildExpressionForValue(Object value) {
		if (value instanceof Double) {
			double d = (double) value;
			if (Double.isNaN(d)) {
				return buildDivision(0.0d, 0.0d);
			}
			if (Double.POSITIVE_INFINITY == d) {
				return buildDivision(1.0d, 0.0d);
			}
			if (Double.NEGATIVE_INFINITY == d) {
				return buildDivision(-1.0d, 0.0d);
			}
		} else if (value instanceof Float) {
			float f = (float) value;
			if (Float.isNaN(f)) {
				return buildDivision(0.0f, 0.0f);
			}
			if (Float.POSITIVE_INFINITY == f) {
				return buildDivision(1.0f, 0.0f);
			}
			if (Float.NEGATIVE_INFINITY == f) {
				return buildDivision(-1.0f, 0.0f);
			}
		}
		return factory.createLiteral(value);
	}

	private CtBinaryOperator<Object> buildDivision(Object first, Object second) {
		return factory.createBinaryOperator(factory.createLiteral(first), factory.createLiteral(second), BinaryOperatorKind.DIV);
	}

	@Override
	public void visitEnumValue(Field field) {
		final CtEnumValue<Object> ctEnumValue = factory.Core().createEnumValue();
		ctEnumValue.setSimpleName(field.getName());
		setModifier(ctEnumValue, field.getModifiers() & Modifier.fieldModifiers());

		enter(new VariableRuntimeBuilderContext(ctEnumValue));
		super.visitEnumValue(field);
		exit();

		contexts.peek().addEnumValue(ctEnumValue);
	}

	@Override
	public void visitParameter(RtParameter parameter) {
		final CtParameter ctParameter = factory.Core().createParameter();
		ctParameter.setSimpleName(parameter.getName());
		ctParameter.setVarArgs(parameter.isVarArgs());
		// it is not possible to detect whether parameter is final in runtime
		enter(new VariableRuntimeBuilderContext(ctParameter));
		super.visitParameter(parameter);
		exit();

		contexts.peek().addParameter(ctParameter);
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter) {
		GenericDeclaration genericDeclaration = parameter.getGenericDeclaration();
		for (RuntimeBuilderContext context : contexts) {
			CtTypeParameter typeParameter = context.getTypeParameter(genericDeclaration, parameter.getName());
			if (typeParameter != null) {
				contexts.peek().addFormalType(typeParameter.clone());
				return;
			}
		}

		final CtTypeParameter typeParameter = factory.Core().createTypeParameter();
		typeParameter.setSimpleName(parameter.getName());

		enter(new TypeRuntimeBuilderContext(parameter, typeParameter) {
			@SuppressWarnings("incomplete-switch")
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				switch (role) {
					case SUPER_TYPE:
						if (typeParameter.getSuperclass() != null) {
							typeParameter.setSuperclass(typeParameter.getFactory().createIntersectionTypeReferenceWithBounds(Arrays.asList(typeParameter.getSuperclass(), typeReference)));
						} else {
							typeParameter.setSuperclass(typeReference);
						}
						return;
				}
				super.addTypeReference(role, typeReference);
			}
		});
		super.visitTypeParameter(parameter);
		exit();

		contexts.peek().addFormalType(typeParameter);
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameterReference(CtRole role, TypeVariable<T> parameter) {
		final CtTypeParameterReference typeParameterReference = factory.Core().createTypeParameterReference();
		typeParameterReference.setSimpleName(parameter.getName());

		RuntimeBuilderContext runtimeBuilderContext = new TypeReferenceRuntimeBuilderContext(parameter, typeParameterReference);
		GenericDeclaration genericDeclaration = parameter.getGenericDeclaration();
		for (RuntimeBuilderContext context : contexts) {
			CtTypeParameter typeParameter = context.getTypeParameter(genericDeclaration, parameter.getName());
			if (typeParameter != null) {
				contexts.peek().addTypeReference(role, typeParameter.getReference());
				return;
			}
		}

		enter(runtimeBuilderContext);
		super.visitTypeParameterReference(role, parameter);
		exit();

		contexts.peek().addTypeReference(role, typeParameterReference);
	}

	@Override
	public void visitTypeReference(CtRole role, ParameterizedType type) {
		Type[] typeArguments = type.getActualTypeArguments();
		if (role == CtRole.SUPER_TYPE && typeArguments.length > 0) {
			if (hasProcessedRecursiveBound(typeArguments)) {
				return;
			}
		}
		final CtTypeReference<?> ctTypeReference = factory.Core().createTypeReference();
		ctTypeReference.setSimpleName(((Class) type.getRawType()).getSimpleName());
		RuntimeBuilderContext context = new TypeReferenceRuntimeBuilderContext(type, ctTypeReference) {

			@Override
			public void addType(CtType<?> aType) {
				//TODO check if it is needed
				this.getClass();
			}
		};

		enter(context);
		super.visitTypeReference(role, type);
		exit();

		contexts.peek().addTypeReference(role, ctTypeReference);
	}

	@Override
	public void visitTypeReference(CtRole role, WildcardType type) {
		final CtWildcardReference wildcard = factory.Core().createWildcardReference();
		//type.getUpperBounds() returns at least a single value array with Object.class
		//so we cannot distinguish between <? extends Object> and <?>, which must be upper==true too!
		wildcard.setUpper(!(type.getLowerBounds().length > 0));

		if (!type.getUpperBounds()[0].equals(Object.class)) {
			if (hasProcessedRecursiveBound(type.getUpperBounds())) {
				return;
			}
		}
		if (hasProcessedRecursiveBound(type.getLowerBounds())) {
			return;
		}

		enter(new TypeReferenceRuntimeBuilderContext(type, wildcard));
		super.visitTypeReference(role, type);
		exit();

		contexts.peek().addTypeReference(role, wildcard);
	}

	// check if a type parameter that is bounded by some expression involving itself has already been processed
	private boolean hasProcessedRecursiveBound(Type[] types) {
		for (Type type : types) {
			if (type instanceof TypeVariable) {
				TypeVariable t = (TypeVariable) type;
				final CtTypeParameterReference typeParameterReference = factory.Core().createTypeParameterReference();
				typeParameterReference.setSimpleName(t.getName());
				RuntimeBuilderContext runtimeBuilderContext = new TypeReferenceRuntimeBuilderContext(t, typeParameterReference);
				if (contexts.contains(runtimeBuilderContext)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public <T> void visitArrayReference(CtRole role, final Type typeArray) {
		final CtArrayTypeReference<?> arrayTypeReference = factory.Core().createArrayTypeReference();

		enter(new TypeReferenceRuntimeBuilderContext(typeArray, arrayTypeReference) {
			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				switch (role) {
					case DECLARING_TYPE:
						arrayTypeReference.setDeclaringType(typeReference);
						return;
				}
				arrayTypeReference.setComponentType(typeReference);
			}
		});
		super.visitArrayReference(role, typeArray);
		exit();

		contexts.peek().addTypeReference(role, arrayTypeReference);
	}


	@Override
	public <T> void visitTypeReference(CtRole role, Class<T> clazz) {
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(clazz.getSimpleName());

		enter(new TypeReferenceRuntimeBuilderContext(clazz, typeReference));
		super.visitTypeReference(role, clazz);
		exit();

		contexts.peek().addTypeReference(role, typeReference);
	}


	private void setModifier(CtModifiable ctModifiable, int modifiers) {
		if (Modifier.isAbstract(modifiers)) {
			ctModifiable.addModifier(ModifierKind.ABSTRACT);
		}
		if (Modifier.isFinal(modifiers)) {
			ctModifiable.addModifier(ModifierKind.FINAL);
		}
		if (Modifier.isNative(modifiers)) {
			ctModifiable.addModifier(ModifierKind.NATIVE);
		}
		if (Modifier.isPrivate(modifiers)) {
			ctModifiable.addModifier(ModifierKind.PRIVATE);
		}
		if (Modifier.isProtected(modifiers)) {
			ctModifiable.addModifier(ModifierKind.PROTECTED);
		}
		if (Modifier.isPublic(modifiers)) {
			ctModifiable.addModifier(ModifierKind.PUBLIC);
		}
		if (Modifier.isStatic(modifiers)) {
			ctModifiable.addModifier(ModifierKind.STATIC);
		}
		if (Modifier.isStrict(modifiers)) {
			ctModifiable.addModifier(ModifierKind.STRICTFP);
		}
		if (Modifier.isSynchronized(modifiers)) {
			ctModifiable.addModifier(ModifierKind.SYNCHRONIZED);
		}
		if (Modifier.isTransient(modifiers)) {
			ctModifiable.addModifier(ModifierKind.TRANSIENT);
		}
		if (Modifier.isVolatile(modifiers)) {
			ctModifiable.addModifier(ModifierKind.VOLATILE);
		}
	}


	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitRecord(Class<T> clazz) {
		CtRecord ctRecord = factory.Core().createRecord();
		ctRecord.setSimpleName(clazz.getSimpleName());
		setModifier(ctRecord, clazz.getModifiers() & Modifier.classModifiers());

		enter(new TypeRuntimeBuilderContext(clazz, ctRecord) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctRecord.addConstructor((CtConstructor<Object>) ctConstructor);
			}

			@Override
			public void addRecordComponent(CtRecordComponent ctRecordComponent) {
				ctRecord.addRecordComponent(ctRecordComponent);
			}
		});
		super.visitRecord(clazz);
		exit();

		contexts.peek().addType(ctRecord);
	}

	@Override
	public void visitRecordComponent(AnnotatedElement recordComponent) {
		CtRecordComponent ctRecordComponent = factory.Core().createRecordComponent();
		ctRecordComponent.setSimpleName(MethodHandleUtils.getRecordComponentName(recordComponent));
		enter(new RecordComponentRuntimeBuilderContext(ctRecordComponent));
		visitTypeReference(CtRole.TYPE, MethodHandleUtils.getRecordComponentType(recordComponent));

		Arrays.stream(recordComponent.getAnnotations()).forEach(this::visitAnnotation);
		exit();
		contexts.peek().addRecordComponent(ctRecordComponent);
	}

}
