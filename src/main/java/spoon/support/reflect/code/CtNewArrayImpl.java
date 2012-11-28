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

import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ChildList;

public class CtNewArrayImpl<T> extends CtExpressionImpl<T> implements
		CtNewArray<T> {
	private static final long serialVersionUID = 1L;

	List<CtExpression<Integer>> dimensionExpressions = new ChildList<CtExpression<Integer>>(this);

	List<CtExpression<?>> expression = new ChildList<CtExpression<?>>(this);

	public void accept(CtVisitor visitor) {
		visitor.visitCtNewArray(this);
	}

	public List<CtExpression<Integer>> getDimensionExpressions() {
		return dimensionExpressions;
	}

	public List<CtExpression<?>> getElements() {
		return expression;
	}

	public void setDimensionExpressions(
			List<CtExpression<Integer>> dimensionExpressions) {
		this.dimensionExpressions = new ChildList<CtExpression<Integer>>(dimensionExpressions,this);
	}

	public void setElements(List<CtExpression<?>> expression) {
		this.expression = new ChildList<CtExpression<?>>(expression,this);
	}
}
