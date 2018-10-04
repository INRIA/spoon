/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.pattern;

import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
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
	 * Main method to generate a new AST made from substituting of parameters by values in `params`
	 * @param valueType - the expected type of returned items
	 * @param params - the substitution parameters, it can be CtElement, primitive literals like String, Integer, ... and or List or Set of them.
	 * @return List of generated elements
	 */
	<T extends CtElement> List<T> generate(Class<T> valueType, Map<String, Object> params);

	/** Utility method that provides the same feature as {@link #generate(Class, Map)}, but with a {@link ImmutableMap} as parameter (a Spoon elegant utility type) */
	<T extends CtElement> List<T> generate(Class<T> valueType, ImmutableMap params);

	/**
	 * Adds type members (fields and methods) to `targetType`.
	 *
	 * The root elements of the pattern must be type members.
	 *
	 * @param valueType the type of generated elements
	 * @param params the pattern parameters
	 * @param targetType the existing type, which will contain the added generated {@link CtElement}s
	 * @return List of generated elements
	 */
	<T extends CtTypeMember> List<T> addToType(Class<T> valueType, Map<String, Object> params, CtType<?> targetType);

	/**
	 * Generates type with qualified name `typeQualifiedName` the provided `params`.
	 *
	 * Note: the root element of pattern must be one type.
	 *
	 * @param typeQualifiedName the qualified name of to be generated type
	 * @param params the pattern parameters
	 * @return the generated type
	 */
	<T extends CtType<?>> T generateType(String typeQualifiedName, Map<String, Object> params);

}
