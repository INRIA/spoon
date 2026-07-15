/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class CtTypeParameterReferenceImpl extends CtTypeReferenceImpl<Object> implements CtTypeParameterReference {
	private static final long serialVersionUID = 1L;
	// Executable matching erases its parameters and can re-enter declaration lookup through a sibling reference.
	private static final ThreadLocal<Set<CtExecutableReference<?>>> RESOLVING_EXECUTABLE_DECLARATIONS =
			ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));
	private static final String DECLARATION_OWNER_METADATA = "spoon.typeParameterDeclarationOwner";


	public CtTypeParameterReferenceImpl() {
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		if (isParentInitialized()
				&& getParent() instanceof CtTypeParameter declaration
				&& Objects.equals(getSimpleName(), declaration.getSimpleName())) {
			rememberDeclarationOwner(declaration);
		}
		if (parent instanceof CtTypeParameter declaration
				&& Objects.equals(getSimpleName(), declaration.getSimpleName())) {
			rememberDeclarationOwner(declaration);
		}
		return super.setParent(parent);
	}

	private void rememberDeclarationOwner(CtTypeParameter declaration) {
		Object declarationOwner = declaration.getMetadata(DECLARATION_OWNER_METADATA);
		if (!(declarationOwner instanceof DeclarationOwnerIdentity)) {
			declarationOwner = new DeclarationOwnerIdentity();
			declaration.putMetadata(DECLARATION_OWNER_METADATA, declarationOwner);
		}
		putMetadata(DECLARATION_OWNER_METADATA, declarationOwner);
	}

	@Override
	public boolean isDefaultBoundingType() {
		return (getBoundingType().equals(getFactory().Type().getDefaultBoundingType()));
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeParameterReference(this);
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getActualClass() {
		return (Class<Object>) getBoundingType().getActualClass();
	}

	@Override
	@DerivedProperty
	public List<CtTypeReference<?>> getActualTypeArguments() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtActualTypeContainer> C setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments) {
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtActualTypeContainer> C addActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument) {
		return false;
	}

	@Override
	@DerivedProperty
	public CtTypeReference<?> getBoundingType() {
		CtTypeParameter typeParam = getDeclaration();
		if (typeParam != null) {
			CtTypeReference<?> typeRef = typeParam.getSuperclass();
			if (typeRef != null) {
				return typeRef;
			}
		}
		return getFactory().Type().getDefaultBoundingType();
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		// this is never annotated
		return null;
	}

	@Override
	public CtTypeParameter getDeclaration() {
		if (!isParentInitialized()) {
			return null;
		}

		CtElement typeDeclarer = this;
		CtElement parent = getParent();

		if (parent instanceof CtTypeParameter && Objects.equals(getSimpleName(), ((CtTypeParameter) parent).getSimpleName())) {
			/*
			 * a special case of newly created (unbound) CtTypeParameterReference,
			 * whose CtTypeParameter is linked as parent - to temporary remember CtTypeParameterReference bounds
			 * See ReferenceBuilder#getTypeReference(TypeBinding)
			 */
			return (CtTypeParameter) parent;
		}
		boolean nestedInTypeReference = parent instanceof CtTypeReference;
		while (parent instanceof CtTypeReference) {
			if (!parent.isParentInitialized()) {
				// we might enter in that case because of a call
				// of getSuperInterfaces() for example
				CtTypeReference typeReference = (CtTypeReference) parent;
				typeDeclarer = typeReference.getTypeDeclaration();
				if (typeDeclarer == null) {
					return null;
				}
				break;
			} else {
				parent = parent.getParent();
			}
		}
		CtTypeParameter lexicalDeclaration = nestedInTypeReference
				? findTypeParamDeclarationInParents(this)
				: null;
		CtTypeParameter fallbackDeclaration = lexicalDeclaration;
		if (lexicalDeclaration != null
				&& getMetadata(DECLARATION_OWNER_METADATA) != null
				&& hasMatchingDeclarationOwner(lexicalDeclaration)) {
			return lexicalDeclaration;
		}
		if (parent instanceof CtExecutableReference) {
			CtExecutableReference parentExec = (CtExecutableReference) parent;
			if (Objects.nonNull(parentExec.getDeclaringType())
					&& !parentExec.getDeclaringType().equals(typeDeclarer)) {
				CtElement parent2 = getExecutableDeclaration(parentExec);
				if (parent2 instanceof CtExecutable) {
					CtTypeParameter executableDeclaration = findCorrespondingTypeParameter(
							parentExec,
							(CtExecutable<?>) parent2);
					if (executableDeclaration != null && hasMatchingDeclarationOwner(executableDeclaration)) {
						return executableDeclaration;
					}
					if (executableDeclaration != null) {
						fallbackDeclaration = executableDeclaration;
					}
					typeDeclarer = parent2;
				}
			}
		}
		if (lexicalDeclaration != null && hasMatchingDeclarationOwner(lexicalDeclaration)) {
			return lexicalDeclaration;
		}

		if (!(typeDeclarer instanceof CtFormalTypeDeclarer)) {
			typeDeclarer = typeDeclarer.getParent(CtFormalTypeDeclarer.class);
		}

		// case #1: we're a type of a method parameter, a local variable, ...
		// the strategy is to look in the parents
		// collecting all formal type declarers of the hierarchy
		while (typeDeclarer != null) {
			CtTypeParameter result = findTypeParamDeclaration((CtFormalTypeDeclarer) typeDeclarer, this.getSimpleName());
			if (result != null) {
				if (hasMatchingDeclarationOwner(result)) {
					return result;
				}
				if (fallbackDeclaration == null) {
					fallbackDeclaration = result;
				}
			}
			typeDeclarer = typeDeclarer.getParent(CtFormalTypeDeclarer.class);
		}
		return fallbackDeclaration;
	}

	private boolean hasMatchingDeclarationOwner(CtTypeParameter declaration) {
		Object expectedOwner = getMetadata(DECLARATION_OWNER_METADATA);
		if (expectedOwner instanceof DeclarationOwnerIdentity) {
			return expectedOwner == declaration.getMetadata(DECLARATION_OWNER_METADATA);
		}
		String actualOwner = declarationOwner(declaration);
		return expectedOwner == null || expectedOwner.equals(actualOwner);
	}

	private String declarationOwner(CtTypeParameter declaration) {
		if (!declaration.isParentInitialized()) {
			return null;
		}
		CtElement owner = declaration.getParent();
		if (!(owner instanceof CtFormalTypeDeclarer formalTypeDeclarer)) {
			return null;
		}
		int rank = formalTypeDeclarer.getFormalCtTypeParameters().indexOf(declaration);
		if (owner instanceof CtExecutable<?> executable) {
			CtType<?> declaringType = executable.getParent(CtType.class);
			if (declaringType == null) {
				return null;
			}
			String sourceOwner = sourceDeclarationOwner("M", executable, rank);
			if (sourceOwner != null) {
				return sourceOwner;
			}
			return "M|" + normalizedTypeName(declaringType.getQualifiedName())
					+ "|" + executable.getSimpleName()
					+ "|" + executable.getParameters().size()
					+ "|-1"
					+ "|" + rank;
		}
		if (owner instanceof CtType<?> type) {
			String sourceOwner = sourceDeclarationOwner("T", type, rank);
			if (sourceOwner != null) {
				return sourceOwner;
			}
			return "T|" + normalizedTypeName(type.getQualifiedName()) + "|" + rank;
		}
		return null;
	}

	private String sourceDeclarationOwner(String kind, CtElement declaration, int rank) {
		if (!declaration.getPosition().isValidPosition()) {
			return null;
		}
		File sourceFile = declaration.getPosition().getFile();
		String sourcePath = sourceFile == null
				? virtualSourcePath(declaration)
				: sourceFile.getPath();
		if (sourcePath == null) {
			return null;
		}
		return kind + "|" + sourcePath.replace('\\', '/')
				+ "|" + declaration.getPosition().getSourceStart()
				+ "|" + rank;
	}

	private String virtualSourcePath(CtElement declaration) {
		var compilationUnit = declaration.getPosition().getCompilationUnit();
		for (var entry : declaration.getFactory().CompilationUnit().getMap().entrySet()) {
			if (entry.getValue() == compilationUnit) {
				return entry.getKey();
			}
		}
		return null;
	}

	private String normalizedTypeName(String qualifiedName) {
		return qualifiedName.replace('$', '.');
	}

	private static final class DeclarationOwnerIdentity implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	private CtElement getExecutableDeclaration(CtExecutableReference<?> executableReference) {
		Set<CtExecutableReference<?>> resolving = RESOLVING_EXECUTABLE_DECLARATIONS.get();
		if (!resolving.add(executableReference)) {
			return null;
		}
		try {
			return executableReference.getExecutableDeclaration();
		} finally {
			resolving.remove(executableReference);
			if (resolving.isEmpty()) {
				RESOLVING_EXECUTABLE_DECLARATIONS.remove();
			}
		}
	}

	private CtTypeParameter findCorrespondingTypeParameter(
			CtExecutableReference<?> executableReference,
			CtExecutable<?> executableDeclaration) {
		Deque<Pair<CtTypeReference<?>, CtTypeReference<?>>> candidates = new ArrayDeque<>();
		// Push siblings in reverse so the worklist preserves the previous depth-first search order.
		List<CtTypeReference<?>> referenceParameters = executableReference.getParameters();
		int parameterCount = Math.min(referenceParameters.size(), executableDeclaration.getParameters().size());
		for (int index = parameterCount - 1; index >= 0; index--) {
			candidates.push(Pair.of(
					referenceParameters.get(index),
					executableDeclaration.getParameters().get(index).getType()));
		}
		candidates.push(Pair.of(executableReference.getType(), executableDeclaration.getType()));

		while (!candidates.isEmpty()) {
			Pair<CtTypeReference<?>, CtTypeReference<?>> candidate = candidates.pop();
			CtTypeReference<?> referenceType = candidate.getLeft();
			CtTypeReference<?> declarationType = candidate.getRight();
			if (referenceType == null || declarationType == null) {
				continue;
			}
			if (referenceType == this) {
				if (declarationType instanceof CtTypeParameterReference typeParameter
						&& declarationType.getSimpleName().equals(getSimpleName())) {
					CtTypeParameter declaration = findTypeParamDeclarationInParents(typeParameter);
					return declaration != null ? declaration : typeParameter.getDeclaration();
				}
				continue;
			}
			if (referenceType instanceof CtWildcardReference referenceWildcard
					&& declarationType instanceof CtWildcardReference declarationWildcard) {
				if (referenceWildcard.isUpper() == declarationWildcard.isUpper()) {
					candidates.push(Pair.of(
							referenceWildcard.getBoundingType(), declarationWildcard.getBoundingType()));
				}
				continue;
			}
			if (referenceType instanceof CtArrayTypeReference<?> referenceArray
					&& declarationType instanceof CtArrayTypeReference<?> declarationArray) {
				candidates.push(Pair.of(
						referenceArray.getComponentType(), declarationArray.getComponentType()));
				continue;
			}
			if (referenceType instanceof CtWildcardReference
					|| declarationType instanceof CtWildcardReference
					|| referenceType instanceof CtArrayTypeReference
					|| declarationType instanceof CtArrayTypeReference
					|| !Objects.equals(referenceType.getQualifiedName(), declarationType.getQualifiedName())) {
				continue;
			}

			List<CtTypeReference<?>> referenceArguments = referenceType.getActualTypeArguments();
			List<CtTypeReference<?>> declarationArguments = declarationType.getActualTypeArguments();
			int argumentCount = Math.min(referenceArguments.size(), declarationArguments.size());
			for (int index = argumentCount - 1; index >= 0; index--) {
				candidates.push(Pair.of(
						referenceArguments.get(index),
						declarationArguments.get(index)));
			}
			candidates.push(Pair.of(
					referenceType.getDeclaringType(), declarationType.getDeclaringType()));
		}
		return null;
	}

	private CtTypeParameter findTypeParamDeclarationInParents(CtElement element) {
		CtFormalTypeDeclarer typeDeclarer = element.getParent(CtFormalTypeDeclarer.class);
		return findTypeParamDeclarationInHierarchy(typeDeclarer);
	}

	private CtTypeParameter findTypeParamDeclarationInHierarchy(CtFormalTypeDeclarer typeDeclarer) {
		while (typeDeclarer != null) {
			CtTypeParameter result = findTypeParamDeclaration(typeDeclarer, getSimpleName());
			if (result != null) {
				return result;
			}
			typeDeclarer = ((CtElement) typeDeclarer).getParent(CtFormalTypeDeclarer.class);
		}
		return null;
	}

	private CtTypeParameter findTypeParamDeclaration(CtFormalTypeDeclarer type, String refName) {
		for (CtTypeParameter typeParam : type.getFormalCtTypeParameters()) {
			if (typeParam.getSimpleName().equals(refName)) {
				return typeParam;
			}
		}
		return null;
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		return getDeclaration();
	}

	@Override
	public CtTypeReference<?> getTypeErasure() {
		CtTypeParameter typeParam = getDeclaration();
		if (typeParam == null) {
			throw new SpoonException("Cannot resolve type erasure of the type parameter reference, which is not able to found it's declaration.");
		}
		return typeParam.getTypeErasure();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getTypeDeclaration().isSubtypeOf(type);
	}

	@Override
	public CtTypeParameterReference clone() {
		return (CtTypeParameterReference) super.clone();
	}

	@Override
	public boolean isGenerics() {
		if (getDeclaration() instanceof CtTypeParameter) {
			return true;
		}
		return getBoundingType() != null && getBoundingType().isGenerics();
	}

	protected boolean isWildcard() {
		return false;
	}

	@Override
	public boolean isSimplyQualified() {
		return false;
	}

	@Override
	@UnsettableProperty
	public CtTypeParameterReferenceImpl setSimplyQualified(boolean isSimplyQualified) {
		return this;
	}
}
