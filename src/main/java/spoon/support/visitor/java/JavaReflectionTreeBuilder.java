/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.*;
import spoon.support.util.RtHelper;
import spoon.support.visitor.java.internal.*;
import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;

import java.lang.annotation.Annotation;
import java.lang.module.ModuleDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
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
		if (clazz.getEnclosingClass() != null && !clazz.isAnonymousClass()) {
			CtType<?> ctEnclosingClass = factory.Type().get(clazz.getEnclosingClass());
			return ctEnclosingClass.getNestedType(clazz.getSimpleName());
		} else {
			CtPackage ctPackage = clazz.getPackage() != null ? factory.Package().getOrCreate(clazz.getPackage().getName())
					: factory.Package().getRootPackage();
			if(contexts.isEmpty()){
				contexts.add(new PackageRuntimeBuilderContext(ctPackage));
			}

			if (clazz.isAnnotation()) {
				visitAnnotationClass((Class<Annotation>) clazz);
			}else if (clazz.isInterface()) {
				visitInterface(clazz);
			}else if (clazz.isEnum()) {
				visitEnum(clazz);
			}else if (MethodHandleUtils.isRecord(clazz)) {
				visitRecord(clazz);
			}else {
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
	public void visitModule(Module module) {
		CtModule know = factory.Module().getModule(module.getName());
		if (know != null && know.isAttributed()) {
			return;
		}

		CtModule fresh = know != null ? know : factory.Module().getOrCreate(module.getName());
		ModuleDescriptor descriptor = module.getDescriptor();
		if(descriptor != null){
			attributeModule(fresh, descriptor);
		}

		fresh.setIsAttributed(true);
		enter(new ModuleRuntimeBuilderContext(fresh));
		super.visitModule(module);
		exit();
	}

	private void attributeModule(CtModule fresh, ModuleDescriptor descriptor) {
		fresh.setIsOpenModule(descriptor.isOpen());
		fresh.setIsAutomatic(descriptor.isAutomatic());

		List<CtModuleRequirement> requires = descriptor.requires().stream().map(this::createRequires).collect(Collectors.toUnmodifiableList());
		fresh.setRequiredModules(requires);

		List<CtPackageExport> exports = descriptor.exports().stream().map(instruction -> createExport(instruction.source(), instruction.targets(), false)).collect(Collectors.toUnmodifiableList());
		fresh.setExportedPackages(exports);

		List<CtPackageExport> opens = descriptor.opens().stream().map(instruction -> createExport(instruction.source(), instruction.targets(), true)).collect(Collectors.toUnmodifiableList());
		fresh.setOpenedPackages(opens);

		List<CtProvidedService> provides = descriptor.provides().stream().map(this::createProvides).collect(Collectors.toUnmodifiableList());
		fresh.setProvidedServices(provides);

		List<CtUsedService> uses = descriptor.uses().stream().map(this::createUses).collect(Collectors.toUnmodifiableList());
		fresh.setUsedServices(uses);
	}

	private CtModuleRequirement createRequires(ModuleDescriptor.Requires instruction) {
		CtModuleReference requiredModule = factory.Module().createReference(instruction.name());
		Set<CtModuleRequirement.RequiresModifier> modifiers = new HashSet<>();
		if(instruction.modifiers().contains(ModuleDescriptor.Requires.Modifier.STATIC)){
			modifiers.add(CtModuleRequirement.RequiresModifier.STATIC);
		}

		if(instruction.modifiers().contains(ModuleDescriptor.Requires.Modifier.TRANSITIVE)){
			modifiers.add(CtModuleRequirement.RequiresModifier.TRANSITIVE);
		}

		CtModuleRequirement requires = factory.Core().createModuleRequirement();
		requires.setModuleReference(requiredModule);
		requires.setRequiresModifiers(modifiers);
		return requires;
	}

	private CtUsedService createUses(String used) {
		CtTypeReference usedType = factory.Type().createReference(used);
		CtUsedService usedService = factory.Core().createUsedService();
		usedService.setServiceType(usedType);
		return usedService;
	}

	private CtProvidedService createProvides(ModuleDescriptor.Provides instruction) {
		CtTypeReference serviceType = factory.Type().createReference(instruction.service());
		List<CtTypeReference> serviceImplementations = instruction.providers().stream().map(factory.Type()::createReference).collect(Collectors.toUnmodifiableList());
		CtProvidedService export = factory.Core().createProvidedService();
		export.setServiceType(serviceType);
		export.setImplementationTypes(serviceImplementations);
		return export;
	}

	private CtPackageExport createExport(String instruction, Set<String> instructions, boolean openedPackage) {
		CtPackageReference exported = factory.Package().createReference(instruction);
		List<CtModuleReference> targets = instructions.stream().map(factory.Module()::createReference).collect(Collectors.toUnmodifiableList());
		CtPackageExport export = factory.Core().createPackageExport();
		export.setOpenedPackage(openedPackage);
		export.setPackageReference(exported);
		export.setTargetExport(targets);
		return export;
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
		setModifier(ctClass, clazz.getModifiers(), clazz.getDeclaringClass());

		enter(new TypeRuntimeBuilderContext(clazz, ctClass) {
			@Override
			public void addConstructor(CtConstructor<?> ctConstructor) {
				ctClass.addConstructor(ctConstructor);
			}

			@Override
			public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
				if (role == CtRole.SUPER_TYPE) {
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
		setModifier(ctInterface, clazz.getModifiers(), clazz.getDeclaringClass());

		enter(new TypeRuntimeBuilderContext(clazz, ctInterface));
		super.visitInterface(clazz);
		exit();

		contexts.peek().addType(ctInterface);
	}

	@Override
	public <T> void visitEnum(Class<T> clazz) {
		final CtEnum ctEnum = factory.Core().createEnum();
		ctEnum.setSimpleName(clazz.getSimpleName());
		setModifier(ctEnum, clazz.getModifiers(), clazz.getDeclaringClass());

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
		setModifier(ctAnnotationType, clazz.getModifiers(), clazz.getDeclaringClass());

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
		setModifier(ctConstructor, constructor.getModifiers(), constructor.getDeclaringClass());

		enter(new ExecutableRuntimeBuilderContext(constructor, ctConstructor));
		super.visitConstructor(constructor);
		exit();

		contexts.peek().addConstructor(ctConstructor);
	}

	@Override
	public void visitMethod(RtMethod method, Annotation parent) {
		final CtMethod<Object> ctMethod = factory.Core().createMethod();
		ctMethod.setSimpleName(method.getName());
		// java 8 static interface methods are marked as abstract but has body
		if (!Modifier.isAbstract(method.getModifiers())) {
			ctMethod.setBody(factory.Core().createBlock());
		}
		setModifier(ctMethod, method.getModifiers(), method.getDeclaringClass());
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
		setModifier(ctField, field.getModifiers(), field.getDeclaringClass());

		// we set the value of the shadow field if it is a public and static primitive value
		try {
			Set<ModifierKind> modifiers = RtHelper.getModifiers(field.getModifiers());
			if (modifiers.contains(ModifierKind.STATIC)
					&& modifiers.contains(ModifierKind.PUBLIC)
					&& (field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType()))) {
				CtLiteral<Object> defaultExpression = factory.createLiteral(field.get(null));
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

	@Override
	public void visitEnumValue(Field field) {
		final CtEnumValue<Object> ctEnumValue = factory.Core().createEnumValue();
		ctEnumValue.setSimpleName(field.getName());
		setModifier(ctEnumValue, field.getModifiers(), field.getDeclaringClass());

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
		wildcard.setUpper((type.getLowerBounds().length > 0) == false);

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


	private void setModifier(CtModifiable ctModifiable, int modifiers, Class<?> declaringClass) {
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
			if (ctModifiable instanceof CtField) {
				ctModifiable.addModifier(ModifierKind.TRANSIENT);
			} else if (ctModifiable instanceof CtExecutable) {
				//it happens when executable has a vararg parameter. But that is not handled by modifiers in Spoon model
//				ctModifiable.addModifier(ModifierKind.VARARG);
			} else {
				throw new UnsupportedOperationException();
			}
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
		setModifier(ctRecord, clazz.getModifiers(), clazz.getDeclaringClass());

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
