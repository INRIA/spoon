/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtPattern;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

import java.io.Serial;

public class CtCasePatternImpl extends CtExpressionImpl<Void> implements CtCasePattern {
	@Serial
	private static final long serialVersionUID = 1L;
	@MetamodelPropertyField(role = CtRole.PATTERN)
	private CtPattern pattern;

	@Override
	public CtPattern getPattern() {
		return pattern;
	}

	@Override
	public CtCasePattern setPattern(CtPattern pattern) {
		if (pattern != null) {
			pattern.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener()
			.onObjectUpdate(this, CtRole.PATTERN, pattern, this.pattern);
		this.pattern = pattern;
		return this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCasePattern(this);
	}

	@Override
	public CtCasePattern clone() {
		return (CtCasePattern) super.clone();
	}
}
