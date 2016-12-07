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
 * The chain of QueryStep items represents the query which can be used to traverse spoon model by several ways.<br>
 *
 *
 * <br>
 * Use {@link Query#query()}, {@link Query#query(Object)} or {@link CtElement#query()} to create a new query.
 *
 * Use these methods to compose the query
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
 * </ul>
 * The query can be used several times.<br>
 * But QueryStep is not thread safe. So you must create new query for each thread.<br>
 * Usually the new query is created each time when you need to query something.
 * The reusing of QueryStep instance makes sense when the same query has to be evaluated
 * several times in the loop.
 *
 * @param <O> the type of the element produced by this QueryStep
 */
public interface QueryStep<O> extends QueryComposer, Consumer<Object> {

	/**
	 * @return previous step of the query or null if this step is first
	 */
	QueryStep<Object> getPrev();

	List<O> list();

	<R> void forEach(Consumer<R> consumer);

	<T, R> void apply(T input, Consumer<R> output);
}
