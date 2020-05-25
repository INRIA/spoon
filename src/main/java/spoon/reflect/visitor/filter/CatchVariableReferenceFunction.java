/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.reference.CtCatchVariableReference;

/**
 * This Query expects a {@link CtCatchVariable} as input
 * and returns all {@link CtCatchVariableReference}s, which refers this input.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtCatchVariable var = ...;
 * var
 *   .map(new CatchVariableReferenceFunction())
 *   .forEach((CtCatchVariableReference ref)->...process references...);
 * }
 * </pre>
 */
public class CatchVariableReferenceFunction extends LocalVariableReferenceFunction {

	public CatchVariableReferenceFunction() {
		super(CtCatchVariable.class, CtCatchVariableReference.class);
	}

	public CatchVariableReferenceFunction(CtCatchVariable<?> catchVariable) {
		super(CtCatchVariable.class, CtCatchVariableReference.class, catchVariable);
	}
}
