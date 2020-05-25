/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

/**
 * Use in {@link CtQuery#failurePolicy(QueryFailurePolicy)} to define how to handle failure
 */
public enum QueryFailurePolicy {
	/**Throws ClassCastException when output type of previous step cannot be assigned to input type of next step*/
	FAIL,
	/**when output type of previous step cannot be assigned to input type of next step then such output is ignored*/
	IGNORE
}
