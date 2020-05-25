/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.DIMENSION;
import static spoon.reflect.path.CtRole.EXPRESSION;

public class CtNewArrayImpl<T> extends CtExpressionImpl<T> implements CtNewArray<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = DIMENSION)
	List<CtExpression<Integer>> dimensionExpressions = emptyList();

	@MetamodelPropertyField(role = EXPRESSION)
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
		if (dimensionExpressions == null || dimensionExpressions.isEmpty()) {
			this.dimensionExpressions = CtElementImpl.emptyList();
			return (C) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, DIMENSION, this.dimensionExpressions, new ArrayList<>(this.dimensionExpressions));
		this.dimensionExpressions.clear();
		for (CtExpression<Integer> expr : dimensionExpressions) {
			addDimensionExpression(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtNewArray<T>> C addDimensionExpression(CtExpression<Integer> dimension) {
		if (dimension == null) {
			return (C) this;
		}
		if (dimensionExpressions == CtElementImpl.<CtExpression<Integer>>emptyList()) {
			dimensionExpressions = new ArrayList<>(NEW_ARRAY_DEFAULT_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY);
		}
		dimension.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, DIMENSION, this.dimensionExpressions, dimension);
		dimensionExpressions.add(dimension);
		return (C) this;
	}

	@Override
	public boolean removeDimensionExpression(CtExpression<Integer> dimension) {
		if (dimensionExpressions == CtElementImpl.<CtExpression<Integer>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, DIMENSION, dimensionExpressions, dimensionExpressions.indexOf(dimension), dimension);
		return dimensionExpressions.remove(dimension);
	}

	@Override
	public <C extends CtNewArray<T>> C setElements(List<CtExpression<?>> expressions) {
		if (expressions == null || expressions.isEmpty()) {
			this.expressions = CtElementImpl.emptyList();
			return (C) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, EXPRESSION, this.expressions, new ArrayList<>(this.expressions));
		this.expressions.clear();
		for (CtExpression<?> expr : expressions) {
			addElement(expr);
		}
		return (C) this;
	}

	@Override
	public <C extends CtNewArray<T>> C addElement(CtExpression<?> expression) {
		if (expression == null) {
			return (C) this;
		}
		if (expressions == CtElementImpl.<CtExpression<?>>emptyList()) {
			this.expressions = new ArrayList<>();
		}
		expression.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, EXPRESSION, this.expressions, expression);
		expressions.add(expression);
		return (C) this;
	}

	@Override
	public boolean removeElement(CtExpression<?> expression) {
		if (expressions == CtElementImpl.<CtExpression<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, EXPRESSION, expressions, expressions.indexOf(expression), expression);
		return expressions.remove(expression);
	}

	@Override
	public CtNewArray<T> clone() {
		return (CtNewArray<T>) super.clone();
	}
}
