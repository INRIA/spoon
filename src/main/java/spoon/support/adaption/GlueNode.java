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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

class GlueNode extends Node {

	private final List<CtTypeReference<?>> actualArguments;

	protected GlueNode(CtTypeReference<?> inducedBy, TypeAdaptor typeAdaptor,
			List<CtTypeReference<?>> actualArguments) {
		super(inducedBy, typeAdaptor);
		this.actualArguments = actualArguments;
	}

	@Override
	public boolean isGlueNode() {
		return true;
	}

	@Override
	public Optional<CtTypeReference<?>> resolveTypeParameter(CtTypeParameterReference reference) {
		String name = reference.getSimpleName();

		CtType<?> typeDeclaration = inducedBy.getTypeDeclaration();

		for (int i = 0; i < typeDeclaration.getFormalCtTypeParameters().size(); i++) {
			CtTypeParameter parameter = typeDeclaration.getFormalCtTypeParameters().get(i);

			if (!parameter.getSimpleName().equals(name)) {
				continue;
			}

			CtTypeReference<?> actualArgument = actualArguments.get(i);
			if (!actualArgument.isGenerics()) {
				return Optional.of(actualArgument.clone());
			}

			// Follow a random child, there should be only one in most cases
			return Optional.of(AdaptionVisitor.adapt(actualArgument, lowerNodes.iterator().next()));
		}

		// We might not find it at all!
		return Optional.empty();
	}

	@Override
	protected String argumentsForToString() {
		return "\"Actual\": " + quote(actualArguments);
	}
}
