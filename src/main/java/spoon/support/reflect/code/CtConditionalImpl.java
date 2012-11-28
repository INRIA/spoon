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

import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtConditionalImpl<T> extends CtExpressionImpl<T> implements
		CtConditional<T> {

	private static final long serialVersionUID = 1L;

	CtExpression<T> elseExpression;

	CtExpression<Boolean> condition;

	CtExpression<T> thenExpression;

	public void accept(CtVisitor visitor) {
		visitor.visitCtConditional(this);
	}

	public CtExpression<T> getElseExpression() {
		return elseExpression;
	}

	public CtExpression<Boolean> getCondition() {
		return condition;
	}

	public CtExpression<T> getThenExpression() {
		return thenExpression;
	}

	public void setElseExpression(CtExpression<T> elseExpression) {
		this.elseExpression = elseExpression;
		elseExpression.setParent(this);
	}

	public void setCondition(CtExpression<Boolean> condition) {
		this.condition = condition;
		condition.setParent(this);
	}

	public void setThenExpression(CtExpression<T> thenExpression) {
		this.thenExpression = thenExpression;
		thenExpression.setParent(this);
	}

}
