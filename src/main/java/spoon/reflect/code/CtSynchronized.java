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

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.EXPRESSION;


/**
 * This code element defines a <code>synchronized</code> statement.
 *
 * Example:
 * <pre>
 *    java.util.List l = new java.util.ArrayList();
 *    synchronized(l) {
 *     	System.out.println("foo");
 *    }
 * </pre>
 */
public interface CtSynchronized extends CtStatement {
	/**
	 * Gets the expression that defines the monitored object if any.
	 *
	 * @return the monitored object if defined, null otherwise
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<?> getExpression();

	/**
	 * Sets the expression that defines the monitored.
	 */
	@PropertySetter(role = EXPRESSION)
	<T extends CtSynchronized> T setExpression(CtExpression<?> expression);

	/**
	 * Gets the synchronized block.
	 */
	@PropertyGetter(role = BODY)
	CtBlock<?> getBlock();

	/**
	 * Sets the synchronized block.
	 */
	@PropertySetter(role = BODY)
	<T extends CtSynchronized> T setBlock(CtBlock<?> block);

	@Override
	CtSynchronized clone();
}
