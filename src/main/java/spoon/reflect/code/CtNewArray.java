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

import java.util.List;

import static spoon.reflect.path.CtRole.DIMENSION;
import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element defines the inline creation of a new array.
 *
 * Example:
 * <pre>
 *     // inline creation of array content
 *     int[] x = new int[] { 0, 1, 42}
 * </pre>
 * @param <T>
 * 		type of this array (should be a array...)
 */
public interface CtNewArray<T> extends CtExpression<T> {

	/**
	 * Gets the expressions that define the array's dimensions.
	 */
	@PropertyGetter(role = DIMENSION)
	List<CtExpression<Integer>> getDimensionExpressions();

	/**
	 * Sets the expressions that define the array's dimensions.
	 */
	@PropertySetter(role = DIMENSION)
	<C extends CtNewArray<T>> C setDimensionExpressions(List<CtExpression<Integer>> dimensions);

	/**
	 * Adds a dimension expression.
	 */
	@PropertySetter(role = DIMENSION)
	<C extends CtNewArray<T>> C addDimensionExpression(CtExpression<Integer> dimension);

	/**
	 * Removes a dimension expression.
	 */
	@PropertySetter(role = DIMENSION)
	boolean removeDimensionExpression(CtExpression<Integer> dimension);

	/**
	 * Gets the initialization expressions.
	 */
	@PropertyGetter(role = EXPRESSION)
	List<CtExpression<?>> getElements();

	/**
	 * Sets the initialization expressions.
	 */
	@PropertySetter(role = EXPRESSION)
	<C extends CtNewArray<T>> C setElements(List<CtExpression<?>> expression);

	/**
	 * Adds an element.
	 */
	@PropertySetter(role = EXPRESSION)
	<C extends CtNewArray<T>> C addElement(CtExpression<?> expression);

	/**
	 * Removes an element.
	 */
	@PropertySetter(role = EXPRESSION)
	boolean removeElement(CtExpression<?> expression);

	@Override
	CtNewArray<T> clone();
}
