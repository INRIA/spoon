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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;

public class CtUnaryOperatorImpl<T> extends CtExpressionImpl<T> implements
		CtUnaryOperator<T> {
	private static final long serialVersionUID = 1L;

	UnaryOperatorKind kind;

	String label;

	CtExpression<T> operand;

	public void accept(CtVisitor visitor) {
		visitor.visitCtUnaryOperator(this);
	}

	public CtExpression<T> getOperand() {
		return operand;
	}

	public UnaryOperatorKind getKind() {
		return kind;
	}

	public String getLabel() {
		return label;
	}

	public void insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
	}

	public void insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
	}

	public void insertAfter(CtStatementList<?> statements) {
		CtStatementImpl.insertAfter(this, statements);
	}

	public void insertBefore(CtStatementList<?> statements) {
		CtStatementImpl.insertBefore(this, statements);
	}

	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList<?>) element);
		} else {
			super.replace(element);
		}
	}

	public void setOperand(CtExpression<T> expression) {
		this.operand = expression;
		operand.setParent(this);
	}

	public void setKind(UnaryOperatorKind kind) {
		this.kind = kind;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
