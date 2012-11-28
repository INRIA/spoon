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
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.CtVisitor;

public class CtThrowImpl extends CtStatementImpl implements CtThrow {
	private static final long serialVersionUID = 1L;

	CtExpression<? extends Throwable> throwExpression;

	public CtExpression<? extends Throwable> getThrownExpression() {
		return throwExpression;
	}

	public void setThrownExpression(CtExpression<? extends Throwable> expression) {
		this.throwExpression = expression;
		throwExpression.setParent(this);
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtThrow(this);
	}

	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

}
