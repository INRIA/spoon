/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.compiler.SnippetCompilationHelper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static spoon.reflect.ModelElementContainerDefaultCapacities.FIELDS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The implementation for {@link spoon.reflect.declaration.CtType}.
 */
public abstract class CtTypeImpl<T> extends CtNamedElementImpl implements CtType<T> {

	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> formalTypeParameters = emptyList();

	Set<CtTypeReference<?>> interfaces = emptySet();

	Set<CtMethod<?>> methods = emptySet();

	private List<CtField<?>> fields = new ArrayList<CtField<?>>(FIELDS_CONTAINER_DEFAULT_CAPACITY);

	Set<CtType<?>> nestedTypes = emptySet();

	Set<ModifierKind> modifiers = emptySet();

	public CtTypeImpl() {
		super();
	}

	@Override
	public <F, C extends CtType<T>> C addField(CtField<F> field) {
		if (!this.fields.contains(field)) {
			field.setParent(this);
			this.fields.add(field);
		}

		// field already exists
		return (C) this;
	}

	@Override
	public <F, C extends CtType<T>> C addField(int index, CtField<F> field) {
		if (!this.fields.contains(field)) {
			field.setParent(this);
			this.fields.add(index, field);
		}

		// field already exists
		return (C) this;
	}

	@Override
	public <F> boolean removeField(CtField<F> field) {
		return this.fields.remove(field);
	}

	@Override
	public CtField<?> getField(String name) {
		for (CtField<?> f : fields) {
			if (f.getSimpleName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	@Override
	public List<CtField<?>> getFields() {
		return fields;
	}

	@Override
	public <N, C extends CtType<T>> C addNestedType(CtType<N> nestedType) {
		if (nestedTypes == CtElementImpl.<CtType<?>>emptySet()) {
			nestedTypes = new TreeSet<CtType<?>>();
		}
		nestedType.setParent(this);
		this.nestedTypes.add(nestedType);
		return (C) this;
	}

	@Override
	public <N> boolean removeNestedType(CtType<N> nestedType) {
		if (nestedTypes.isEmpty()) {
			return false;
		} else if (nestedTypes.size() == 1) {
			if (nestedTypes.contains(nestedType)) {
				nestedTypes = CtElementImpl.<CtType<?>>emptySet();
				return true;
			} else {
				return false;
			}
		} else {
			return this.nestedTypes.remove(nestedType);
		}
	}

	@Override
	public Set<CtTypeReference<?>> getUsedTypes(boolean includeSamePackage) {
		Set<CtTypeReference<?>> typeRefs = new HashSet<CtTypeReference<?>>();
		for (CtTypeReference<?> typeRef : Query
				.getReferences(this, new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class))) {
			if (!(typeRef.isPrimitive() || (typeRef instanceof CtArrayTypeReference) || typeRef.toString()
					.equals(CtTypeReference.NULL_TYPE_NAME) || ((typeRef.getPackage() != null) && "java.lang"
					.equals(typeRef.getPackage().toString()))) && !(!includeSamePackage && getPackageReference(typeRef)
					.equals(this.getPackage().getReference()))) {
				typeRefs.add(typeRef);
			}
		}
		return typeRefs;
	}

	/**
	 * Return the package reference for the corresponding type reference. For
	 * inner type, return the package reference of the top-most enclosing type.
	 * This helper method is meant to deal with package references that are
	 * <code>null</code> for inner types.
	 *
	 * @param tref
	 * 		the type reference
	 * @return the corresponding package reference
	 * @see CtTypeReference#getPackage()
	 * @since 4.0
	 */
	private static CtPackageReference getPackageReference(CtTypeReference<?> tref) {
		CtPackageReference pref = tref.getPackage();
		while (pref == null) {
			tref = tref.getDeclaringType();
			pref = tref.getPackage();
		}
		return pref;
	}

	@Override
	public Class<T> getActualClass() {
		return getFactory().Type().createReference(this).getActualClass();
	}

	@Override
	public CtType<?> getDeclaringType() {
		try {
			return getParent(CtType.class);
		} catch (ParentNotInitializedException ex) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <N extends CtType<?>> N getNestedType(final String name) {
		class NestedTypeScanner extends CtScanner {
			CtType<?> type;

			public void checkType(CtType<?> type) {
				if (type.getSimpleName().equals(name) && CtTypeImpl.this.equals(type.getDeclaringType())) {
					this.type = type;
				}
			}

			@Override
			public <U> void visitCtClass(spoon.reflect.declaration.CtClass<U> ctClass) {
				scan(ctClass.getNestedTypes());
				scan(ctClass.getConstructors());
				scan(ctClass.getMethods());

				checkType(ctClass);
			}

			@Override
			public <U> void visitCtInterface(spoon.reflect.declaration.CtInterface<U> intrface) {
				scan(intrface.getNestedTypes());
				scan(intrface.getMethods());

				checkType(intrface);
			}

			@Override
			public <U extends java.lang.Enum<?>> void visitCtEnum(spoon.reflect.declaration.CtEnum<U> ctEnum) {
				scan(ctEnum.getNestedTypes());
				scan(ctEnum.getConstructors());
				scan(ctEnum.getMethods());

				checkType(ctEnum);
			}

			@Override
			public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
				scan(annotationType.getNestedTypes());

				checkType(annotationType);
			}

			CtType<?> getType() {
				return type;
			}
		}
		NestedTypeScanner scanner = new NestedTypeScanner();
		scanner.scan(this);
		return (N) scanner.getType();
	}

	@Override
	public Set<CtType<?>> getNestedTypes() {
		return nestedTypes;
	}

	@Override
	public CtPackage getPackage() {
		if (parent instanceof CtPackage) {
			return (CtPackage) getParent();
		} else if (parent instanceof CtType) {
			return ((CtType<?>) parent).getPackage();
		} else {
			return null;
		}
	}

	@Override
	public CtTypeReference<T> getReference() {
		return getFactory().Type().createReference(this);
	}

	@Override
	public boolean isTopLevel() {
		return (getDeclaringType() == null) && (getPackage() != null);
	}

	@Override
	public void compileAndReplaceSnippets() {
		SnippetCompilationHelper.compileAndReplaceSnippetsIn(this);
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	@Override
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.of(modifier);
		}
		modifiers.add(modifier);
		return (C) this;
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return modifiers != CtElementImpl.<ModifierKind>emptySet() && modifiers.remove(modifier);
	}

	@Override
	public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind>emptySet()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
		return (C) this;
	}

	@Override
	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC)) {
			return ModifierKind.PUBLIC;
		}
		if (getModifiers().contains(ModifierKind.PROTECTED)) {
			return ModifierKind.PROTECTED;
		}
		if (getModifiers().contains(ModifierKind.PRIVATE)) {
			return ModifierKind.PRIVATE;
		}
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public boolean isLocalType() {
		return getReference().isLocalType();
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		// overridden in CtClassImpl
		return null;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public List<CtFieldReference<?>> getAllFields() {
		List<CtFieldReference<?>> l = new ArrayList<CtFieldReference<?>>(getFields().size());
		for (CtField<?> f : getFields()) {
			l.add(f.getReference());
		}
		if (this instanceof CtClass) {
			CtTypeReference<?> st = ((CtClass<?>) this).getSuperclass();
			if (st != null) {
				l.addAll(st.getAllFields());
			}
		}
		return l;
	}

	@Override
	public Collection<CtFieldReference<?>> getDeclaredFields() {
		List<CtFieldReference<?>> l = new ArrayList<CtFieldReference<?>>(getFields().size());
		for (CtField<?> f : getFields()) {
			l.add(f.getReference());
		}
		return Collections.unmodifiableCollection(l);
	}

	/**
	 * Tells if this type is a subtype of the given type.
	 */
	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return type.isSubtypeOf(getReference());
	}

	@Override
	public boolean isAssignableFrom(CtTypeReference<?> type) {
		return isSubtypeOf(type);
	}

	@Override
	public <M, C extends CtType<T>> C addMethod(CtMethod<M> method) {
		if (methods == CtElementImpl.<CtMethod<?>>emptySet()) {
			methods = new TreeSet<CtMethod<?>>();
		}
		method.setParent(this);
		methods.add(method);
		return (C) this;
	}

	@Override
	public <M> boolean removeMethod(CtMethod<M> method) {
		if (methods.isEmpty()) {
			return false;
		} else if (methods.size() == 1) {
			if (methods.contains(method)) {
				methods = CtElementImpl.<CtMethod<?>>emptySet();
				return true;
			} else {
				return false;
			}
		} else {
			// This contains() is not needed for dealing with empty and
			// singleton sets (as they are dealt above), but removing contains()
			// check here might broke someone's code like
			// type.setMethods(immutableSet(a, b, c));
			// ...
			// type.removeMethod(d)
			return methods.contains(method) && methods.remove(method);
		}
	}

	@Override
	public <S, C extends CtType<T>> C addSuperInterface(CtTypeReference<S> interfac) {
		if (interfaces == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			interfaces = new TreeSet<CtTypeReference<?>>();
		}
		interfac.setParent(this);
		interfaces.add(interfac);
		return (C) this;
	}

	@Override
	public <S> boolean removeSuperInterface(CtTypeReference<S> interfac) {
		if (interfaces.isEmpty()) {
			return false;
		} else if (interfaces.size() == 1) {
			if (interfaces.contains(interfac)) {
				interfaces = CtElementImpl.<CtTypeReference<?>>emptySet();
				return true;
			} else {
				return false;
			}
		} else {
			// contains() not needed. see comment in removeMethod()
			return interfaces.contains(interfac) && interfaces.remove(interfac);
		}
	}

	@Override
	public <C extends CtGenericElement> C addFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		if (formalTypeParameters == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			formalTypeParameters = new ArrayList<CtTypeReference<?>>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		formalTypeParameter.setParent(this);
		formalTypeParameters.add(formalTypeParameter);
		return (C) this;
	}

	@Override
	public boolean removeFormalTypeParameter(CtTypeReference<?> formalTypeParameter) {
		return formalTypeParameters.contains(formalTypeParameter) && formalTypeParameters.remove(formalTypeParameter);
	}

	@Override
	public List<CtTypeReference<?>> getFormalTypeParameters() {
		return formalTypeParameters;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>...
			parameterTypes) {
		for (CtMethod<?> mm : methods) {
			CtMethod<R> m = (CtMethod<R>) mm;
			if (m.getSimpleName().equals(name)) {
				if (!m.getType().equals(returnType)) {
					continue;
				}
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size()) && (i < parameterTypes.length); i++) {
					if (!m.getParameters().get(i).getType().getQualifiedName()
							.equals(parameterTypes[i].getQualifiedName())) {
						cont = false;
					}
				}
				if (cont) {
					return m;
				}
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes) {
		for (CtMethod<?> m : methods) {
			if (m.getSimpleName().equals(name)) {
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size()) && (i < parameterTypes.length); i++) {
					// String
					// s1=m.getParameters().get(i).getType().getQualifiedName();
					// String s2=parameterTypes[i].getQualifiedName();
					if (!m.getParameters().get(i).getType().equals(parameterTypes[i])) {
						cont = false;
					}
				}
				if (cont) {
					return (CtMethod<R>) m;
				}
			}
		}
		return null;
	}

	@Override
	public Set<CtMethod<?>> getMethods() {
		return methods;
	}

	@Override
	public Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes) {
		Set<CtMethod<?>> result = new HashSet<CtMethod<?>>();
		for (CtMethod<?> m : methods) {
			for (CtAnnotation<?> a : m.getAnnotations()) {
				if (Arrays.asList(annotationTypes).contains(a.getAnnotationType())) {
					result.add(m);
				}
			}
		}
		return result;
	}

	@Override
	public List<CtMethod<?>> getMethodsByName(String name) {
		List<CtMethod<?>> result = new ArrayList<CtMethod<?>>(1);
		for (CtMethod<?> m : methods) {
			if (name.equals(m.getSimpleName())) {
				result.add(m);
			}
		}
		return result;
	}

	@Override
	public String getQualifiedName() {
		if (isTopLevel()) {
			if ((getPackage() != null) && !getPackage().getSimpleName().equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				return getPackage().getQualifiedName() + "." + getSimpleName();
			}
			return getSimpleName();
		}
		if (getDeclaringType() != null) {
			return getDeclaringType().getQualifiedName() + INNERTTYPE_SEPARATOR + getSimpleName();
		}
		return getSimpleName();
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		return interfaces;
	}

	@Override
	public <C extends CtGenericElement> C setFormalTypeParameters(List<CtTypeReference<?>> formalTypeParameters) {
		if (this.formalTypeParameters == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.formalTypeParameters = new ArrayList<CtTypeReference<?>>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.formalTypeParameters.clear();
		for (CtTypeReference<?> formalTypeParameter : formalTypeParameters) {
			addFormalTypeParameter(formalTypeParameter);
		}
		return (C) this;
	}

	@Override
	public <C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods) {
		this.methods.clear();
		for (CtMethod<?> meth : methods) {
			addMethod(meth);
		}
		return (C) this;
	}

	@Override
	public <C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces) {
		if (this.interfaces == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			this.interfaces = new TreeSet<CtTypeReference<?>>();
		}
		this.interfaces.clear();
		for (CtTypeReference<?> anInterface : interfaces) {
			addSuperInterface(anInterface);
		}
		return (C) this;
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		List<CtExecutableReference<?>> l = new ArrayList<CtExecutableReference<?>>(getMethods().size());
		for (CtExecutable<?> m : getMethods()) {
			l.add(m.getReference());
		}
		return Collections.unmodifiableCollection(l);
	}

	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		HashSet<CtExecutableReference<?>> l = new HashSet<CtExecutableReference<?>>(getDeclaredExecutables());
		if (this instanceof CtClass) {
			CtTypeReference<?> st = ((CtClass<?>) this).getSuperclass();
			if (st != null) {
				l.addAll(st.getAllExecutables());
			}
		}
		return l;
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> l = new HashSet<CtMethod<?>>(getMethods());
		if ((getSuperclass() != null) && (getSuperclass().getDeclaration() != null)) {
			CtType<?> t = getSuperclass().getDeclaration();
			l.addAll(t.getAllMethods());
		}

		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.getDeclaration() != null) {
				CtType<?> t = ref.getDeclaration();
				l.addAll(t.getAllMethods());
			}
		}

		return Collections.unmodifiableSet(l);
	}
}
