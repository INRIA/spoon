/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryImpl;

/**
 * A factory to create some queries on the Spoon metamodel.
 */
public class QueryFactory extends SubFactory {

	/**
	 * Creates the evaluation factory.
	 */
	public QueryFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a unbound query. Use {@link CtQuery#setInput(Object...)}
	 * before {@link CtQuery#forEach(spoon.reflect.visitor.chain.CtConsumer)}
	 * or {@link CtQuery#list()} is called
	 */
	public CtQuery createQuery() {
		return new CtQueryImpl();
	}

	/**
	 * Creates a bound query. Use directly
	 * {@link CtQuery#forEach(spoon.reflect.visitor.chain.CtConsumer)}
	 * or {@link CtQuery#list()} to evaluate the query
	 */
	public CtQuery createQuery(Object input) {
		return new CtQueryImpl(input);
	}

	/**
	 * Creates a bound query. Use directly
	 * {@link CtQuery#forEach(spoon.reflect.visitor.chain.CtConsumer)}
	 * or {@link CtQuery#list()} to evaluate the query
	 */
	public CtQuery createQuery(Iterable<?> inputs) {
		return new CtQueryImpl().addInput(inputs);
	}

	/**
	 * Creates a bound query with an optional number
	 * of inputs elements to the query (see {@link CtQuery#setInput(Object...)})
	 */
	public CtQuery createQuery(Object... input) {
		return new CtQueryImpl(input);
	}
}
