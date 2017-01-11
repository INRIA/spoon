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
 * It is implemented
 * <ol>
 * <li> by {@link CtElement} to allow creation of a new query on
 * children of an element.
 * <li> by {@link CtQuery} to allow chaining query steps.
 * </ol>
 * @param <T> the type of returned query
 */
public interface CtQueryable<T> {

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
	<I, R> T map(CtFunction<I, R> function);

	/**
	 * Query elements based on a CtQueryStep, which supports efficient implementation of CtScanner based queries,
	 * which produces thousands of mapping output elements.
	 *
	 * @param queryStep
	 * @return the created QueryStep, which is the new last step of the query
	 */
	<I> T map(CtConsumableFunction<I> queryStep);

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
	<R extends CtElement> T filterChildren(Filter<R> filter);

	/**
	 * Defines helper methods of query step
	 *
	 * @param <T> the type of returned query
	 */
	interface Step<T> extends CtQueryable<T> {
		/**
		 * Defines whether this query will throw {@link ClassCastException}
		 * when the output of the previous step cannot be cast to type of input of next step.
		 * The default value is {@link QueryFailurePolicy#FAIL}<br>
		 *
		 * Note: The {@link CtQueryable#filterChildren(Filter)} step never throws {@link ClassCastException}
		 *
		 * @param policy the policy
		 * @return this to support fluent API
		 */
		T failurePolicy(QueryFailurePolicy policy);

		/**
		 * Sets the name of current query, to identify the current step during debugging of a query
		 * @param name
		 * @return this to support fluent API
		 */
		T name(String name);
	}
}
