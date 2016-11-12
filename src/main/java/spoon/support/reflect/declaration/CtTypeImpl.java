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
package spoon.support.reflect.declaration;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.compiler.SnippetCompilationHelper;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.util.QualifiedNameBasedSortedSet;
import spoon.support.util.SignatureBasedSortedSet;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The implementation for {@link spoon.reflect.declaration.CtType}.
 */
public abstract class CtTypeImpl<T> extends CtNamedElementImpl implements CtType<T> {

	private static final long serialVersionUID = 1L;

	List<CtTypeParameter> formalCtTypeParameters = emptyList();

	Set<CtTypeReference<?>> interfaces = emptySet();

	Set<ModifierKind> modifiers = emptySet();

	List<CtTypeMember> typeMembers = emptyList();

	public CtTypeImpl() {
		super();
	}

	@Override
	public List<CtTypeMember> getTypeMembers() {
		return typeMembers;
	}

	@Override
	public <C extends CtType<T>> C addTypeMember(CtTypeMember member) {
		if (member == null) {
			return (C) this;
		}
		return addTypeMemberAt(typeMembers.size(), member);
	}

	@Override
	public <C extends CtType<T>> C addTypeMemberAt(int position, CtTypeMember member) {
		if (member == null) {
			return (C) this;
		}
		if (this.typeMembers == CtElementImpl.<CtTypeMember>emptyList()) {
			this.typeMembers = new ArrayList<>();
		}
		if (!this.typeMembers.contains(member)) {
			member.setParent(this);
			this.typeMembers.add(position, member);
		}
		return (C) this;
	}

	@Override
	public boolean removeTypeMember(CtTypeMember member) {
		if (typeMembers.size() == 1) {
			if (typeMembers.contains(member)) {
				typeMembers = emptyList();
				return true;
			} else {
				return false;
			}
		}
		return typeMembers.remove(member);
	}

	@Override
	public <C extends CtType<T>> C setTypeMembers(List<CtTypeMember> members) {
		if (members == null || members.isEmpty()) {
			this.typeMembers = emptyList();
			return (C) this;
		}
		typeMembers.clear();
		for (CtTypeMember typeMember : members) {
			addTypeMember(typeMember);
		}
		return (C) this;
	}

	@Override
	public <F, C extends CtType<T>> C addFieldAtTop(CtField<F> field) {
		if (field != null && !this.typeMembers.contains(field)) {
			CompilationUnit compilationUnit = null;
			if (getPosition() != null) {
				compilationUnit = getPosition().getCompilationUnit();
			}
			field.setPosition(getFactory().Core().createSourcePosition(compilationUnit, -1, -1, -1, new int[0]));
		}
		return addTypeMemberAt(0, field);
	}

	@Override
	public <F, C extends CtType<T>> C addField(CtField<F> field) {
		return addTypeMember(field);
	}

	@Override
	public <F, C extends CtType<T>> C addField(int index, CtField<F> field) {
		return addTypeMemberAt(index, field);
	}

	@Override
	public <C extends CtType<T>> C setFields(List<CtField<?>> fields) {
		if (fields == null || fields.isEmpty()) {
			this.typeMembers.removeAll(getFields());
			return (C) this;
		}
		typeMembers.removeAll(getFields());
		for (CtField<?> field : fields) {
			addField(field);
		}
		return (C) this;
	}

	@Override
	public <F> boolean removeField(CtField<F> field) {
		return removeTypeMember(field);
	}

	@Override
	public CtField<?> getField(String name) {
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtField && ((CtField) typeMember).getSimpleName().equals(name)) {
				return (CtField<?>) typeMember;
			}
		}
		return null;
	}

	@Override
	public CtFieldReference<?> getDeclaredField(String name) {
		CtField<?> field = getField(name);
		return field != null ? getFactory().Field().createReference(field) : null;
	}

	@Override
	public CtFieldReference<?> getDeclaredOrInheritedField(String fieldName) {
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
	}


	@Override
	public List<CtField<?>> getFields() {
		List<CtField<?>> fields = new ArrayList<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtField) {
				fields.add((CtField<?>) typeMember);
			}
		}
		return fields;
	}

	@Override
	public <N, C extends CtType<T>> C addNestedType(CtType<N> nestedType) {
		return addTypeMember(nestedType);
	}

	@Override
	public <N> boolean removeNestedType(CtType<N> nestedType) {
		return removeTypeMember(nestedType);
	}

	@Override
	public <C extends CtType<T>> C setNestedTypes(Set<CtType<?>> nestedTypes) {
		if (nestedTypes == null || nestedTypes.isEmpty()) {
			this.typeMembers.removeAll(getNestedTypes());
			return (C) this;
		}
		typeMembers.removeAll(getNestedTypes());
		for (CtType<?> nestedType : nestedTypes) {
			addNestedType(nestedType);
		}
		return (C) this;
	}

	@Override
	public Set<CtTypeReference<?>> getUsedTypes(boolean includeSamePackage) {
		Set<CtTypeReference<?>> typeRefs = new QualifiedNameBasedSortedSet<>();
		for (CtTypeReference<?> typeRef : Query.getReferences(this, new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class))) {
			if (isValidTypeReference(typeRef) && shouldIncludeSamePackage(includeSamePackage, typeRef)) {
				typeRefs.add(typeRef);
			}
		}
		return typeRefs;
	}

	private boolean shouldIncludeSamePackage(boolean includeSamePackage, CtTypeReference<?> typeRef) {
		return includeSamePackage || (getPackage() != null && !getPackageReference(typeRef).equals(getPackage().getReference()));
	}

	private boolean isValidTypeReference(CtTypeReference<?> typeRef) {
		return !(isFromJavaLang(typeRef) || typeRef.isPrimitive() || typeRef instanceof CtArrayTypeReference || CtTypeReference.NULL_TYPE_NAME.equals(typeRef.toString()));
	}

	private boolean isFromJavaLang(CtTypeReference<?> typeRef) {
		return typeRef.getPackage() != null && "java.lang".equals(typeRef.getPackage().toString());
	}

	/**
	 * Return the package reference for the corresponding type reference. For
	 * inner type, return the package reference of the top-most enclosing type.
	 * This helper method is meant to deal with package references that are
	 * <code>null</code> for inner types.
	 *
	 * @param tref the type reference
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
		class NestedTypeScanner extends EarlyTerminatingScanner<CtType<?>> {

			private boolean checkType(CtType<?> type) {
				if (type.getSimpleName().equals(name) && CtTypeImpl.this.equals(type.getDeclaringType())) {
					setResult(type);
					terminate();
					return true;
				}
				return false;
			}

			@Override
			public <U> void visitCtClass(spoon.reflect.declaration.CtClass<U> ctClass) {
				if (!checkType(ctClass)) {
					final List<CtTypeMember> typeMembers = new ArrayList<>();
					for (CtTypeMember typeMember : ctClass.getTypeMembers()) {
						if (typeMember instanceof CtType || typeMember instanceof CtConstructor || typeMember instanceof CtMethod) {
							typeMembers.add(typeMember);
						}
					}
					scan(typeMembers);
				}
			}

			@Override
			public <U> void visitCtInterface(spoon.reflect.declaration.CtInterface<U> intrface) {
				if (!checkType(intrface)) {
					final List<CtTypeMember> typeMembers = new ArrayList<>();
					for (CtTypeMember typeMember : intrface.getTypeMembers()) {
						if (typeMember instanceof CtType || typeMember instanceof CtMethod) {
							typeMembers.add(typeMember);
						}
					}
					scan(typeMembers);
				}
			}

			@Override
			public <U extends java.lang.Enum<?>> void visitCtEnum(spoon.reflect.declaration.CtEnum<U> ctEnum) {
				if (!checkType(ctEnum)) {
					final List<CtTypeMember> typeMembers = new ArrayList<>();
					for (CtTypeMember typeMember : ctEnum.getTypeMembers()) {
						if (typeMember instanceof CtType || typeMember instanceof CtConstructor || typeMember instanceof CtMethod) {
							typeMembers.add(typeMember);
						}
					}
					scan(typeMembers);
				}
			}

			@Override
			public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
				if (!checkType(annotationType)) {
					scan(annotationType.getNestedTypes());
				}
			}
		}
		NestedTypeScanner scanner = new NestedTypeScanner();
		scanner.scan(this);
		return (N) scanner.getResult();
	}

	@Override
	public Set<CtType<?>> getNestedTypes() {
		Set<CtType<?>> nestedTypes = new QualifiedNameBasedSortedSet<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtType) {
				nestedTypes.add((CtType<?>) typeMember);
			}
		}
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
		if (modifiers.size() > 0) {
			this.modifiers = EnumSet.copyOf(modifiers);
		}
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
		return isParentInitialized() && getParent() instanceof CtBlock;
	}

	@Override
	public CtTypeReference<?> getSuperclass() {
		// overridden in subclasses.
		return null;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isAnnotationType() {
		return false;
	}

	@Override
	public boolean isGenerics() {
		return false;
	}

	@Override
	public List<CtFieldReference<?>> getAllFields() {
		final List<CtFieldReference<?>> fields = getDeclaredFields();
		if (this instanceof CtClass) {
			CtTypeReference<?> st = ((CtClass<?>) this).getSuperclass();
			if (st != null) {
				fields.addAll(st.getAllFields());
			}
			Set<CtTypeReference<?>> superIFaces = ((CtClass<?>) this).getSuperInterfaces();
			for (CtTypeReference<?> superIFace : superIFaces) {
				fields.addAll(superIFace.getAllFields());
			}
		}
		return fields;
	}

	@Override
	public List<CtFieldReference<?>> getDeclaredFields() {
		if (typeMembers.isEmpty()) {
			return Collections.emptyList();
		}
		final List<CtFieldReference<?>> fields = new ArrayList<>(typeMembers.size());
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtField) {
				fields.add(((CtField) typeMember).getReference());
			}
		}
		return fields;
	}

	@Override
	public boolean isAssignableFrom(CtTypeReference<?> type) {
		return isSubtypeOf(type);
	}

	@Override
	public <M, C extends CtType<T>> C addMethod(CtMethod<M> method) {
		if (method != null) {
			for (CtTypeMember typeMember : new ArrayList<>(typeMembers)) {
				if (!(typeMember instanceof CtMethod)) {
					continue;
				}
				CtMethod<?> m = (CtMethod<?>) typeMember;
				if (m.getSignature().equals(method.getSignature())) {
					// replace old method by new one (based on signature and not equality)
					// we have to do it by hand
					typeMembers.remove(m);
				} else {
					// checking contract signature implies equal
					if (!factory.getEnvironment().checksAreSkipped() && m.equals(method)) {
						throw new AssertionError("violation of core contract! different signature but same equal");
					}
				}
			}
		}
		return addTypeMember(method);
	}

	@Override
	public <M> boolean removeMethod(CtMethod<M> method) {
		return removeTypeMember(method);
	}

	@Override
	public <S, C extends CtType<T>> C addSuperInterface(CtTypeReference<S> interfac) {
		if (interfac == null) {
			return (C) this;
		}
		if (interfaces == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			interfaces = new QualifiedNameBasedSortedSet<>();
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
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return formalCtTypeParameters;
	}

	@Override
	public <C extends CtFormalTypeDeclarer> C setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		if (formalTypeParameters == null || formalTypeParameters.isEmpty()) {
			this.formalCtTypeParameters = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			this.formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.formalCtTypeParameters.clear();
		for (CtTypeParameter formalTypeParameter : formalTypeParameters) {
			addFormalCtTypeParameter(formalTypeParameter);
		}
		return (C) this;
	}

	@Override
	public <C extends CtFormalTypeDeclarer> C addFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (formalTypeParameter == null) {
			return (C) this;
		}
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			formalCtTypeParameters = new ArrayList<>(TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		formalTypeParameter.setParent(this);
		formalCtTypeParameters.add(formalTypeParameter);
		return (C) this;
	}

	@Override
	public boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		return formalCtTypeParameters.contains(formalTypeParameter) && formalCtTypeParameters.remove(formalTypeParameter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>... parameterTypes) {
		for (CtTypeMember typeMember : typeMembers) {
			if (!(typeMember instanceof CtMethod)) {
				continue;
			}
			CtMethod<R> m = (CtMethod<R>) typeMember;
			if (m.getSimpleName().equals(name)) {
				if (!m.getType().equals(returnType)) {
					continue;
				}
				boolean cont = m.getParameters().size() == parameterTypes.length;
				for (int i = 0; cont && (i < m.getParameters().size()) && (i < parameterTypes.length); i++) {
					if (!m.getParameters().get(i).getType().getQualifiedName().equals(parameterTypes[i].getQualifiedName())) {
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
		if (name == null) {
			return null;
		}

		for (CtMethod<?> candidate : getMethodsByName(name)) {
			boolean cont = candidate.getParameters().size() == parameterTypes.length;
			for (int i = 0; cont && (i < candidate.getParameters().size()) && (i < parameterTypes.length); i++) {
				final CtTypeReference<?> ctParameterType = candidate.getParameters().get(i).getType();
				final CtTypeReference<?> parameterType = parameterTypes[i];
				if (parameterType instanceof CtArrayTypeReference) {
					if (ctParameterType instanceof CtArrayTypeReference) {
						if (!isSameParameter(((CtArrayTypeReference) ctParameterType).getComponentType(), ((CtArrayTypeReference) parameterType).getComponentType())) {
							cont = false;
						} else {
							if (!(((CtArrayTypeReference) ctParameterType).getDimensionCount() == ((CtArrayTypeReference) parameterType).getDimensionCount())) {
								cont = false;
							}
						}
					} else {
						cont = false;
					}
				} else if (!isSameParameter(ctParameterType, parameterType)) {
					cont = false;
				}
			}
			if (cont) {
				return (CtMethod<R>) candidate;
			}
		}
		return null;
	}

	private boolean isSameParameter(CtTypeReference<?> ctParameterType, CtTypeReference<?> expectedType) {
		if (expectedType instanceof CtTypeParameterReference && ctParameterType instanceof CtTypeParameterReference) {
			// Check if Object or extended.
			if (!ctParameterType.equals(expectedType)) {
				return false;
			}
		} else if (expectedType instanceof CtTypeParameterReference) {
			if (!ctParameterType.isSubtypeOf(factory.Type().createReference(expectedType.getActualClass()))) {
				return false;
			}
		} else if (ctParameterType instanceof CtTypeParameterReference) {
			CtTypeParameter declaration = (CtTypeParameter) ctParameterType.getDeclaration();
			if (declaration.getSuperclass() instanceof CtIntersectionTypeReference) {
				for (CtTypeReference<?> ctTypeReference : declaration.getSuperclass().asCtIntersectionTypeReference().getBounds()) {
					if (ctTypeReference.equals(expectedType)) {
						return true;
					}
				}
			} else if (declaration.getSuperclass() != null) {
				return declaration.getSuperclass().equals(expectedType);
			} else {
				return getFactory().Type().objectType().equals(expectedType);
			}
		} else if (!expectedType.equals(ctParameterType)) {
			return false;
		}
		return true;
	}

	@Override
	public Set<CtMethod<?>> getMethods() {
		Set<CtMethod<?>> methods = new SignatureBasedSortedSet<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (typeMember instanceof CtMethod) {
				methods.add((CtMethod<?>) typeMember);
			}
		}
		return methods;
	}

	@Override
	public Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes) {
		Set<CtMethod<?>> result = new SignatureBasedSortedSet<>();
		for (CtTypeMember typeMember : typeMembers) {
			if (!(typeMember instanceof CtMethod)) {
				continue;
			}
			CtMethod<?> m = (CtMethod<?>) typeMember;
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
		List<CtMethod<?>> result = new ArrayList<>(1);
		for (CtTypeMember typeMember : typeMembers) {
			if (!(typeMember instanceof CtMethod)) {
				continue;
			}
			CtMethod<?> m = (CtMethod<?>) typeMember;
			if (name.equals(m.getSimpleName())) {
				result.add(m);
			}
		}
		return result;
	}


	@Override
	public boolean hasMethod(CtMethod<?> method) {
		if (method == null) {
			return false;
		}

		final String over = method.getSignature();
		for (CtMethod<?> m : getMethods()) {
			if (m.getSignature().equals(over)) {
				return true;
			}
		}

		// Checking whether a super class has the method.
		final CtTypeReference<?> superCl = getSuperclass();
		try {
			if (superCl != null && superCl.getTypeDeclaration().hasMethod(method)) {
				return true;
			}
		} catch (SpoonException ex) {
			// No matter, trying something else.
		}

		// Finally, checking whether an interface has the method.
		for (CtTypeReference<?> interf : getSuperInterfaces()) {
			try {
				if (interf.getTypeDeclaration().hasMethod(method)) {
					return true;
				}
			} catch (SpoonException ex) {
				// No matter, trying something else.
			}
		}

		return false;
	}


	@Override
	public String getQualifiedName() {
		if (isTopLevel()) {
			if (getPackage() != null && !getPackage().isUnnamedPackage()) {
				return getPackage().getQualifiedName() + "." + getSimpleName();
			} else {
				return getSimpleName();
			}
		} else if (getDeclaringType() != null) {
			return getDeclaringType().getQualifiedName() + INNERTTYPE_SEPARATOR + getSimpleName();
		} else {
			return getSimpleName();
		}
	}

	@Override
	public Set<CtTypeReference<?>> getSuperInterfaces() {
		return interfaces;
	}

	@Override
	public <C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods) {
		if (methods == null || methods.isEmpty()) {
			this.typeMembers.removeAll(getMethods());
			return (C) this;
		}
		typeMembers.removeAll(getMethods());
		for (CtMethod<?> meth : methods) {
			addMethod(meth);
		}
		return (C) this;
	}

	@Override
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		// overridden in subclasses.
		return (C) this;
	}

	@Override
	public <C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces) {
		if (interfaces == null || interfaces.isEmpty()) {
			this.interfaces = CtElementImpl.emptySet();
			return (C) this;
		}
		if (this.interfaces == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			this.interfaces = new QualifiedNameBasedSortedSet<>();
		}
		this.interfaces.clear();
		for (CtTypeReference<?> anInterface : interfaces) {
			addSuperInterface(anInterface);
		}
		return (C) this;
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		if (getMethods().isEmpty()) {
			return Collections.emptyList();
		}
		List<CtExecutableReference<?>> l = new ArrayList<>(getMethods().size());
		for (CtExecutable<?> m : getMethods()) {
			l.add(m.getReference());
		}
		return Collections.unmodifiableList(l);
	}

	@Override
	public Collection<CtExecutableReference<?>> getAllExecutables() {
		Set<CtExecutableReference<?>> l = new HashSet<>(getDeclaredExecutables());
		if (this instanceof CtClass) {
			CtTypeReference<?> st = ((CtClass<?>) this).getSuperclass();
			if (st != null) {
				l.addAll(st.getAllExecutables());
			}
		}
		return l;
	}

	/**
	 * puts all methods of from in destination based on signatures only
	 */
	private void addAllBasedOnSignature(Set<CtMethod<?>> from, Set<CtMethod<?>> destination) {
		List<String> signatures = new ArrayList<>();
		for (CtMethod<?> m : destination) {
			signatures.add(m.getSignature());
		}

		for (CtMethod<?> m : from) {
			if (!signatures.contains(m.getSignature())) {
				destination.add(m);
			}
		}
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> l = new SignatureBasedSortedSet<>(getMethods());
		if ((getSuperclass() != null)) {
			try {
				CtType<?> t = getSuperclass().getTypeDeclaration();
				addAllBasedOnSignature(t.getAllMethods(), l);
			} catch (SpoonClassNotFoundException ignored) {
				// should not be thrown in 'noClasspath' environment (#775)
			}
		} else {
			// this is object
			addAllBasedOnSignature(getFactory().Type().get(Object.class).getMethods(), l);
		}

		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			try {
				CtType<?> t = ref.getTypeDeclaration();
				addAllBasedOnSignature(t.getAllMethods(), l);
			} catch (SpoonClassNotFoundException ignored) {
				// should not be thrown in 'noClasspath' environment (#775)
			}
		}

		return Collections.unmodifiableSet(l);
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
	public CtType<T> clone() {
		return (CtType<T>) super.clone();
	}

}
