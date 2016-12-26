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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * The facade of {@link CtBaseQuery} which represents a query bound to the {@link CtElement},
 * which is the constant input of this query.
 * It is used by {@link CtElement} implementations of {@link CtQueryable}.
 *
 * @param <O> - the type of element which is produced by the last step of the query
 */
public class CtQueryImpl<O> implements CtQuery<O> {

	/**
	 * All the constant inputs of this query.
	 */
	private List<Object> inputs;

	/**
	 * The {@link CtBaseQuery} which provides low level query behavior for this query.
	 */
	private final CtBaseQueryImpl<Object> query;

	@SuppressWarnings("unchecked")
	public <T> CtQueryImpl(T input) {
		query = new CtBaseQueryImpl<>();
		setInput((O) input);
	}

	/**
	 * @return list of elements which will be used as input of the query
	 */
	public List<Object> getInputs() {
		return inputs == null ? Collections.emptyList() : inputs;
	}

	/**
	 * sets list of elements which will be used as input of the query
	 * @param input
	 * @return this to support fluent API
	 */
	public CtQuery<O> setInput(O input) {
		if (inputs != null) {
			inputs.clear();
		}
		return addInput(input);
	}

	/**
	 * adds list of elements which will be used as input of the query too
	 * @param input
	 * @return this to support fluent API
	 */
	public CtQuery<O> addInput(O input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> CtQuery<R> map(CtLazyFunction<?, R> queryStep) {
		query.map(queryStep);
		return (CtQuery<R>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I, R> CtQuery<R> map(CtFunction<I, R> function) {
		query.map(function);
		return (CtQuery<R>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtElement> CtQuery<T> filterChildren(Filter<T> filter) {
		query.filterChildren(filter);
		return (CtQuery<T>) this;
	}

	@SuppressWarnings("unchecked")
	public <R> void forEach(CtConsumer<R> consumer) {
		for (Object input : inputs) {
			query.apply(input, (CtConsumer<Object>) consumer);
		}
	}

	public List<O> list() {
		final List<O> list = new ArrayList<>();
		forEach(new CtConsumer<O>() {
			@Override
			public void accept(O out) {
				list.add(out);
			}
		});
		return list;
	}

	@Override
	public CtQuery<O> name(String name) {
		query.name(name);
		return this;
	}

	@Override
	public CtQuery<O> failurePolicy(QueryFailurePolicy policy) {
		query.failurePolicy(policy);
		return this;
	}

	/**
	 * Enable/disable logging for this query
	 *
	 * Note: it is not possible to enable logging of all queries globally by Launcher.LOGGER.isDebugEnabled()
	 * because it causes StackOverflow.
	 * Reason: Query chains are used internally during writing of log messages too. So it would write logs for ever...
	 */
	public CtQuery<O> logging(boolean logging) {
		query.logging(logging);
		return this;
	}
}
