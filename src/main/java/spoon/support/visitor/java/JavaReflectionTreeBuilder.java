/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.visitor.java;

import spoon.reflect.declaration.CtAnnotation;
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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.support.visitor.java.internal.AnnotationRuntimeBuilderContext;
import spoon.support.visitor.java.internal.ExecutableRuntimeBuilderContext;
import spoon.support.visitor.java.internal.PackageRuntimeBuilderContext;
import spoon.support.visitor.java.internal.RuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeReferenceRuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeRuntimeBuilderContext;
import spoon.support.visitor.java.internal.VariableRuntimeBuilderContext;
import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.Deque;

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
	private Deque<RuntimeBuilderContext> contexts = new ArrayDeque<>();
	private Factory factory;

	public JavaReflectionTreeBuilder(Factory factory) {
		this.factory = factory;
	}

	private void enter(RuntimeBuilderContext context) {
		contexts.push(context);
	}

	private RuntimeBuilderContext exit() {
		return contexts.pop();
	}

	public <T, R extends CtType<T>> R scan(Class<T> clazz) {
		CtPackage ctPackage;
		CtType<?> ctEnclosingClass;
		if (clazz.getEnclosingClass() != null) {
			ctEnclosingClass = scan(clazz.getEnclosingClass());
			return ctEnclosingClass.getNestedType(clazz.getSimpleName());
		} else {
			if (clazz.getPackage() == null) {
				ctPackage = factory.Package().getRootPackage();
			} else {
				ctPackage = factory.Package().getOrCreate(clazz.getPackage().getName());
			}
			if (contexts.isEmpty()) {
				enter(new PackageRuntimeBuilderContext(ctPackage));
			}
			if (clazz.isAnnotation()) {
				visitAnnotationClass((Class<Annotation>) clazz);
			} else if (clazz.isInterface()) {
				visitInterface(clazz);
			} else if (clazz.isEnum()) {
				visitEnum(clazz);
			} else {
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

	@Override
	public void visitPackage(Package aPackage) {
		final CtPackage ctPackage = factory.Package().getOrCreate(aPackage.getName());

		enter(new PackageRuntimeBuilderContext(ctPackage));
		super.visitPackage(aPackage);
		exit();

		contexts.peek().addPackage(ctPackage);
	}

	@Override
	public <T> void visitClass(Class<T> clazz) {
		final CtClass ctClass = factory.Core().createClass();
		ctClass.setSimpleName(clazz.getSimpleName());
		setModifier(ctClass, clazz.getModifiers());

		enter(new TypeRuntimeBuilderContext(ctClass) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctClass.addConstructor(ctConstructor);
			}

			@Override
			public void addClassReference(CtTypeReference<?> typeReference) {
				ctClass.setSuperclass(typeReference);
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
		setModifier(ctInterface, clazz.getModifiers());

		enter(new TypeRuntimeBuilderContext(ctInterface) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				super.addMethod(ctMethod);
				ctMethod.setBody(null);
			}
		});
		super.visitInterface(clazz);
		exit();

		contexts.peek().addType(ctInterface);
	}

	@Override
	public <T> void visitEnum(Class<T> clazz) {
		final CtEnum ctEnum = factory.Core().createEnum();
		ctEnum.setSimpleName(clazz.getSimpleName());
		setModifier(ctEnum, clazz.getModifiers());

		enter(new TypeRuntimeBuilderContext(ctEnum) {
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
		setModifier(ctAnnotationType, clazz.getModifiers());

		enter(new TypeRuntimeBuilderContext(ctAnnotationType) {
			@Override
			public void addMethod(CtMethod ctMethod) {
				final CtField<Object> field = factory.Core().createField();
				field.setSimpleName(ctMethod.getSimpleName());
				field.setModifiers(ctMethod.getModifiers());
				field.setType(ctMethod.getType());
				ctAnnotationType.addField(field);
			}
		});
		super.visitAnnotationClass(clazz);
		exit();

		contexts.peek().addType(ctAnnotationType);
	}

	@Override
	public void visitAnnotation(Annotation annotation) {
		final CtAnnotation<Annotation> ctAnnotation = factory.Core().createAnnotation();

		enter(new AnnotationRuntimeBuilderContext(ctAnnotation));
		super.visitAnnotation(annotation);
		exit();

		contexts.peek().addAnnotation(ctAnnotation);
	}

	@Override
	public <T> void visitConstructor(Constructor<T> constructor) {
		final CtConstructor<Object> ctConstructor = factory.Core().createConstructor();
		ctConstructor.setBody(factory.Core().createBlock());
		setModifier(ctConstructor, constructor.getModifiers());

		enter(new ExecutableRuntimeBuilderContext(ctConstructor));
		super.visitConstructor(constructor);
		exit();

		contexts.peek().addConstructor(ctConstructor);
	}

	@Override
	public void visitMethod(RtMethod method) {
		final CtMethod<Object> ctMethod = factory.Core().createMethod();
		ctMethod.setSimpleName(method.getName());
		ctMethod.setBody(factory.Core().createBlock());
		setModifier(ctMethod, method.getModifiers());

		enter(new ExecutableRuntimeBuilderContext(ctMethod));
		super.visitMethod(method);
		exit();

		contexts.peek().addMethod(ctMethod);
	}

	@Override
	public void visitField(Field field) {
		final CtField<Object> ctField = factory.Core().createField();
		ctField.setSimpleName(field.getName());
		setModifier(ctField, field.getModifiers());

		enter(new VariableRuntimeBuilderContext(ctField));
		super.visitField(field);
		exit();

		contexts.peek().addField(ctField);
	}

	@Override
	public void visitEnumValue(Field field) {
		final CtEnumValue<Object> ctEnumValue = factory.Core().createEnumValue();
		ctEnumValue.setSimpleName(field.getName());

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

		enter(new VariableRuntimeBuilderContext(ctParameter));
		super.visitParameter(parameter);
		exit();

		contexts.peek().addParameter(ctParameter);
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter) {
		final CtTypeParameter typeParameter = factory.Core().createTypeParameter();
		typeParameter.setSimpleName(parameter.getName());

		enter(new TypeRuntimeBuilderContext(typeParameter));
		super.visitTypeParameter(parameter);
		exit();

		contexts.peek().addFormalType(typeParameter);
	}

	@Override
	public void visitType(Type type) {
		final CtTypeReference<?> ctTypeReference = factory.Core().createTypeReference();
		ctTypeReference.setSimpleName(getTypeName(type));

		enter(new TypeReferenceRuntimeBuilderContext(ctTypeReference));
		super.visitType(type);
		exit();

		contexts.peek().addTypeName(ctTypeReference);
	}

	@Override
	public void visitType(ParameterizedType type) {
		final CtTypeReference<?> ctTypeReference = factory.Core().createTypeReference();

		enter(new TypeReferenceRuntimeBuilderContext(ctTypeReference) {
			@Override
			public void addClassReference(CtTypeReference<?> typeReference) {
				ctTypeReference.setSimpleName(typeReference.getSimpleName());
				ctTypeReference.setPackage(typeReference.getPackage());
				ctTypeReference.setDeclaringType(typeReference.getDeclaringType());
				ctTypeReference.setActualTypeArguments(typeReference.getActualTypeArguments());
				ctTypeReference.setAnnotations(typeReference.getAnnotations());
			}

			@Override
			public void addType(CtType<?> aType) {
			}
		});
		super.visitType(type);
		exit();

		contexts.peek().addTypeName(ctTypeReference);
	}

	@Override
	public void visitType(WildcardType type) {
		final CtWildcardReference wildcard = factory.Core().createWildcardReference();
		wildcard.setUpper(type.getUpperBounds() != null && !type.getUpperBounds()[0].equals(Object.class));

		enter(new TypeReferenceRuntimeBuilderContext(wildcard));
		super.visitType(type);
		exit();

		contexts.peek().addTypeName(wildcard);
	}

	private String getTypeName(Type type) {
		if (!(type instanceof Class)) {
			return type.toString();
		}
		Class clazz = (Class) type;
		if (clazz.isArray()) {
			try {
				Class<?> cl = clazz;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				StringBuilder sb = new StringBuilder();
				sb.append(cl.getName());
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) { /*FALLTHRU*/ }
		}
		return clazz.getName();
	}

	@Override
	public <T> void visitArrayReference(final Class<T> typeArray) {
		final CtArrayTypeReference<?> arrayTypeReference = factory.Core().createArrayTypeReference();

		enter(new TypeReferenceRuntimeBuilderContext(arrayTypeReference) {
			@Override
			public void addClassReference(CtTypeReference<?> typeReference) {
				if (typeArray.getSimpleName().equals(typeReference.getSimpleName())) {
					arrayTypeReference.setComponentType(typeReference);
				} else {
					arrayTypeReference.setDeclaringType(typeReference);
				}
			}

			@Override
			public void addArrayReference(CtArrayTypeReference<?> typeReference) {
				arrayTypeReference.setComponentType(typeReference);
			}
		});
		super.visitArrayReference(typeArray);
		exit();

		contexts.peek().addArrayReference(arrayTypeReference);
	}

	@Override
	public <T> void visitClassReference(Class<T> clazz) {
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(clazz.getSimpleName());

		enter(new TypeReferenceRuntimeBuilderContext(typeReference));
		super.visitClassReference(clazz);
		exit();

		contexts.peek().addClassReference(typeReference);
	}

	@Override
	public <T> void visitInterfaceReference(Class<T> anInterface) {
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName(anInterface.getSimpleName());

		enter(new TypeReferenceRuntimeBuilderContext(typeReference));
		super.visitInterfaceReference(anInterface);
		exit();

		contexts.peek().addInterfaceReference(typeReference);
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
}
