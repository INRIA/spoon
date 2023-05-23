/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import spoon.SpoonException;
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

class DeclarationNode {

	private final List<CtTypeParameter> formalArguments;
	private final CtTypeReference<?> inducedBy;
	private final Collection<GlueNode> children;

	protected DeclarationNode(CtTypeReference<?> inducedBy) {
		this.inducedBy = inducedBy;
		this.formalArguments = inducedBy.getTypeDeclaration().getFormalCtTypeParameters();

		this.children = new ArrayList<>();
	}

	/**
	 * Adds a glue node as a child of this declaration node.
	 *
	 * @param child the node to add as a child
	 */
	public void addChild(GlueNode child) {
		children.add(child);
	}

	/**
	 * Checks if this node is induced by the passed type.
	 *
	 * @param type the type to check against
	 * @return true if the qualified names match and this node is induced by the passed type
	 */
	public boolean inducedBy(CtType<?> type) {
		return inducedBy.getQualifiedName().equals(type.getQualifiedName());
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

		// We try to find a glue node below us to delegate to. Glue nodes do the mapping so we can just
		// pass it on unchanged.
		if (!children.isEmpty()) {
			// We pick a random child. Well-typed programs will converge to the same solution, no matter
			// which path we pick.
			return children.iterator().next().resolveTypeParameter(reference);
		}

		// If we have no glue node, we need to actually resolve the type parameter as we reached the
		// end of the chain.
		Optional<CtTypeReference<?>> foo = formalArguments.stream()
			.filter(it -> it.getSimpleName().equals(name))
			.findFirst()
			.map(CtTypeParameter::getReference)
			.or(() -> findTypeParameterByName(name))
			.map(it -> it);
		if (foo.isPresent()) {
			return foo;
		}
		throw new SpoonException(
			"Could not find declaration of formal type parameter" + name
				+ " in " + formalArguments
				+ " for " + inducedBy
		);
	}

	/**
	 * Finds a type parameter in the type referenced by {@code inducedBy} or any enclosing type by its
	 * name.
	 *
	 * @param name the name of the type parameter
	 * @return the parameter, if found
	 */
	private Optional<CtTypeParameterReference> findTypeParameterByName(String name) {
		CtType<?> currentType = inducedBy.getTypeDeclaration();
		while (currentType != null) {
			Optional<CtTypeParameterReference> parameter = currentType.getFormalCtTypeParameters()
				.stream()
				.filter(it -> it.getSimpleName().equals(name))
				.map(CtTypeParameter::getReference)
				.findFirst();

			if (parameter.isPresent()) {
				return parameter;
			}
			currentType = currentType.getDeclaringType();
		}

		return Optional.empty();
	}

	@Override
	public String toString() {
		String result = "{\n";
		result += "  " + quote("String") + ": " + quote(inducedBy.getQualifiedName()) + ",\n";
		result += "  " + quote("Formal") + ": " + toJsonLikeArray(formalArguments);

		if (!children.isEmpty()) {
			result += ",\n  [\n";
			StringJoiner children = new StringJoiner("\n");
			for (GlueNode node : this.children) {
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
