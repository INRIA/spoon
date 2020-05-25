/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.visitor.CtVisitor;

public class CtVariableWriteImpl<T> extends CtVariableAccessImpl<T> implements CtVariableWrite<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtVariableWrite(this);
	}

	@Override
	public CtVariableWrite<T> clone() {
		return (CtVariableWrite<T>) super.clone();
	}
}
