/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.CtVisitor;

public class CtIfImpl extends CtStatementImpl implements CtIf {
	private static final long serialVersionUID = 1L;

	CtExpression<Boolean> condition;

	CtStatement elseStatement;

	CtStatement thenStatement;

	public void accept(CtVisitor visitor) {
		visitor.visitCtIf(this);
	}

	public CtExpression<Boolean> getCondition() {
		return condition;
	}

	public CtStatement getElseStatement() {
		return elseStatement;
	}

	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

	public CtStatement getThenStatement() {
		return thenStatement;
	}

	public Void S() {
		return null;
	}

	public void setCondition(CtExpression<Boolean> condition) {
		this.condition = condition;
		this.condition.setParent(this);
	}

	public void setElseStatement(CtStatement elseStatement) {
		this.elseStatement = elseStatement;
		this.elseStatement.setParent(this);
	}

	public void setThenStatement(CtStatement thenStatement) {
		this.thenStatement = thenStatement;
		this.thenStatement.setParent(this);
	}

}
