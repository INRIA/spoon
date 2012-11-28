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

import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtAssertImpl<T> extends CtStatementImpl implements CtAssert<T> {
	private static final long serialVersionUID = 1L;

	public CtExpression<Boolean> getAssertExpression() {
		return asserted;
	}

	CtExpression<Boolean> asserted;

	CtExpression<T> value;

	public void setAssertExpression(CtExpression<Boolean> asserted) {
		this.asserted = asserted;
		asserted.setParent(this);
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtAssert(this);
	}

	public CtExpression<T> getExpression() {
		return value;
	}

	public void setExpression(CtExpression<T> value) {
		this.value = value;
		value.setParent(this);
	}

}
