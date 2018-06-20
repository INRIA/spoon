/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.template.TemplateParameter;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element represents a <code>return</code> statement.
 *
 * Example:
 * <pre>
 *    Runnable r = new Runnable() {
 *     	&#64;Override
 *     	public void run() {
 *     	  return; // &lt;-- CtReturn statement
 *     	}
 *    };
 * </pre>

 */
public interface CtReturn<R> extends CtCFlowBreak, TemplateParameter<Void> {

	/**
	 * Gets the returned expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<R> getReturnedExpression();

	/**
	 * Sets the returned expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtReturn<R>> T setReturnedExpression(CtExpression<R> returnedExpression);

	@Override
	CtReturn<R> clone();
}
