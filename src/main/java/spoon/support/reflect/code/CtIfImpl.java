/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.code;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;

public class CtIfImpl extends CtStatementImpl implements CtIf {
	private static final long serialVersionUID = 1L;

	CtExpression<Boolean> condition;

	CtStatement elseStatement;

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
		condition.setParent(this);
		this.condition = condition;
		return (T) this;
	}

	@Override
	public <T extends CtIf> T setElseStatement(CtStatement elseStatement) {
		if (elseStatement != null) {
			elseStatement.setParent(this);
		}
		this.elseStatement = elseStatement;
		return (T) this;
	}

	@Override
	public <T extends CtIf> T setThenStatement(CtStatement thenStatement) {
		// then branch might be null: `if (condition) ;`
		if (thenStatement != null) {
			thenStatement.setParent(this);
		}
		this.thenStatement = thenStatement;
		return (T) this;
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return getFactory().Core().clone(this);
	}
}
