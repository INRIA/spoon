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
package spoon.support.reflect.reference;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
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
import spoon.support.SpoonClassNotFoundException;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.QualifiedNameBasedSortedSet;
import spoon.support.util.RtHelper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtTypeReferenceImpl<T> extends CtReferenceImpl implements CtTypeReference<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> actualTypeArguments = CtElementImpl.emptyList();

	CtTypeReference<?> declaringType;

	private CtPackageReference pack;

	public CtTypeReferenceImpl() {
		super();
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
		if (getSimpleName().equals("int")) {
			return getFactory().Type().createReference(Integer.class);
		}
		if (getSimpleName().equals("float")) {
			return getFactory().Type().createReference(Float.class);
		}
		if (getSimpleName().equals("long")) {
			return getFactory().Type().createReference(Long.class);
		}
		if (getSimpleName().equals("char")) {
			return getFactory().Type().createReference(Character.class);
		}
		if (getSimpleName().equals("double")) {
			return getFactory().Type().createReference(Double.class);
		}
		if (getSimpleName().equals("boolean")) {
			return getFactory().Type().createReference(Boolean.class);
		}
		if (getSimpleName().equals("short")) {
			return getFactory().Type().createReference(Short.class);
		}
		if (getSimpleName().equals("byte")) {
			return getFactory().Type().createReference(Byte.class);
		}
		if (getSimpleName().equals("void")) {
			return getFactory().Type().createReference(Void.class);
		}
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<T> getActualClass() {
		if (isPrimitive()) {
			String simpleN = getSimpleName();
			if ("boolean".equals(simpleN)) {
				return (Class<T>) boolean.class;
			} else if ("byte".equals(simpleN)) {
				return (Class<T>) byte.class;
			} else if ("double".equals(simpleN)) {
				return (Class<T>) double.class;
			} else if ("int".equals(simpleN)) {
				return (Class<T>) int.class;
			} else if ("short".equals(simpleN)) {
				return (Class<T>) short.class;
			} else if ("char".equals(simpleN)) {
				return (Class<T>) char.class;
			} else if ("long".equals(simpleN)) {
				return (Class<T>) long.class;
			} else if ("float".equals(simpleN)) {
				return (Class<T>) float.class;
			} else if ("void".equals(simpleN)) {
				return (Class<T>) void.class;
			}
		}
		return findClass();
	}

	/**
	 * Finds the class requested in {@link #getActualClass()}.
	 *
	 * Looks for the class in the standard Java classpath, but also in the sourceClassPath given as option.
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> findClass() {
		try {
			// creating a classloader on the fly is not the most efficient
			// but it decreases the amount of state to maintain
			// since getActualClass is only used in rare cases, that's OK.
			return (Class<T>) getFactory().getEnvironment().getClassLoader().loadClass(getQualifiedName());
		} catch (Throwable e) {
			throw new SpoonClassNotFoundException("cannot load class: " + getQualifiedName(), e);
		}
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
	@SuppressWarnings("unchecked")
	public CtType<T> getDeclaration() {
		return getFactory().Type().get(getQualifiedName());
	}

	@Override
	public CtType<T> getTypeDeclaration() {
		CtType<T> t = getFactory().Type().get(getQualifiedName());
		if (t != null) {
			return t;
		}
		return getFactory().Type().get(getActualClass());
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
	public boolean isAssignableFrom(CtTypeReference<?> type) {
		return type != null && type.isSubtypeOf(this);
	}

	@Override
	public boolean isPrimitive() {
		return ("boolean".equals(getSimpleName()) || "byte".equals(getSimpleName()) || "double".equals(getSimpleName()) || "int".equals(getSimpleName()) || "short".equals(getSimpleName())
				|| "char".equals(getSimpleName()) || "long".equals(getSimpleName()) || "float".equals(getSimpleName()) || "void".equals(getSimpleName()));
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
		CtType<?> superTypeDecl = type.getDeclaration();
		CtType<?> subTypeDecl = getDeclaration();
		if ((subTypeDecl == null) && (superTypeDecl == null)) {
			try {
				if (((this instanceof CtArrayTypeReference) && (type instanceof CtArrayTypeReference))) {
					return ((CtArrayTypeReference<?>) this).getComponentType().isSubtypeOf(((CtArrayTypeReference<?>) type).getComponentType());
				}
				Class<?> actualSubType = getActualClass();
				Class<?> actualSuperType = type.getActualClass();
				return actualSuperType.isAssignableFrom(actualSubType);
			} catch (Exception e) {
				Launcher.LOGGER.error("cannot determine runtime types for '" + this + "' (" + getQualifiedName() + ") and '" + type + "' (" + type.getQualifiedName() + ")", e);
				return false;
			}
		}
		if (getQualifiedName().equals(type.getQualifiedName())) {
			return true;
		}
		if (subTypeDecl != null) {
			if (getFactory().Type().OBJECT.equals(type)) {
				return true;
			}
			for (CtTypeReference<?> ref : subTypeDecl.getSuperInterfaces()) {
				if (ref.isSubtypeOf(type)) {
					return true;
				}
			}
			if (subTypeDecl instanceof CtClass) {
				if (((CtClass<?>) subTypeDecl).getSuperclass() != null) {
					if (((CtClass<?>) subTypeDecl).getSuperclass().equals(type)) {
						return true;
					}
					return ((CtClass<?>) subTypeDecl).getSuperclass().isSubtypeOf(type);
				}
			}
			return false;
		} else {
			try {
				Class<?> actualSubType = getActualClass();
				for (Class<?> c : actualSubType.getInterfaces()) {
					if (getFactory().Type().createReference(c).isSubtypeOf(type)) {
						return true;
					}
				}
				CtTypeReference<?> superType = getFactory().Type().createReference(actualSubType.getSuperclass());
				return superType != null && (superType.equals(type) || superType.isSubtypeOf(type));
			} catch (Exception e) {
				Launcher.LOGGER.error("cannot determine runtime types for '" + this + "' and '" + type + "'", e);
				return false;
			}
		}
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
		this.declaringType = declaringType;
		return (C) this;
	}

	@Override
	public <C extends CtTypeReference<T>> C setPackage(CtPackageReference pack) {
		if (pack != null) {
			pack.setParent(this);
		}
		this.pack = pack;
		return (C) this;
	}

	@Override
	public void replace(CtTypeReference<?> reference) {
		super.replace(reference);
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
		CtType<?> t = getDeclaration();
		if (t == null) {
			try {
				return getDeclaredFieldReferences();
			} catch (SpoonClassNotFoundException cnfe) {
				return handleParentNotFound(cnfe);
			}
		} else {
			return t.getDeclaredFields();
		}
	}

	/**
	 * Collects all field references of the declared class.
	 *
	 * @return collection of field references
	 */
	private Collection<CtFieldReference<?>> getDeclaredFieldReferences() {
			Collection<CtFieldReference<?>> references = new ArrayList<>();
			for (Field f : getDeclaredFields(getActualClass())) {
				references.add(getFactory().Field().createReference(f));
			}
			if (getActualClass().isAnnotation()) {
				for (Method m : getActualClass().getDeclaredMethods()) {
					CtTypeReference<?> retRef = getFactory().Type().createReference(m.getReturnType());
					CtFieldReference<?> fr = getFactory().Field().createReference(this, retRef, m.getName());
					references.add(fr);
				}
			}
			return references;
	}

	private Field[] getDeclaredFields(Class<?> cls) {
		try {
			return cls.getDeclaredFields();
		} catch (Throwable e) {
			throw new SpoonClassNotFoundException("cannot load fields of class: " + getQualifiedName(), e);
		}
	}

	private Collection<CtFieldReference<?>> handleParentNotFound(SpoonClassNotFoundException cnfe) {
		String msg = "cannot load class: " + getQualifiedName() + " with class loader "
				+ Thread.currentThread().getContextClassLoader();
		if (getFactory().getEnvironment().getNoClasspath()) {
			// should not be thrown in 'noClasspath' environment (#775)
			Launcher.LOGGER.warn(msg);
			return Collections.emptyList();
		} else {
			throw cnfe;
		}
	}

	@Override
	public CtFieldReference<?> getDeclaredField(String name) {
		if (name == null) {
			return null;
		}
		CtType<?> t = getDeclaration();
		if (t == null) {
			try {
				Collection<CtFieldReference<?>> fields = getDeclaredFieldReferences();
				for (CtFieldReference<?> field : fields) {
					if (name.equals(field.getSimpleName())) {
						return field;
					}
				}
			} catch (SpoonClassNotFoundException cnfe) {
				handleParentNotFound(cnfe);
				return null;
			}
			return null;
		} else {
			return t.getDeclaredField(name);
		}
	}

	public CtFieldReference<?> getDeclaredOrInheritedField(String fieldName) {
		CtType<?> t = getDeclaration();
		if (t == null) {
			CtFieldReference<?> field = getDeclaredField(fieldName);
			if (field != null) {
				return field;
			}
			CtTypeReference<?> typeRef = getSuperclass();
			if (typeRef != null) {
				field = typeRef.getDeclaredOrInheritedField(fieldName);
				if (field != null) {
					return field;
				}
			}
			Set<CtTypeReference<?>> ifaces = getSuperInterfaces();
			for (CtTypeReference<?> iface : ifaces) {
				field = iface.getDeclaredOrInheritedField(fieldName);
				if (field != null) {
					return field;
				}
			}
			return field;
		} else {
			return t.getDeclaredOrInheritedField(fieldName);
		}
	}


	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		CtType<T> t = getDeclaration();
		if (t == null) {
			return RtHelper.getAllExecutables(getActualClass(), getFactory());
		} else {
			return t.getDeclaredExecutables();
		}
	}

	@Override
	public Collection<CtFieldReference<?>> getAllFields() {
		try {
			CtType<?> t = getTypeDeclaration();
			return t.getAllFields();
		} catch (SpoonClassNotFoundException cnfe) {
			//START OF Hack of Hack in JDTTreeBuilderHelper.createType(...)
			CtTypeReference<?> declaringTypeRef = this.getDeclaringType();
			if (declaringTypeRef != null) {
				CtType<?> declaringType = declaringTypeRef.getDeclaration();
				if (declaringType != null && declaringType.getNestedType(getSimpleName()) == null) {
					//this type does not know it's real fully qualified name, so we cannot access it's java class.
					//See the spoon.test.imports.ImportTest, whose class spoon.test.imports.testclasses.internal.SuperClass$InnerClassProtected is this case
					Launcher.LOGGER.warn("cannot load class with access path: " + getQualifiedName());
					return Collections.emptyList();
				}
			}
			//END OF Hack
			return handleParentNotFound(cnfe);
		}
	}

	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Collection<CtExecutableReference<?>> l = new ArrayList<>();
		CtType<T> t = getDeclaration();
		if (t == null) {
			Class<?> c = getActualClass();
			for (Method m : c.getDeclaredMethods()) {
				l.add(getFactory().Method().createReference(m));
			}
			for (Constructor<?> cons : c.getDeclaredConstructors()) {
				CtExecutableReference<?> consRef = getFactory().Constructor().createReference(cons);
				l.add(consRef);
			}
			Class<?> sc = c.getSuperclass();
			if (sc != null) {
				l.addAll(getFactory().Type().createReference(sc).getAllExecutables());
			}
		} else {
			return t.getAllExecutables();
		}
		return l;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		CtType<T> t = getDeclaration();
		if (t != null) {
			return t.getModifiers();
		}
		Class<T> c = getActualClass();
		return RtHelper.getModifiers(c.getModifiers());
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		CtType<T> t = getDeclaration();
		if (t != null) {
			return t.getSuperclass();
		} else {
			Class<T> c = getActualClass();
			Class<?> sc = c.getSuperclass();
			if (sc == null) {
				return null;
			}
			return getFactory().Type().createReference(sc);
		}
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		CtType<?> t = getDeclaration();
		if (t != null) {
			return t.getSuperInterfaces();
		} else {
			Class<?> c = getActualClass();
			Class<?>[] sis = c.getInterfaces();
			if ((sis != null) && (sis.length > 0)) {
				Set<CtTypeReference<?>> set = new QualifiedNameBasedSortedSet<CtTypeReference<?>>();
				for (Class<?> si : sis) {
					set.add(getFactory().Type().createReference(si));
				}
				return set;
			}
		}
		return Collections.emptySet();
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
		if (this.getDeclaration() != null) {
			return (this.getDeclaration().isLocalType());
		}
		// A local type doesn't have a fully qualified name but have an identifier
		// to know which is the local type member wanted by the developer.
		// Oracle documentation: https://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.7
		// JDT documentation: http://help.eclipse.org/juno/topic/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/ITypeBinding.html#getQualifiedName()
		final Pattern pattern = Pattern.compile("^([0-9]+)([a-zA-Z]+)$");
		final Matcher m = pattern.matcher(getSimpleName());
		return m.find();
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
		actualTypeArguments.add(actualTypeArgument);
		return (C) this;
	}

	@Override
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return actualTypeArguments != CtElementImpl.<CtTypeReference<?>>emptyList() && actualTypeArguments.remove(actualTypeArgument);
	}

	@Override
	public boolean isInterface() {
		CtType<T> t = getDeclaration();
		if (t == null) {
			return getActualClass().isInterface();
		} else {
			return t.isInterface();
		}
	}

	@Override
	public boolean isAnnotationType() {
		CtType<T> t = getDeclaration();
		if (t == null) {
			return getActualClass().isAnnotation();
		} else {
			return t.isAnnotationType();
		}
	}

	@Override
	public boolean isGenerics() {
		return false;
	}

	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtTypeReference<T> clone() {
		return (CtTypeReference<T>) super.clone();
	}
}
