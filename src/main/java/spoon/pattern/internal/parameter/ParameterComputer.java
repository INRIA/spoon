/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import spoon.pattern.internal.ResultHolder;

/**
 * Computes a value of {@link ComputedParameterInfo}
 * I - type of input value
 * O - type of computed value
 */
public interface ParameterComputer {

	/**
	 * @return user friendly name of this computer
	 */
	String getName();

	/**
	 * @return holder for input value
	 */
	ResultHolder<?> createInputHolder();

	/**
	 * @param outputHolder holds result of computation
	 * @param inputHolder holds input of computation
	 */
	void computeValue(ResultHolder<Object> outputHolder, ResultHolder<?> inputHolder);
}
