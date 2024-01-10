/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;

public class CtTypePatternImpl extends CtExpressionImpl<Void> implements CtTypePattern {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.VARIABLE)
	private CtLocalVariable<?> variable;

	@Override
	public CtLocalVariable<?> getVariable() {
		return this.variable;
	}

	@Override
	public CtTypePattern setVariable(CtLocalVariable<?> variable) {
		if (variable != null) {
			variable.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener()
				.onObjectUpdate(this, CtRole.VARIABLE, variable, this.variable);
		this.variable = variable;
		return this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypePattern(this);
	}

	@Override
	public CtTypePattern clone() {
		return (CtTypePattern) super.clone();
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		if (parent != null && !(parent instanceof CtBinaryOperator<?> || parent instanceof CtPattern || parent instanceof CtCasePattern)) {
			throw new SpoonException("type pattern can only be used in an instanceof binary operator or a case pattern (was " + parent.getClass() + ")");
		}
		return super.setParent(parent);
	}
}
