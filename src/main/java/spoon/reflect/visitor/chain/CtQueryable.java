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
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Represents an object on which one can make queries.
 * It is implemented 1) by {@link CtElement} to allow creation of a new query on
 * children of an element.
 * 2) by {@link CtQuery} to allow chaining query steps.
 */
public interface CtQueryable {

	/**
	 * Query elements based on a function, the behavior depends on the return type of the function.
	 * <table>
	 * <tr><td><b>Return type of `function`</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Select elements if thereturned value of `function` is true (as for {@link Filter}).
	 * <tr><td>? extends {@link Object}<td>Send the returned value of `function` to the next step
	 * <tr><td>{@link Iterable}<td>Send each item of the collection to the next step
	 * <tr><td>{@link Object[]}<td>Send each item of the array to the next step
	 * </table><br>
	 *
	 * @param function a Function with one parameter of type I returning a value of type R
	 * @return a new query object
	 */
	<I, R> CtQuery<R> map(CtFunction<I, R> function);

	/**
	 * Recursively scans all children elements of an input element.
	 * The matched child element for which (filter.matches(element)==true) are sent to the next step.
	 * Essentially the same as {@link CtElement#getElements(Filter)} but more powerful, because it
	 * can be chained with other subsequent queries.
	 *
	 * Note: the input element (the root of the query, `this` if you're in {@link CtElement}) is also checked and may thus be also sent to the next step.
	 * The elements which throw {@link ClassCastException} during {@link Filter#matches(CtElement)}
	 * are considered as **not matching**, ie. are excluded.
	 *
	 * @param filter used to filter scanned children elements of the AST tree
	 * @return a new Query
	 */
	<T extends CtElement> CtQuery<T> filterChildren(Filter<T> filter);

}
