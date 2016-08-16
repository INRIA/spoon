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
package spoon.reflect.builder;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;

public class BinaryOperatorBuilder<P extends AbsBuilder<?, ?, ?>> extends
		AbsBuilder<CtBinaryOperator, BinaryOperatorBuilder<P>, P> {

	public BinaryOperatorBuilder(Factory factory, CtExpression e) {
		super(factory, factory.Core().createBinaryOperator());
		getElement().setLeftHandOperand(e);
	}

	public BinaryOperatorBuilder greaterEquals(CtExpression e) {
		addHand(e, BinaryOperatorKind.GE);
		return this;
	}

	public BinaryOperatorBuilder greaterEquals(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return greaterEquals(e.build());
	}

	public BinaryOperatorBuilder greater(CtExpression e) {
		addHand(e, BinaryOperatorKind.GT);
		return this;
	}

	public BinaryOperatorBuilder greater(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return greater(e.build());
	}

	public BinaryOperatorBuilder lowerEquals(CtExpression e) {
		addHand(e, BinaryOperatorKind.LE);
		return this;
	}

	public BinaryOperatorBuilder lowerEquals(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return lowerEquals(e.build());
	}

	public BinaryOperatorBuilder lower(CtExpression e) {
		addHand(e, BinaryOperatorKind.LT);
		return this;
	}

	public BinaryOperatorBuilder lower(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return lower(e.build());
	}

	public BinaryOperatorBuilder division(CtExpression e) {
		addHand(e, BinaryOperatorKind.DIV);
		return this;
	}

	public BinaryOperatorBuilder division(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return multiplication(e.build());
	}

	public BinaryOperatorBuilder multiplication(CtExpression e) {
		addHand(e, BinaryOperatorKind.MUL);
		return this;
	}

	public BinaryOperatorBuilder multiplication(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return multiplication(e.build());
	}

	public BinaryOperatorBuilder different(CtExpression e) {
		addHand(e, BinaryOperatorKind.NE);
		return this;
	}

	public BinaryOperatorBuilder different(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return different(e.build());
	}

	public BinaryOperatorBuilder equals(CtExpression e) {
		addHand(e, BinaryOperatorKind.EQ);
		return this;
	}

	public BinaryOperatorBuilder equals(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return equals(e.build());
	}

	public BinaryOperatorBuilder minus(CtExpression e) {
		addHand(e, BinaryOperatorKind.MINUS);
		return this;
	}

	public BinaryOperatorBuilder minus(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return minus(e.build());
	}

	public BinaryOperatorBuilder plus(CtExpression e) {
		addHand(e, BinaryOperatorKind.PLUS);
		return this;
	}

	public BinaryOperatorBuilder plus(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return plus(e.build());
	}

	public BinaryOperatorBuilder and(CtExpression e) {
		addHand(e, BinaryOperatorKind.AND);
		return this;
	}

	public BinaryOperatorBuilder and(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return and(e.build());
	}

	public BinaryOperatorBuilder or(CtExpression e) {
		addHand(e, BinaryOperatorKind.AND);
		return this;
	}

	public BinaryOperatorBuilder or(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return or(e.build());
	}


	private void addHand(CtExpression e, BinaryOperatorKind operator) {
		if (getElement().getRightHandOperand() == null) {
			getElement().setRightHandOperand(e);
		} else {
			CtBinaryOperator binaryOperator = getFactory().Core().createBinaryOperator();
			binaryOperator.setLeftHandOperand(getElement());
			binaryOperator.setRightHandOperand(e);
			setElement(binaryOperator);
		}
		getElement().setKind(operator);
	}

	public CtBinaryOperator build() {
		return getElement();
	}
}
