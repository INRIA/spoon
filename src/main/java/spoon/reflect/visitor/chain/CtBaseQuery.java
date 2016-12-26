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
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

/**
 * {@link CtBaseQuery} represents a low level query, which can be used to traverse a spoon model and collect
 * children elements in several ways.<br>
 *
 * <br>
 * Use {@link CtQueryable#map(CtLazyFunction)}}, {@link CtQueryable#map(CtFunction)}} or {@link CtQueryable#filterChildren(Filter)}
 * to append a next query step.<br>
 *
 * The main methods are:
 * <ul>
 * <li> {@link #map(CtFunction))} - uses a lambda expression to return any model elements that are directly accessible from an input element.
 * <li> {@link #map(CtLazyFunction))} -implementations of {@link CtLazyFunction} provides a complex queries.
 * <li> {@link #filterChildren(Filter))} - uses {@link Filter} instances to filter children of an element
 * <li> {@link #list()} - to evaluate the query and return a list of elements produced by this query.
 * </ul>
 * The {@link CtBaseQuery} can be used several times by calling of {@link #apply(Object, CtConsumer)}.<br>
 * Reusing a {@link CtBaseQuery} instance makes sense when the same query has to be evaluated
 * several times in a loop.
 *
 * The instance of CtBaseQuery is not thread safe! So use it always only in one thread.
 *
 * @param &lt;O> the type of the element produced by this query
 */
public interface CtBaseQuery<O> extends CtLazyFunction<Object, O> {

	/**
	 * Evaluates this query.
	 * @param input represents the input element of the first mapping function of this query
	 * @param outputConsumer method accept of the outputConsumer is called for each element produced by last mapping function of this query
	 */
	@Override
	void apply(Object input, CtConsumer<O> outputConsumer);

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
	<I, R> CtBaseQuery<R> map(CtFunction<I, R> function);

	/**
	 * Query elements based on a {@link CtLazyFunction}, which supports efficient implementation of {@link CtScanner} based queries,
	 * which may produce many thousands of mapping output elements.
	 *
	 * @param queryStep
	 * @return the created QueryStep, which is the new last step of the query
	 */
	<T> CtBaseQuery<T> map(CtLazyFunction<?, T> queryStep);


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
	<T extends CtElement> CtBaseQuery<T> filterChildren(Filter<T> filter);

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
	CtBaseQuery<O> failurePolicy(QueryFailurePolicy policy);

	/**
	 * Sets the name of current query, to identify the current step during debugging of a query
	 * @param name
	 * @return this to support fluent API
	 */
	CtBaseQuery<O> name(String name);

}
