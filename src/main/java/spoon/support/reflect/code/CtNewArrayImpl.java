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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities
		.NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY;

public class CtNewArrayImpl<T> extends CtExpressionImpl<T> implements CtNewArray<T> {
	private static final long serialVersionUID = 1L;

	List<CtExpression<Integer>> dimensionExpressions = emptyList();

	List<CtExpression<?>> expressions = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtNewArray(this);
	}

	@Override
	public List<CtExpression<Integer>> getDimensionExpressions() {
		return dimensionExpressions;
	}

	@Override
	public List<CtExpression<?>> getElements() {
		return expressions;
	}

	@Override
	public <C extends CtNewArray<T>> C setDimensionExpressions(List<CtExpression<Integer>> dimensionExpressions) {
		this.dimensionExpressions.clear();
		for (CtExpression<Integer> expr : dimensionExpressions) {
			addDimensionExpression(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtNewArray<T>> C addDimensionExpression(CtExpression<Integer> dimension) {
		if (dimensionExpressions == CtElementImpl.<CtExpression<Integer>>emptyList()) {
			dimensionExpressions = new ArrayList<CtExpression<Integer>>(
					NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		dimension.setParent(this);
		dimensionExpressions.add(dimension);
		return (C) this;
	}

	@Override
	public boolean removeDimensionExpression(CtExpression<Integer> dimension) {
		return dimensionExpressions
				!= CtElementImpl.<CtExpression<Integer>>emptyList()
				&& dimensionExpressions.remove(dimension);
	}

	@Override
	public <C extends CtNewArray<T>> C setElements(List<CtExpression<?>> expressions) {
		this.expressions.clear();
		for (CtExpression<?> expr : expressions) {
			addElement(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtNewArray<T>> C addElement(CtExpression<?> expression) {
		if (expressions == CtElementImpl.<CtExpression<?>>emptyList()) {
			this.expressions = new ArrayList<CtExpression<?>>();
		}
		expression.setParent(this);
		expressions.add(expression);
		return (C) this;
	}

	@Override
	public boolean removeElement(CtExpression<?> expression) {
		return expressions != CtElementImpl.<CtExpression<?>>emptyList() && expressions.remove(expression);
	}
}
