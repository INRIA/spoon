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

import java.util.List;

/**
 * CtQuery represents a query, which can be used to traverse a spoon model and collect
 * children elements in several ways.<br>
 *
 * <br>
 * Use {@link CtQueryable#map(CtFunction)}} or {@link CtQueryable#filterChildren(Filter)} to create a new query starting from an existing element.<br>
 *
 * The main methods are:
 * <ul>
 * <li> {@link #map(CtFunction))} - uses a lambda expression to return any model elements that are directly accessible from an input element.
 * <li> {@link #map(CtConsumableFunction))} -implementations of {@link CtConsumableFunction} provides a complex queries.
 * <li> {@link #filterChildren(Filter))} - uses {@link Filter} instances to filter children of an element
 * <li> {@link #list()} - to evaluate the query and return a list of elements produced by this query.
 * </ul>
 * It makes sense to evaluate this query only once, because the input element is constant.<br>
 * A CtQuery is lazily evaluated once {{@link #list()}} or {@link #forEach(CtConsumer)} are called.
 * Usually a new query is created each time when one needs to query something.
 * If you need to reuse a query instance several times, for example in a loop, then use {@link CtQuery#setInput(Object...)}
 * to bound this query with different input.
 *
 */
public interface CtQuery extends CtQueryable {

	/**
	 * sets (binds) the input of the query. If the query is created by {@link CtElement#map} or {@link CtElement#filterChildren(Filter)},
	 * then the query is already bound to this element.
	 * A new call of {@link #setInput(Object...)} will reset the current binding ans use the new one.
	 *
	 * @param input
	 * @return this to support fluent API
	 */
	CtQuery setInput(Object... input);

	/**
	 * actually evaluates the query and for each produced outputElement calls `consumer.accept(outputElement)`
     * @param consumer The consumer which accepts the results of the query
	 */
	<R> void forEach(CtConsumer<R> consumer);
	/**
	 * actually evaluates the query and returns all the produced elements collected in a List
	 * @return the list of elements collected by the query.
	 * @see #forEach(CtConsumer) for an efficient way of manipulating the elements without creating an intermediate list.
	 */
	<R extends Object> List<R> list();
	/**
	 * actually evaluates the query and returns these produced elements as a List,
	 * which are assignable to `itemClass`
	 * @return the list of elements collected by the query.
	 */
	<R> List<R> list(Class<R> itemClass);

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
	CtQuery failurePolicy(QueryFailurePolicy policy);

	/**
	 * Sets the name of current query, to identify the current step during debugging of a query
	 * @param name
	 * @return this to support fluent API
	 */
	CtQuery name(String name);
}
