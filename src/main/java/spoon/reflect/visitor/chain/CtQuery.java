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

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.OverriddenMethodFilter;

/**
 * CtQuery represents a query, which can be used to traverse spoon model by several ways.<br>
 *
 * <br>
 * Use {@link CtElement#map()} or {@link CtElement#filterChildren(Filter)} to create a new query starting from existing element.<br>
 *
 * Use these methods to compose the next steps of the query
 * <ul>
 * <li> {@link #map(CtFunction))} - use java 8 lambda expression to navigate to any model element directly accessible from input element
 * <li> {@link #map(CtQueryStep))} - use implementation of the CtQueryStep interface to evaluate complex query like {@link OverriddenMethodFilter}
 * <li> {@link #filterChildren(Filter))} - use {@link Filter} instances to filter children of input element
 * </ul>
 *
 * Use these methods to process the query:
 * <ul>
 * <li>{@link #forEach(CtConsumer)} - to evaluate the query and call a Consumer.apply(output) method for each element produced by this query
 * <li>{@link #list()} - to evaluate the query and return list of elements produced by this query
 * </ul>
 * The query can be used several times.<br>
 * QueryStep is not thread safe. So you must create new query for each thread.<br>
 * Usually the new query is created each time when you need to query something.
 * The reusing of {@link CtQuery} instance makes sense when the same query has to be evaluated
 * several times in the loop.
 *
 * @param <O> the type of the element produced by this query
 */
public interface CtQuery<O> extends CtQueryable {

	/**
	 * evaluates the query which causes that input element
	 * is processed by query chain. All the produced elements are collected in the List
	 * @return the List of collected elements.
	 */
	List<O> list();

	/**
	 * evaluates the query which causes that input element
	 * is processed by query chain. For each produced element the consumer.accept(element) is called.<br>
	 * You can use java 8 lambda expression to implement consumer.
	 * @param consumer
	 */
	<R> void forEach(CtConsumer<R> consumer);

	/**
	 * Sets the name of current QueryStep. It can help to identify the steps during debugging of your query
	 * @param name
	 * @return this to support fluent API
	 */
	CtQuery<O> name(String name);

	/**
	 * @param policy - defines whether this query will throw {@link ClassCastException}
	 * when output of previous step cannot be cast to type of input of next step.
	 * The default value is {@link QueryFailurePolicy#FAIL}<br>
	 *
	 * Note: The {@link CtQueryable#filterChildren(Filter)} step never throws {@link ClassCastException}
	 *
	 * @return this to support fluent API
	 */
	CtQuery<O> failurePolicy(QueryFailurePolicy policy);
}
