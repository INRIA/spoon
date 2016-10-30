/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import java.util.List;

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
	List<CtExpression<Integer>> getDimensionExpressions();

	/**
	 * Sets the expressions that define the array's dimensions.
	 */
	<C extends CtNewArray<T>> C setDimensionExpressions(List<CtExpression<Integer>> dimensions);

	/**
	 * Adds a dimension expression.
	 */
	<C extends CtNewArray<T>> C addDimensionExpression(CtExpression<Integer> dimension);

	/**
	 * Removes a dimension expression.
	 */
	boolean removeDimensionExpression(CtExpression<Integer> dimension);

	/**
	 * Gets the initialization expressions.
	 */
	List<CtExpression<?>> getElements();

	/**
	 * Sets the initialization expressions.
	 */
	<C extends CtNewArray<T>> C setElements(List<CtExpression<?>> expression);

	/**
	 * Adds an element.
	 */
	<C extends CtNewArray<T>> C addElement(CtExpression<?> expression);

	/**
	 * Removes an element.
	 */
	boolean removeElement(CtExpression<?> expression);

	@Override
	CtNewArray<T> clone();
}
