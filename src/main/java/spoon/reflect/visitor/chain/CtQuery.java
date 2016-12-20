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
 * <li> {@link #filterChildren(Filter))} - uses {@link Filter} instances to filter children of an element
 * <li> {@link #list()} - to evaluate the query and return a list of elements produced by this query.
 * </ul>
 * The query can be used several times.<br>
 * A CtQuery  is lazily evaluated once {{@link #list()}} is called.
 * Usually a new query is created each time when one needs to query something.
 * However, reusing a {@link CtQuery} instance makes sense when the same query has to be evaluated
 * several times in a loop.
 *
 * @param &lt;O> the type of the element produced by this query
 */
public interface CtQuery<O> extends CtQueryable {

	/**
	 * actually evaluates the query and returns all the produced elements collected in a List
	 * @return the list of elements collected by the query.
	 */
	List<O> list();

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
	CtQuery<O> failurePolicy(QueryFailurePolicy policy);

	/**
	 * Sets the name of current query, to identify the current step during debugging of a query
	 * @param name
	 * @return this to support fluent API
	 */
	CtQuery<O> name(String name);

}
