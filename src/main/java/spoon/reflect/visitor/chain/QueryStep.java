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
import spoon.reflect.visitor.Query;

/**
 * QueryStep represents one step of the query.
 * The chain of QueryStep items represents the query, which can be used to traverse spoon model by several ways.<br>
 *
 * <br>
 * Use {@link CtElement#map()} or {@link CtElement#scan(Filter)} to create a new query starting from existing element.<br>
 * If you need to create query which is not bound to any input element and then call that query independent then use
 * {@link Query#map()} or {@link Query#scan(spoon.reflect.visitor.Filter)} or {@link Query#match(spoon.reflect.visitor.Filter)}
 *
 * Use these methods to compose the next steps of the query
 * <ul>
 * <li> {@link #map(spoon.reflect.visitor.chain.Function)}
 * <li> {@link #scan(spoon.reflect.visitor.chain.Predicate)}
 * </ul>
 *
 * Use these methods to process the query:
 * <ul>
 * <li>{@link #apply(Object, Consumer)}
 * <li>{@link #accept(Object)}
 * <li>{@link #forEach(Consumer)}
 * <li>{@link #list()}
 * </ul>
 * The query can be used several times.<br>
 * But QueryStep is not thread safe. So you must create new query for each thread.<br>
 * Usually the new query is created each time when you need to query something.
 * The reusing of QueryStep instance makes sense when the same query has to be evaluated
 * several times in the loop.
 *
 * @param <O> the type of the element produced by this QueryStep
 */
public interface QueryStep<O> extends CtQueryable, Consumer<Object>, ChainableFunction<Object, O> {

	/**
	 * calls getFirstStep().accept(null), which causes that all input elements registered in {@link StartQueryStep}
	 * are processed by query chain. All the produced elements are collected in List
	 * @return the List of collected elements.
	 */
	List<O> list();

	/**
	 * calls getFirstStep().accept(null), which causes that all input elements registered in {@link StartQueryStep}
	 * are processed by query chain. For each produced element the consumer.accept(element) is called
	 * @param consumer
	 */
	<R> void forEach(Consumer<R> consumer);

	/**
	 * Sends the input parameter as input of the whole query (by getFirstStep().accept(input)) and call output.accept(element) for each element produced by this query
	 *
	 * @param input
	 * @param output
	 */
	@Override
	void apply(Object input, Consumer<O> output);

	/**
	 * Sets the name of current QueryStep. It can help to identify the steps during debugging of your query
	 * @param name
	 * @return this to support fluent API
	 */
	QueryStep<O> name(String name);
	/**
	 * @return true if logging is enabled for this query chain
	 */
	boolean isLogging();
	/**
	 * Enable/disable logging for this query chain.
	 *
	 * Note: it is not possible to enable logging of all queries globally by Launcher.LOGGER.isDebugEnabled()
	 * because it causes StackOverflow.
	 * Reason: Query chains are used internally during writing of log messages too. So it would write logs for ever...
	 */
	QueryStep<O> setLogging(boolean logging);
}
