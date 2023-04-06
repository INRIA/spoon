/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.Experimental;
import spoon.support.util.ImmutableMap;

/**
 * Generates code from patterns. The core idea is to replace pattern parameters by objects.
 */
@Experimental
public interface Generator {
	/**
	 * @return a {@link Factory}, which has to be used to generate instances
	 */
	Factory getFactory();


	/**
	 * Main method to generate a new AST made from substituting of parameters by
	 * the values in `params`
	 * @param params - the substitution parameters, it can be CtElement, primitive literals like String, Integer, ... and or List or Set of them.
	 * @return List of generated elements
	 */
	<T extends CtElement> List<T> generate(Map<String, Object> params);

	/** Utility method that provides the same feature as {@link #generate(Map)}, but with a {@link ImmutableMap} as parameter (a Spoon elegant utility type) */
	<T extends CtElement> List<T> generate(ImmutableMap params);

	/**
	 * Generates a type with qualified name `typeQualifiedName`
	 * by replacing the generator parameters with the arguments given in `params`.
	 *
	 * Note: the root element of pattern must be a CtType.
	 *
	 * @param typeQualifiedName the qualified name of to be generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	<T extends CtType<?>> T generate(String typeQualifiedName, Map<String, Object> params);

}
