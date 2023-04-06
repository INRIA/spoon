/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.adaption.TypeAdaptor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.DECLARING_TYPE;
import static spoon.reflect.path.CtRole.IS_SHADOW;
import static spoon.reflect.path.CtRole.PACKAGE_REF;
import static spoon.reflect.path.CtRole.TYPE_ARGUMENT;

public class CtTypeReferenceImpl<T> extends CtReferenceImpl implements CtTypeReference<T> {
	private static final long serialVersionUID = 1L;

	// We use thread-local storage for the caching to avoid having to lock when doing cache invalidation and lookup.
	// See https://github.com/INRIA/spoon/issues/4668 for details.
	private static final ThreadLocal<Map<String, Class<?>>> classByQName = ThreadLocal.withInitial(HashMap::new);
	private static final ThreadLocal<ClassLoader> lastClassLoader = new ThreadLocal<>();

	@MetamodelPropertyField(role = TYPE_ARGUMENT)
	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = DECLARING_TYPE)
	CtTypeReference<?> declaringType;

	@MetamodelPropertyField(role = PACKAGE_REF)
	private CtPackageReference pack;



	public CtTypeReferenceImpl() {
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeReference(this);
	}

	@Override
	public CtTypeReference<?> box() {
		if (!isPrimitive()) {
			return this;
		}
		if ("int".equals(getSimpleName())) {
			return getFactory().Type().createReference(Integer.class);
		}
		if ("float".equals(getSimpleName())) {
			return getFactory().Type().createReference(Float.class);
		}
		if ("long".equals(getSimpleName())) {
			return getFactory().Type().createReference(Long.class);
		}
		if ("char".equals(getSimpleName())) {
			return getFactory().Type().createReference(Character.class);
		}
		if ("double".equals(getSimpleName())) {
			return getFactory().Type().createReference(Double.class);
		}
		if ("boolean".equals(getSimpleName())) {
			return getFactory().Type().createReference(Boolean.class);
		}
		if ("short".equals(getSimpleName())) {
			return getFactory().Type().createReference(Short.class);
		}
		if ("byte".equals(getSimpleName())) {
			return getFactory().Type().createReference(Byte.class);
		}
		if ("void".equals(getSimpleName())) {
			return getFactory().Type().createReference(Void.class);
		}
		return this;
	}

	@Override

	public Class<T> getActualClass() {
			return getPrimitiveType(this).orElseGet(this::findClass);
	}

	@SuppressWarnings("unchecked")
	private Optional<Class<T>> getPrimitiveType(CtTypeReference<?> typeReference) {
		switch (typeReference.getSimpleName()) {
			case "boolean":
				return Optional.of((Class<T>) boolean.class);
			case "byte":
				return Optional.of((Class<T>) byte.class);
			case "double":
				return Optional.of((Class<T>) double.class);
			case "int":
				return Optional.of((Class<T>) int.class);
			case "short":
				return Optional.of((Class<T>) short.class);
			case "char":
				return Optional.of((Class<T>) char.class);
			case "long":
				return Optional.of((Class<T>) long.class);
			case "float":
				return Optional.of((Class<T>) float.class);
			case "void":
				return Optional.of((Class<T>) void.class);
			default:
				return Optional.empty();
		}
	}


	/**
	 * Finds the class requested in {@link #getActualClass()}.
	 *
	 * Looks for the class in the standard Java classpath, but also in the sourceClassPath given as option.
	 */
	protected Class<T> findClass() {
		CtTypeReference<?> typeReference = this;
		if (isArray()) {
			CtTypeReference<?> componentTypeReference = convertToComponentType();
			if (componentTypeReference.isPrimitive()) {
				return getPrimitiveType(componentTypeReference)
					.map(this::asArrayTypeWithOurDimensions)
					.orElseThrow(() -> new SpoonException("Cant find primitive type: " + componentTypeReference));
			}
			typeReference = componentTypeReference;
		}
		Class<T> actualClass = getClassFromThreadLocalCacheOrLoad(typeReference);
		return isArray() ? asArrayTypeWithOurDimensions(actualClass) : actualClass;
	}

	@SuppressWarnings("unchecked")
	private Class<T> getClassFromThreadLocalCacheOrLoad(CtTypeReference<?> typeReference) {
		ClassLoader classLoader = getFactory().getEnvironment().getInputClassLoader();
		checkCacheIntegrity(classLoader);
		String qualifiedName = typeReference.getQualifiedName();
		return (Class<T>) classByQName.get().computeIfAbsent(qualifiedName, key -> loadClassWithQName(classLoader, qualifiedName));
	}

	/**
	 * Converts the type reference to its component type. If the type is no array, the type reference is returned.
	 * @return  the component type of the type reference.
	 */
	private CtTypeReference<T> convertToComponentType() {
		if (!this.getQualifiedName().contains("[")) {
			return this;
		}
		return getFactory().createReference(this.getQualifiedName().substring(0, this.getQualifiedName().indexOf("[")));
	}

	private Class<?> loadClassWithQName(ClassLoader classLoader, String qualifiedName) {
			try {
				return classLoader.loadClass(qualifiedName);
			} catch (Throwable e) {
				throw new SpoonClassNotFoundException("cannot load class: " + qualifiedName, e);
			}
	}

	/**
	 * Checks if the given classloader is the same as the one used in the last call to {@link #findClass()}.
	 * If not, the cache is cleared.
	 * @param classLoader  the classloader to check against the old one.
	 */
	private void checkCacheIntegrity(ClassLoader classLoader) {
		if (classLoader != lastClassLoader.get()) {
			//clear cache because class loader changed
			classByQName.get().clear();
			lastClassLoader.set(classLoader);
		}
	}

	/**
	 * Converts the given type to an array type.
	 * @param clazz  the type to convert.
	 * @return  the array type.
	 */
	@SuppressWarnings("unchecked")
	private <R> Class<R> asArrayTypeWithOurDimensions(Class<R> clazz) {
		String simpleName = getSimpleName();
		int dimensionCount = (simpleName.length() - simpleName.indexOf('[')) / 2;
		int[] dimensions = new int[dimensionCount];
		return (Class<R>) Array.newInstance(clazz, dimensions).getClass();
	}

	@Override
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return actualTypeArguments;
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return getActualClass();
	}

	@Override
	public CtType<T> getDeclaration() {
		return getFactory().Type().get(getQualifiedName());
	}

	@Override
	public CtType<T> getTypeDeclaration() {
		CtType<T> t = getFactory().Type().get(getQualifiedName());
		if (t != null) {
			return t;
		}
		try {
			return getFactory().Type().get(getActualClass());
		} catch (SpoonClassNotFoundException e) {
			// this only happens in noclasspath
			return null;
		}
	}

	@Override
	public CtTypeReference<?> getDeclaringType() {
		return declaringType;
	}

	@Override
	public CtPackageReference getPackage() {
		return pack;
	}

	@Override
	public String getQualifiedName() {
		if (getDeclaringType() != null) {
			return getDeclaringType().getQualifiedName() + CtType.INNERTTYPE_SEPARATOR + getSimpleName();
		} else if (getPackage() != null && !getPackage().isUnnamedPackage()) {
			return getPackage().getSimpleName() + CtPackage.PACKAGE_SEPARATOR + getSimpleName();
		} else {
			return getSimpleName();
		}
	}

	@Override
	public boolean isPrimitive() {
		return getPrimitiveType(this).isPresent();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		if (type instanceof CtTypeParameterReference) {
			return false;
		}
		if (NULL_TYPE_NAME.equals(getSimpleName()) || NULL_TYPE_NAME.equals(type.getSimpleName())) {
			return false;
		}
		if (isPrimitive() || type.isPrimitive()) {
			return equals(type);
		}
		if (this instanceof CtArrayTypeReference) {
			if (type instanceof CtArrayTypeReference) {
				return ((CtArrayTypeReference<?>) this).getComponentType().isSubtypeOf(((CtArrayTypeReference<?>) type).getComponentType());
			}
			if (Array.class.getName().equals(type.getQualifiedName())) {
				return true;
			}
		}
		if (Object.class.getName().equals(type.getQualifiedName())) {
			//everything is a sub type of Object
			return true;
		}
		return new TypeAdaptor(this).isSubtypeOf(type);
	}

	/**
	 * Detects if this type is an code responsible for implementing of that type.<br>
	 * In means it detects whether this type can access protected members of that type
	 * @return true if this type or any declaring type recursively is subtype of type or directly is the type.
	 */
	private boolean isImplementationOf(CtTypeReference<?> type) {
		CtTypeReference<?> impl = this;
		while (impl != null) {
			if (impl.isSubtypeOf(type)) {
				return true;
			}
			impl = impl.getDeclaringType();
		}
		return false;
	}

	@Override
	public <C extends CtActualTypeContainer> C setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		if (actualTypeArguments == null || actualTypeArguments.isEmpty()) {
			this.actualTypeArguments = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.actualTypeArguments = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, TYPE_ARGUMENT, this.actualTypeArguments, new ArrayList<>(this.actualTypeArguments));
		this.actualTypeArguments.clear();
		for (CtTypeReference<?> actualTypeArgument : actualTypeArguments) {
			addActualTypeArgument(actualTypeArgument);
		}
		return (C) this;
	}

	@Override
	public <C extends CtTypeReference<T>> C setDeclaringType(CtTypeReference<?> declaringType) {
		if (declaringType != null) {
			declaringType.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DECLARING_TYPE, declaringType, this.declaringType);
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtTypeReference<T>> C setPackage(CtPackageReference pack) {
		if (pack != null) {
			pack.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, PACKAGE_REF, pack, this.pack);
		this.pack = pack;
		return (C) this;
	}

	@Override
	public CtIntersectionTypeReference<T> asCtIntersectionTypeReference() {
		return (CtIntersectionTypeReference<T>) this;
	}

	@Override
	public CtTypeReference<?> unbox() {
		if (isPrimitive()) {
			return this;
		}
		Class<T> actualClass;
		try {
			actualClass = getActualClass();
		} catch (SpoonClassNotFoundException e) {
			return this;
		}
		if (actualClass == Integer.class) {
			return getFactory().Type().createReference(int.class);
		}
		if (actualClass == Float.class) {
			return getFactory().Type().createReference(float.class);
		}
		if (actualClass == Long.class) {
			return getFactory().Type().createReference(long.class);
		}
		if (actualClass == Character.class) {
			return getFactory().Type().createReference(char.class);
		}
		if (actualClass == Double.class) {
			return getFactory().Type().createReference(double.class);
		}
		if (actualClass == Boolean.class) {
			return getFactory().Type().createReference(boolean.class);
		}
		if (actualClass == Short.class) {
			return getFactory().Type().createReference(short.class);
		}
		if (actualClass == Byte.class) {
			return getFactory().Type().createReference(byte.class);
		}
		if (actualClass == Void.class) {
			return getFactory().Type().createReference(void.class);
		}
		return this;
	}

	@Override
	public Collection<CtFieldReference<?>> getDeclaredFields() {
		CtType<?> t = getTypeDeclaration();
		if (t != null) {
			return t.getDeclaredFields();
		}
		return Collections.emptyList();
	}

	private void handleParentNotFound(SpoonClassNotFoundException cnfe) {
		String msg = "cannot load class: " + getQualifiedName() + " with class loader "
				+ Thread.currentThread().getContextClassLoader();
		if (getFactory().getEnvironment().getNoClasspath()) {
			// should not be thrown in 'noClasspath' environment (#775)
			Launcher.LOGGER.warn(msg);
		} else {
			throw cnfe;
		}
	}

	@Override
	public CtFieldReference<?> getDeclaredField(String name) {
		if (name == null) {
			return null;
		}
		CtType<?> t = getTypeDeclaration();
		if (t != null) {
			return t.getDeclaredField(name);
		}
		return null;
	}

	@Override
	public CtFieldReference<?> getDeclaredOrInheritedField(String fieldName) {
		CtType<?> t = getTypeDeclaration();
		if (t != null) {
			return t.getDeclaredOrInheritedField(fieldName);
		}
		return null;
	}


	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		CtType<T> t = getTypeDeclaration();
		if (t == null) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				return Collections.emptyList();
			} else {
				throw new SpoonException("Type not found " + getQualifiedName());
			}
		} else {
			return t.getDeclaredExecutables();
		}
	}

	@Override
	public Collection<CtFieldReference<?>> getAllFields() {
		CtType<?> t = getTypeDeclaration();
		if (t != null) {
			return t.getAllFields();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Collection<CtExecutableReference<?>> l = new ArrayList<>();
		CtType<T> t = getTypeDeclaration();
		if (t != null) {
			l.addAll(t.getAllExecutables());
		}
		return l;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		CtType<T> t = getTypeDeclaration();
		if (t != null) {
			return t.getModifiers();
		}
		if (getFactory().getEnvironment().getNoClasspath()) {
			return Collections.emptySet();
		}
		throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		CtType<T> t = getTypeDeclaration();
		if (t != null) {
			return t.getSuperclass();
		}
		return null;
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		//we need a interface type references whose parent is connected to CtType, otherwise TypeParameterReferences cannot be resolved well
		CtType<?> t = getTypeDeclaration();
		if (t != null) {
			return Collections.unmodifiableSet(t.getSuperInterfaces());
		}
		if (getFactory().getEnvironment().getNoClasspath()) {
			return Collections.emptySet();
		}
		throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
	}

	@Override
	public boolean isAnonymous() {
		try {
			Integer.parseInt(getSimpleName());
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isLocalType() {
		CtType<T> declaration = this.getDeclaration();
		if (declaration != null) {
			return declaration.isLocalType();
		}
		// A local type doesn't have a fully qualified name but have an identifier
		// to know which is the local type member wanted by the developer.
		// Oracle documentation: https://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.7
		// JDT documentation: http://help.eclipse.org/juno/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/ITypeBinding.html#getQualifiedName()
		String name = getSimpleName();
		if (name.isEmpty() || !Character.isDigit(name.charAt(0))) {
			return false;
		}
		// first char has to be a digit, everything else just needs to be a valid
		// java identifier part and at least one non-digit. The validity is already covered by
		// setSimpleName, so we just need to look for that non-digit char
		for (int i = 1; i < name.length(); i++) {
			if (!Character.isDigit(name.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <C extends CtActualTypeContainer> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArgument == null) {
			return (C) this;
		}
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			actualTypeArguments = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		actualTypeArgument.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, TYPE_ARGUMENT, this.actualTypeArguments, actualTypeArgument);
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		if (actualTypeArguments == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, TYPE_ARGUMENT, actualTypeArguments, actualTypeArguments.indexOf(actualTypeArgument), actualTypeArgument);
		return actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public boolean isClass() {
		CtType<T> t = getTypeDeclaration();

		if (t == null) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				return false;
			} else {
				throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
			}
		} else {
			return t.isClass();
		}
	}

	@Override
	public boolean isInterface() {
		CtType<T> t = getTypeDeclaration();
		if (t == null) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				return false;
			} else {
				throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
			}
		} else {
			return t.isInterface();
		}
	}

	@Override
	public boolean isAnnotationType() {
		CtType<T> t = getTypeDeclaration();
		if (t == null) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				return false;
			} else {
				throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
			}
		} else {
			return t.isAnnotationType();
		}
	}

	@Override
	public boolean isEnum() {
		CtType<T> t = getTypeDeclaration();
		if (t == null) {
			if (getFactory().getEnvironment().getNoClasspath()) {
				return false;
			} else {
				throw new SpoonClassNotFoundException(getQualifiedName() + " cannot be found");
			}
		} else {
			return t.isEnum();
		}
	}

	@Override
	public boolean canAccess(CtTypeReference<?> type) {
		try {
			if (type == null) {
				//noclasspath mode
				return true;
			}
			if (type.getTypeDeclaration() == null) {
				return true;
			}
			Set<ModifierKind> modifiers = type.getModifiers();

			if (modifiers.contains(ModifierKind.PUBLIC)) {
				return true;
			}
			if (modifiers.contains(ModifierKind.PROTECTED)) {
				//the accessed type is protected in scope of declaring type.
				CtTypeReference<?> declaringType = type.getDeclaringType();
				if (declaringType == null) {
					//top level type cannot be protected. So this is a model inconsistency.
					throw new SpoonException("The protected class " + type.getQualifiedName() + " has no declaring class.");
				}
				if (isImplementationOf(declaringType)) {
					//type is visible in code which implements declaringType
					return true;
				} //else it is visible in same package, like package protected
				return isInSamePackage(type);
			}
			if (modifiers.contains(ModifierKind.PRIVATE)) {
				//it is visible in scope of the same class only
				return type.getTopLevelType().getQualifiedName().equals(this.getTopLevelType().getQualifiedName());
			}
			/*
			 * no modifier, we have to check if it is nested type and if yes, if parent is interface or class.
			 * In case of no parent then implicit access is package protected
			 * In case of parent is interface, then implicit access is PUBLIC
			 * In case of parent is class, then implicit access is package protected
			 */
			CtTypeReference<?> declaringTypeRef = type.getDeclaringType();
			if (declaringTypeRef != null && declaringTypeRef.isInterface()) {
				//the declaring type is interface, then implicit access is PUBLIC
				return true;
			}
			//package protected
			//visible only in scope of the same package
			return isInSamePackage(type);
		} catch (SpoonClassNotFoundException e) {
			handleParentNotFound(e);
			//if the modifiers cannot be resolved then we expect that it is visible
			return true;
		}
	}

	@Override
	public boolean canAccess(CtTypeMember typeMember) {
		CtType<?> declaringType = typeMember.getDeclaringType();
		if (declaringType == null) {
			//noclasspath mode
			return true;
		}
		CtTypeReference<?> declaringTypeRef = declaringType.getReference();
		if (!canAccess(declaringTypeRef)) {
			return false;
		}
		Set<ModifierKind> modifiers = typeMember.getModifiers();

		if (modifiers.contains(ModifierKind.PUBLIC)) {
			return true;
		}
		if (modifiers.contains(ModifierKind.PROTECTED)) {
			if (isImplementationOf(declaringTypeRef)) {
				//type is visible in code which implements declaringType
				return true;
			} //else it is visible in same package, like package protected
			return isInSamePackage(declaringTypeRef);
		}
		if (modifiers.contains(ModifierKind.PRIVATE)) {
			//it is visible in scope of the same class only
			return declaringType.getTopLevelType().getQualifiedName().equals(this.getTopLevelType().getQualifiedName());
		}
		/*
		 * no modifier, we have to check if it is nested type and if yes, if parent is interface or class.
		 * In case of no parent then implicit access is package protected
		 * In case of parent is interface, then implicit access is PUBLIC
		 * In case of parent is class, then implicit access is package protected
		 */
		CtType<?> declaringTypeDeclaringType = declaringType.getDeclaringType();
		if (declaringTypeDeclaringType != null && declaringTypeDeclaringType.isInterface()) {
			//the declaring type is interface, then implicit access is PUBLIC
			return true;
		}
		//package protected
		//visible only in scope of the same package
		return isInSamePackage(declaringTypeRef);
	}

	private boolean isInSamePackage(CtTypeReference<?> type) {
		CtPackageReference thisPackage = this.getTopLevelType().getPackage();
		CtPackageReference otherPackage = type.getTopLevelType().getPackage();
		if (thisPackage == null || otherPackage == null) {
			return true;
		}
		return thisPackage.getQualifiedName().equals(otherPackage.getQualifiedName());
	}

	@Override
	public CtTypeReference<?> getTopLevelType() {
		CtTypeReference<?> type = this;
		while (true) {
			CtTypeReference<?> parentType = type.getDeclaringType();
			if (parentType == null) {
				return type;
			}
			type = parentType;
		}
	}

	@Override
	public CtTypeReference<?> getAccessType() {
		CtTypeReference<?> declType = this.getDeclaringType();
		if (declType == null) {
			throw new SpoonException("The declaring type is expected, but " + getQualifiedName() + " is top level type");
		}
		CtType<?> contextType = getParent(CtType.class);
		if (contextType == null) {
			return declType;
		}
		CtTypeReference<?> contextTypeRef = contextType.getReference();
		if (contextTypeRef != null && !contextTypeRef.canAccess(declType)) {
			//search for visible declaring type
			CtTypeReference<?> visibleDeclType = null;
			CtTypeReference<?> type = contextTypeRef;
			//search which type or declaring type of startType extends from nestedType
			while (visibleDeclType == null && type != null) {
				visibleDeclType = getLastVisibleSuperClassExtendingFrom(type, declType);
				if (visibleDeclType != null) {
					//found one!
					applyActualTypeArguments(visibleDeclType, declType);
					break;
				}
				//try class hierarchy of declaring type
				type = type.getDeclaringType();
			}
			declType = visibleDeclType;
		}
		if (declType == null) {
			throw new SpoonException("Cannot compute access path to type: " + this.getQualifiedName() + " in context of type: " + contextType.getQualifiedName());
		}
		return declType;
	}

	/**
	 * adds the actualTypeArguments of sourceTypeRef to targetTypeRef. Type of targetTypeRef extends from type of sourceTypeRef
	 * @param targetTypeRef
	 * @param sourceTypeRef
	 */
	private static void applyActualTypeArguments(CtTypeReference<?> targetTypeRef, CtTypeReference<?> sourceTypeRef) {
		CtTypeReference<?> targetDeclType = targetTypeRef.getDeclaringType();
		CtTypeReference<?> sourceDeclType = sourceTypeRef.getDeclaringType();
		if (targetDeclType != null && sourceDeclType != null && targetDeclType.isSubtypeOf(sourceDeclType)) {
			applyActualTypeArguments(targetDeclType, sourceDeclType);
		}
		if (!targetTypeRef.isSubtypeOf(sourceTypeRef)) {
			throw new SpoonException("Invalid arguments. targetTypeRef " + targetTypeRef.getQualifiedName() + " must be a sub type of sourceTypeRef " + sourceTypeRef.getQualifiedName());
		}
		List<CtTypeReference<?>> newTypeArgs = new ArrayList<>();
		/*
		 * for now simply copy the type arguments, to have it fixed fast. But it is not correct!
		 *
		 * For example in this case
		 *
		 * class A<T,K>{}
		 *
		 * class B<U,T> extends A<T,Integer>
		 *
		 * The sourceTypeRef: A<T,K>
		 * has to be applied to
		 * targetTypeRef: B<?,T>
		 */
		for (CtTypeReference<?> l_tr : sourceTypeRef.getActualTypeArguments()) {
			newTypeArgs.add(l_tr.clone());
		}
		targetTypeRef.setActualTypeArguments(newTypeArgs);
	}

	/**
	 *
	 * @param sourceType
	 * @param targetType
	 * @return sourceType or last super class of sourceType, which extends from targetType and which is visible from sourceType or null if sourceType does not extends from targetType
	 */
	private static CtTypeReference<?> getLastVisibleSuperClassExtendingFrom(CtTypeReference<?> sourceType, CtTypeReference<?> targetType) {
		String targetQN = targetType.getQualifiedName();
		CtTypeReference<?> adept = sourceType;
		CtTypeReference<?> type = sourceType;
		while (true) {
			if (targetQN.equals(type.getQualifiedName())) {
				return adept;
			}
			type = type.getSuperclass();
			if (type == null) {
				//there is no super type which extends from targetType
				return null;
			}
			if (sourceType.canAccess(type)) {
				//this super type is still visible. It is adept for returning
				adept = type;
			}
		}
	}

	@MetamodelPropertyField(role = IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtTypeReference<T> clone() {
		return (CtTypeReference<T>) super.clone();
	}

	@Override
	public CtTypeParameter getTypeParameterDeclaration() {

		CtElement parent = this.getParent();

		// case 1: this is an actual type argument of a type reference eg List<E>
		if (parent instanceof CtTypeReference) {
			CtType t = ((CtTypeReference) parent).getTypeDeclaration();
			return findTypeParamDeclarationByPosition(t, ((CtTypeReference) parent).getActualTypeArguments().indexOf(this));
		}

		// case 2: this is an actual type argument of a method/constructor reference
		if (parent instanceof CtExecutableReference) {
			CtExecutable<?> exec = ((CtExecutableReference<?>) parent).getExecutableDeclaration();
			if (exec instanceof CtMethod || exec instanceof CtConstructor) {
				int idx = ((CtExecutableReference) parent).getActualTypeArguments().indexOf(this);
				return idx >= 0 ? findTypeParamDeclarationByPosition((CtFormalTypeDeclarer) exec, idx) : null;
			}
		}

		if (parent instanceof CtFormalTypeDeclarer) {
			CtFormalTypeDeclarer exec = (CtFormalTypeDeclarer) parent;
			if (exec instanceof CtMethod || exec instanceof CtConstructor) {
				for (CtTypeParameter typeParam : exec.getFormalCtTypeParameters()) {
					if (typeParam.getSimpleName().equals(getSimpleName())) {
						return typeParam;
					}
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean isGenerics() {
		if (getDeclaration() instanceof CtTypeParameter) {
			return true;
		}
		for (CtTypeReference ref : getActualTypeArguments()) {
			if (ref.isGenerics()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isParameterized() {
		return !getActualTypeArguments().isEmpty();
	}

	private CtTypeParameter findTypeParamDeclarationByPosition(CtFormalTypeDeclarer type, int position) {
		return type.getFormalCtTypeParameters().get(position);
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		if (getActualTypeArguments().isEmpty()) {
			return this;
		}
		CtTypeReference<?> erasedRef = clone();
		erasedRef.getActualTypeArguments().clear();
		return erasedRef;
	}

	@Override
	@DerivedProperty
	public boolean isSimplyQualified() {
		if (pack != null) {
			return pack.isImplicit();
		} else if (declaringType != null) {
			return declaringType.isImplicit();
		}
		return false;
	}

	@Override
	@DerivedProperty
	public CtTypeReferenceImpl<T> setSimplyQualified(boolean isSimplyQualified) {
		if (pack != null) {
			pack.setImplicit(isSimplyQualified);
		} else if (declaringType != null) {
			declaringType.setImplicit(isSimplyQualified);
		}
		return this;
	}

	@Override
	public boolean isArray() {
		return getSimpleName().contains("[");
	}
}
