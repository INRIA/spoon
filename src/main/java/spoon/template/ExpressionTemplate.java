/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * This class represents an expression template parameter expressed in Java.
 *
 * <p>
 * To define a new expression template parameter, you must subclass this class
 * and implement the {@link #expression()} method, which actually defines the
 * Java expression. It corresponds to a {@link spoon.reflect.code.CtExpression}.
 */
public abstract class ExpressionTemplate<T> extends AbstractTemplate<CtExpression<T>> {

	/**
	 * Returns the expression.
	 */
	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> getExpression(
			CtClass<? extends ExpressionTemplate<?>> p) {
		CtBlock<?> b = getExpressionBlock(p);
		return ((CtReturn<T>) b.getStatements().get(0)).getReturnedExpression();
	}

	private static CtBlock<?> getExpressionBlock(
			CtClass<? extends ExpressionTemplate<?>> p) {
		return p.getMethod("expression").getBody();
	}

	/**
	 * Creates a new expression template parameter.
	 */
	public ExpressionTemplate() {
	}

	/**
	 * This method must be implemented to define the template expression. The
	 * convention is that the defined expression corresponds to the expression
	 * returned by the return statement of the method.
	 */
	public abstract T expression() throws Throwable;

	@Override
	@SuppressWarnings("unchecked")
	public CtExpression<T> apply(CtType<?> targetType) {
		CtClass<? extends ExpressionTemplate<?>> c = Substitution.getTemplateCtClass(targetType, this);
		return TemplateBuilder.createPattern(
				new PatternBuilderHelper(c).setReturnExpressionOfMethod("expression").getPatternElements().get(0), this)
				.setAddGeneratedBy(isAddGeneratedBy()).substituteSingle(targetType, CtExpression.class);
	}

	public T S() {
		return null;
	}
}
