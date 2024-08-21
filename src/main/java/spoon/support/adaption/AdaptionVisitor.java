/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtAbstractVisitor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A visitor that rewrites everything with a generic type according to a {@link Node} hierarchy. This class changes the
 * type arguments of cloned references and methods to what they should be after adaption.
 */
class AdaptionVisitor extends CtAbstractVisitor {

	private final DeclarationNode hierarchy;
	private CtTypeReference<?> result;

	private AdaptionVisitor(DeclarationNode hierarchy) {
		this.hierarchy = hierarchy;
	}

	@Override
	public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
		CtTypeReference<?> newBounding = adapt(wildcardReference.getBoundingType(), hierarchy);
		result = wildcardReference.clone().setBoundingType(newBounding);
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference reference) {
		if (isDeclaredOnExecutable(reference)) {
			result = reference.clone();
			return;
		}
		result = hierarchy.resolveTypeParameter(reference).orElse(reference.clone());
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		CtTypeReference<?> newArrayType = adapt(
			reference.getArrayType(),
			hierarchy
		);
		CtArrayTypeReference<?> newReference = reference.getFactory()
			.createArrayReference(newArrayType);
		for (int i = 1; i < reference.getDimensionCount(); i++) {
			newReference = reference.getFactory().createArrayReference(newReference);
		}
		result = newReference;
	}

	@Override
	public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
		List<CtTypeReference<?>> newBounds = reference.getBounds()
			.stream()
			.map(it -> adapt(it, hierarchy))
			.collect(Collectors.toList());
		result = reference.clone().setBounds(newBounds);
	}

	/**
	 * Adapts a given reference within the given hierarchy.
	 *
	 * @param reference the reference to adapt
	 * @param hierarchy the hierarchy to adapt within
	 * @return the adapted reference
	 */
	public static CtTypeReference<?> adapt(CtTypeReference<?> reference, DeclarationNode hierarchy) {
		if (!reference.isGenerics()) {
			return reference.clone();
		}
		AdaptionVisitor visitor = new AdaptionVisitor(hierarchy);
		reference.accept(visitor);

		if (visitor.result != null) {
			return visitor.result;
		}

		List<CtTypeReference<?>> newActualArguments = reference.getActualTypeArguments()
			.stream()
			.map(it -> adapt(it, hierarchy))
			.collect(Collectors.toList());
		return reference.clone().setActualTypeArguments(newActualArguments);
	}

	private static boolean isDeclaredOnExecutable(CtTypeParameterReference reference) {
		return reference.getDeclaration().getTypeParameterDeclarer() instanceof CtExecutable;
	}
}
