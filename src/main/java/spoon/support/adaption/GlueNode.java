/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static spoon.support.adaption.NodePrintHelper.quote;
import static spoon.support.adaption.NodePrintHelper.toJsonLikeArray;

class GlueNode {

	private final List<CtTypeReference<?>> actualArguments;
	private final CtTypeReference<?> inducedBy;
	private final Collection<DeclarationNode> children;

	/**
	 * Creates a new glue node for the given reference.
	 *
	 * @param inducedBy the reference this node is built from
	 */
	GlueNode(CtTypeReference<?> inducedBy) {
		this.inducedBy = inducedBy;
		this.actualArguments = inducedBy.getActualTypeArguments();

		this.children = new ArrayList<>();
	}

	/**
	 * Adds a declaration node as a child of this glue node.
	 *
	 * @param child the node to add as a child
	 */
	public void addChild(DeclarationNode child) {
		children.add(child);
	}

	/**
	 * Checks whether this glue node is induced by the same type as the passed reference. Does not compare type arguments.
	 *
	 * @param reference the reference to check against
	 * @return true if this glue node is induced by the same type as the passed reference
	 */
	public boolean isInducedBy(CtTypeReference<?> reference) {
		return reference.getQualifiedName().equals(inducedBy.getQualifiedName());
	}

	/**
	 * Resolves a type parameter within this node's hierarchy. This is done by walking down the chain
	 * until we encounter a concrete type or reach the end. If we can not find a mapping, empty is
	 * returned. If we can't find the declaration for a formal parameter however, an exception is
	 * thrown as type resolution is longer possible.
	 *
	 * @param reference the reference to resolve
	 * @return the resolved reference if found
	 */
	public Optional<CtTypeReference<?>> resolveTypeParameter(CtTypeParameterReference reference) {
		String name = reference.getSimpleName();

		CtType<?> typeDeclaration = inducedBy.getTypeDeclaration();

		for (int i = 0; i < typeDeclaration.getFormalCtTypeParameters().size(); i++) {
			CtTypeParameter parameter = typeDeclaration.getFormalCtTypeParameters().get(i);

			if (!parameter.getSimpleName().equals(name)) {
				continue;
			}

			// We were used as a rawtype. Redirect queries to java.lang.Object.
			if (actualArguments.isEmpty()) {
				return Optional.of(reference.getFactory().Class().get(Object.class).getReference());
			}

			CtTypeReference<?> actualArgument = actualArguments.get(i);
			if (!actualArgument.isGenerics()) {
				return Optional.of(actualArgument.clone());
			}

			// Follow a random child, there should be only one in most cases
			return Optional.of(AdaptionVisitor.adapt(actualArgument, children.iterator().next()));
		}

		// We might not find it at all!
		return Optional.empty();
	}

	@Override
	public String toString() {
		String result = "{\n";
		result += "  " + quote("String") + ": " + quote(inducedBy.getQualifiedName()) + ",\n";
		result += "  " + quote("Actual") + ": " + toJsonLikeArray(actualArguments);

		if (!children.isEmpty()) {
			result += ",\n  [\n";
			StringJoiner children = new StringJoiner(",\n");
			for (DeclarationNode node : this.children) {
				children.add(node.toString());
			}
			result += children.toString().lines().map(it -> "    " + it).collect(Collectors.joining("\n")) + "\n";
			result += "  ]\n";
		} else {
			result += "\n";
		}

		result += "}";
		return result;
	}
}
