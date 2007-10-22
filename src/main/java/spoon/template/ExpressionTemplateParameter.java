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

package spoon.template;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This class represents an expression template parameter expressed in Java.
 * 
 * <p>
 * To define a new expression template parameter, you must subclass this class
 * and implement the {@link #expression()} method, which actually defines the
 * Java expression. It corresponds to a {@link spoon.reflect.code.CtExpression}.
 */
public abstract class ExpressionTemplateParameter<T> implements
		TemplateParameter<T> {

	/**
	 * Returns the expression.
	 */
	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> getExpression(
			CtClass<? extends ExpressionTemplateParameter<?>> p) {
		CtBlock<?> b = getExpressionBlock(p);
		return ((CtReturn<T>) b.getStatements().get(0)).getReturnedExpression();
	}

	@SuppressWarnings("unchecked")
	private static CtBlock<?> getExpressionBlock(
			CtClass<? extends ExpressionTemplateParameter<?>> p) {
		CtBlock b=p.getMethod("expression").getBody();
		return b;
	}

	/**
	 * Creates a new expression template parameter.
	 */
	public ExpressionTemplateParameter() {
	}

	/**
	 * This method must be implemented to define the template expression. The
	 * convention is that the defined expression corresponds to the expression
	 * returned by the return statement of the method.
	 */
	public abstract T expression() throws Throwable;

	@SuppressWarnings("unchecked")
	public CtExpression<T> getSubstitution(CtSimpleType<?> targetType) {
		CtClass<? extends ExpressionTemplateParameter<?>> c;
		CtBlock<?> b;
		c = targetType.getFactory().Template().get(this.getClass());
		if (c == null) {
			c = targetType.getFactory().Class().get(this.getClass());
		}
		if (this instanceof Template) {
			b = Substitution.substitute(targetType, (Template) this,
					getExpressionBlock(c));
		} else {
			b = targetType.getFactory().Core().clone(getExpressionBlock(c));
		}
		return ((CtReturn<T>) b.getStatements().get(0)).getReturnedExpression();
	}

	public T S() {
		return null;
	}
}
