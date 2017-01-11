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

/**
 * {@link CtBaseQuery} represents a low level query, which can be used to traverse a spoon model and collect
 * children elements in several ways.<br>
 *
 * <br>
 * Use {@link CtQueryable#map(CtConsumableFunction)}}, {@link CtQueryable#map(CtFunction)}} or {@link CtQueryable#filterChildren(Filter)}
 * to append a next query step.<br>
 *
 * The main methods are:
 * <ul>
 * <li> {@link #map(CtFunction))} - uses a lambda expression to return any model elements that are directly accessible from an input element.
 * <li> {@link #map(CtConsumableFunction))} -implementations of {@link CtConsumableFunction} provides a complex queries.
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
public interface CtBaseQuery extends CtQueryable.Step<CtBaseQuery> {

	/**
	 * Evaluates this query.
	 *
	 * @param input represents the input element of the first mapping function of this query
	 * @param outputConsumer method accept of the outputConsumer is called for each element produced by last mapping function of this query
	 */
	<I, R> void evaluate(I input, CtConsumer<R> outputConsumer);
}
