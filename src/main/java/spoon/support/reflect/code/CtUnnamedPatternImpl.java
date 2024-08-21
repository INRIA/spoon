/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;

public class CtUnnamedPatternImpl extends CtExpressionImpl<Void> implements CtUnnamedPattern {


	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtUnnamedPattern(this);
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		if (parent != null && !(parent instanceof CtRecordPattern)) {
			throw new SpoonException("unnamed pattern can only be used in a record pattern");
		}
		return super.setParent(parent);
	}

	@Override
	public CtUnnamedPattern clone() {
		return (CtUnnamedPattern) super.clone();
	}
}
