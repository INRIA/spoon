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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.factory.Factory;

public class Builder {

	private final Factory factory;

	public Builder(Factory factory) {
		this.factory = factory;
	}

	/*****************/
	/**      If     **/
	/*****************/
	public IfBuilder<?> If(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return new IfBuilder<>(factory, e.build());
	}

	public IfBuilder<?> If(CtExpression e) {
		return new IfBuilder<>(factory, e);
	}

	/*****************/
	/**     Try     **/
	/*****************/
	public TryBuilder<?> Try() {
		return new TryBuilder(factory);
	}

	public CatchBuilder<?> Catch() {
		return new CatchBuilder(factory);
	}

	/*****************/
	/**    Method   **/
	/*****************/
	public MethodBuilder<?> Method(String name) {
		return new MethodBuilder(factory, name);
	}

	/*****************/
	/**    Binary   **/
	/*****************/
	public BinaryOperatorBuilder<?> Binary(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return Binary(e.build());
	}

	public BinaryOperatorBuilder<?> Binary(CtExpression e) {
		return new BinaryOperatorBuilder(factory, e);
	}

	/*****************/
	/**    Unary    **/
	/*****************/
	public UnaryOperatorBuilder<?> Increment(CtExpression e) {
		return new UnaryOperatorBuilder(factory, e, UnaryOperatorKind.POSTINC);
	}

	public UnaryOperatorBuilder<?> Increment(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return Increment(e.build());
	}

	public UnaryOperatorBuilder<?> Decrement(CtExpression e) {
		return new UnaryOperatorBuilder(factory, e, UnaryOperatorKind.POSTDEC);
	}

	public UnaryOperatorBuilder<?> Decrement(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return Decrement(e.build());
	}

	public UnaryOperatorBuilder<?> Not(CtExpression e) {
		return new UnaryOperatorBuilder(factory, e, UnaryOperatorKind.NOT);
	}

	public UnaryOperatorBuilder<?> Not(AbsBuilder<? extends CtExpression, ?, ?> e) {
		return Not(e.build());
	}

	/*****************/
	/**   Literal   **/
	/*****************/
	public LiteralBuilder<?> Literal(Object e) {
		return new LiteralBuilder(factory, e);
	}
}
