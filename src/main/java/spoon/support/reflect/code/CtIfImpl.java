/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;

public class CtIfImpl extends CtStatementImpl implements CtIf {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CONDITION)
	CtExpression<Boolean> condition;

	@MetamodelPropertyField(role = ELSE)
	CtStatement elseStatement;

	@MetamodelPropertyField(role = THEN)
	CtStatement thenStatement;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtIf(this);
	}

	@Override
	public CtExpression<Boolean> getCondition() {
		return condition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends CtStatement> S getElseStatement() {
		return (S) elseStatement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends CtStatement> S getThenStatement() {
		return (S) thenStatement;
	}

	@Override
	public <T extends CtIf> T setCondition(CtExpression<Boolean> condition) {
		if (condition != null) {
			condition.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CONDITION, condition, this.condition);
		this.condition = condition;
		return (T) this;
	}

	@Override
	public <T extends CtIf> T setElseStatement(CtStatement elseStatement) {
		if (elseStatement != null) {
			elseStatement.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, ELSE, elseStatement, this.elseStatement);
		this.elseStatement = elseStatement;
		return (T) this;
	}

	@Override
	public <T extends CtIf> T setThenStatement(CtStatement thenStatement) {
		// then branch might be null: `if (condition) ;`
		if (thenStatement != null) {
			thenStatement.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, THEN, thenStatement, this.thenStatement);
		this.thenStatement = thenStatement;
		return (T) this;
	}

	@Override
	public CtIf clone() {
		return (CtIf) super.clone();
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return clone();
	}
}
