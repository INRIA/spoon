/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

import java.util.List;

/**
 * <p>CtQuery represents a query, which can be used to traverse a spoon model and collect
 * children elements in several ways.</p>
 *
 * <p>Creation: A query is created either from a {@link CtElement}, or it can be defined first from {@link Factory#createQuery()} and bound to root elements
 * afterwards using {@link CtQuery#setInput(Object...)}.</p>
 *
 * <p>Chaining: In a query several steps can be chained, by chaining calls to map functions. The non-null outputs of one step
 * are given as input to the next step. An iterable or array output is considered as a set of different inputs for the next step.</p>
 *
 * <p>Evaluation: A CtQuery is lazily evaluated once {@link CtQuery#list()} or {@link CtQuery#forEach(CtConsumer)} are called.</p>
 *
 */
public interface CtQuery extends CtQueryable {

	/**
	 * Recursively scans all children elements of an input element.
	 * The matched child element for which (filter.matches(element)==true) are sent to the next query step.
	 * Essentially the same as {@link CtElement#getElements(Filter)} but more powerful, because it
	 * can be chained with other subsequent queries.
	 *
	 * Note: the input element (the root of the query, `this` if you're in {@link CtElement}) is also checked and may thus be also sent to the next step.
	 * The elements which throw {@link ClassCastException} during {@link Filter#matches(CtElement)}
	 * are considered as **not matching**, ie. are excluded.
	 *
	 * @param filter used to filter scanned children elements of the AST tree.
	 * 	If null then all children elements pass to next step.
	 * @return this to support fluent API
	 */
	@Override
	<R extends CtElement> CtQuery filterChildren(Filter<R> filter);

	/**
	 * The matched element for which (filter.matches(element)==true) is sent to the next query step.
	 *
	 * The elements which throw {@link ClassCastException} during {@link Filter#matches(CtElement)}
	 * are considered as **not matching**, ie. are excluded.
	 *
	 * @param filter used to detect if input element can pass to next query step
	 * @return this to support fluent API
	 */
	<R extends CtElement> CtQuery select(Filter<R> filter);

	/**
	 * Query elements based on a function, the behavior depends on the return type of the function.
	 * <table summary="">
	 * <tr><td>Return type of `function`</td><td>Behavior</td></tr>
	 * <tr><td>{@link Boolean}</td><td>Select elements if the returned value of `function` is true (as for {@link Filter}).</td></tr>
	 * <tr><td>? extends {@link Object}</td><td>Send the returned value of `function` to the next step</td></tr>
	 * <tr><td>{@link Iterable}</td><td>Send each item of the collection to the next step</td></tr>
	 * <tr><td>{@link Object}[]</td><td>Send each item of the array to the next step</td></tr>
	 * </table><br>
	 *
	 * @param function a Function with one parameter of type I returning a value of type R
	 * @return this to support fluent API
	 */
	@Override
	<I, R> CtQuery map(CtFunction<I, R> function);

	/**
	 * Sets (binds) the input of the query.
	 * If the query is created by {@link CtElement#map} or {@link CtElement#filterChildren(Filter)},
	 * then the query is already bound to this element.
	 * A new call of {@link CtQuery#setInput(Object...)} is always possible, it resets the current binding and sets the new one.
	 *
	 * @param input
	 * @return this to support fluent API
	 */
	<T extends CtQuery> T setInput(Object... input);

	/**
	 * Actually evaluates the query and for each produced output element of the last step,
	 * calls `consumer.accept(outputElement)`.
	 *
	 * This avoids to create useless intermediate lists.
	 *
     * @param consumer The consumer which accepts the results of the query
	 */
	<R> void forEach(CtConsumer<R> consumer);

	/**
	 * Actually evaluates the query and returns all the elements produced in the last step.<br>
	 * Note: The type R of the list is not checked by the query. So use the type, which matches the results of your query,
	 * otherwise the ClassCastException will be thrown when reading the list.
	 * @return the list of elements collected by the query.
	 * @see #forEach(CtConsumer) for an efficient way of manipulating the elements without creating an intermediate list.
	 */
	<R> List<R> list();

	/**
	 * Same as {@link CtQuery#list()}, but with static typing on the return type
	 * and the final filtering, which matches only results, which are assignable from that return type.
	 *
	 * @return the list of elements collected by the query.
	 */
	<R> List<R> list(Class<R> itemClass);

	/**
	 * Actually evaluates the query and returns first elements produced in the last step.<br>
	 * After the first element is found, the query evaluation is terminated.
	 *
	 * Note: The return type R is not checked by the query. So use the type, which matches the results of your query,
	 * otherwise the ClassCastException will be thrown.
	 * @return the first element found by the query.
	 */
	<R> R first();

	/**
	 * Same as {@link CtQuery#first()}, but with static typing on the return type
	 * and the final filtering, which matches only the first result, which is assignable from that return type.
	 *
	 * @return the list of elements collected by the query.
	 */
	<R> R first(Class<R> itemClass);

	/**
	 * Defines whether this query will throw {@link ClassCastException}
	 * when the output of the previous step cannot be cast to type of input of next step.
	 * The default value is {@link QueryFailurePolicy#FAIL}, which means than exception is thrown when there is a mismatch<br>
	 *
	 * Note: The {@link CtQueryable#filterChildren(Filter)} step never throws {@link ClassCastException}
	 *
	 * @param policy the policy
	 * @return this to support fluent API
	 */
	CtQuery failurePolicy(QueryFailurePolicy policy);

	/**
	 * Sets the name of current query, to identify the current step during debugging of a query
	 * @param name
	 * @return this to support fluent API
	 */
	CtQuery name(String name);

	/**
	 * Same as {@link CtQuery#map(CtFunction)}, but the returned object is not handled
	 * by java's return statement, but by a call to {@link CtConsumer#accept(Object)}, this
	 * allows efficient and easy to write chained processing, see {@link CtConsumableFunction}.
	 *
	 * @param queryStep
	 * @return this to support fluent API
	 */
	@Override
	<I> CtQuery map(CtConsumableFunction<I> queryStep);

	/**
	 * Terminates the evaluation of this query.
	 * The query still returns all results collected before termination.
	 * This method should not throw an exception.
	 */
	void terminate();

	/**
	 * @return true if the evaluation has been terminated.
	 */
	boolean isTerminated();
}
