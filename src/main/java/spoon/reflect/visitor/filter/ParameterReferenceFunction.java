/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;

/**
 * This Query expects a {@link CtParameter} as input
 * and returns all {@link CtParameterReference}s, which refers this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtParameter param = ...;
 * param
 *   .map(new ParameterReferenceFunction())
 *   .forEach((CtParameterReference ref)->...process references...);
 * }
 * </pre>
 */
public class ParameterReferenceFunction extends LocalVariableReferenceFunction {

	public ParameterReferenceFunction() {
		super(CtParameter.class, CtParameterReference.class);
	}

	/**
	 * This constructor allows to define target parameter - the one for which this function will search for.
	 * In such case the input of mapping function represents the searching scope
	 * @param parameter - the parameter declaration which is searched in scope of input element
	 */
	public ParameterReferenceFunction(CtParameter<?> parameter) {
		super(CtParameter.class, CtParameterReference.class, parameter);
	}
}
