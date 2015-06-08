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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY;

public class CtNewArrayImpl<T> extends CtExpressionImpl<T> implements
		CtNewArray<T> {
	private static final long serialVersionUID = 1L;

	List<CtExpression<Integer>> dimensionExpressions = EMPTY_LIST();

	List<CtExpression<?>> expressions = EMPTY_LIST();

	public void accept(CtVisitor visitor) {
		visitor.visitCtNewArray(this);
	}

	public List<CtExpression<Integer>> getDimensionExpressions() {
		return dimensionExpressions;
	}

	public List<CtExpression<?>> getElements() {
		return expressions;
	}

	public void setDimensionExpressions(
			List<CtExpression<Integer>> dimensionExpressions) {
		this.dimensionExpressions.clear();
		for (CtExpression expr : dimensionExpressions) {
			addDimensionExpression(expr);
		}
	}

	@Override
	public boolean addDimensionExpression(CtExpression<Integer> dimension) {
		if (dimensionExpressions == CtElementImpl
				.<CtExpression<Integer>> EMPTY_LIST()) {
			dimensionExpressions = new ArrayList<CtExpression<Integer>>(
					NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		dimension.setParent(this);
		return dimensionExpressions.add(dimension);
	}

	@Override
	public boolean removeDimensionExpression(CtExpression<Integer> dimension) {
		return dimensionExpressions !=
				CtElementImpl.<CtExpression<Integer>>EMPTY_LIST() &&
				dimensionExpressions.remove(dimension);
	}

	public void setElements(List<CtExpression<?>> expressions) {
		this.expressions.clear();
		for (CtExpression expr: expressions) {
			addElement(expr);
		}
	}

	@Override
	public boolean addElement(CtExpression<?> expression) {
		if (expressions == CtElementImpl.<CtExpression<?>> EMPTY_LIST()) {
			this.expressions = new ArrayList<CtExpression<?>>();
		}
		expression.setParent(this);
		return expressions.add(expression);
	}

	@Override
	public boolean removeElement(CtExpression<?> expression) {
		return expressions != CtElementImpl.<CtExpression<?>>EMPTY_LIST() &&
				expressions.remove(expression);
	}


}
