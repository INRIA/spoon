/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import java.util.List;
import java.util.Optional;
import spoon.SpoonException;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

class DeclarationNode extends Node {

	private final List<CtTypeParameter> formalArguments;

	protected DeclarationNode(CtTypeReference<?> inducedBy, List<CtTypeParameter> formalArguments) {
		super(inducedBy);
		this.formalArguments = formalArguments;
	}

	@Override
	public boolean isGlueNode() {
		return false;
	}

	@Override
	public Optional<CtTypeReference<?>> resolveTypeParameter(CtTypeParameterReference reference) {
		String name = reference.getSimpleName();

		// We try to find a glue node below us to delegate to. Glue nodes do the mapping so we can just
		// pass it on unchanged.
		Optional<Node> glueNode = lowerNodes.stream()
				.filter(it -> it.inducedBy.getQualifiedName().equals(this.inducedBy.getQualifiedName()))
				.findFirst();

		if (glueNode.isPresent()) {
			return glueNode.get().resolveTypeParameter(reference);
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
	protected String argumentsForToString() {
		return "\"Formal\": " + quote(formalArguments);
	}
}
