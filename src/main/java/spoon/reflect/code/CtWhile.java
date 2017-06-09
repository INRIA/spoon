/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines a <code>while</code> loop.
 *
 * Example:
 * <pre>
 *     int x = 0;
 *     while (x!=10) {
 *         x=x+1;
 *     };
 * </pre>
 *
 */
public interface CtWhile extends CtLoop {
	/**
	 * Gets the looping boolean test expression.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<Boolean> getLoopingExpression();

	/**
	 * Sets the looping boolean test expression.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtWhile> T setLoopingExpression(CtExpression<Boolean> expression);

	@Override
	CtWhile clone();
}
