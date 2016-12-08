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
import java.util.List;

import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.Scann;

/**
 * Contains the default implementation of the generic {@link QueryStep} methods
 * Just the {@link #accept(Object)} method is implemented, by children classes
 */
public abstract class QueryStepImpl<O> implements QueryStep<O> {

	private QueryStep<Object> prev;
	private MultiConsumer<Object> next = new MultiConsumer<>();

	protected QueryStepImpl() {
	}

	@Override
	public QueryStep<Object> getPrev() {
		return prev;
	}

	@Override
	public QueryStep<Object> getFirstStep() {
		@SuppressWarnings("unchecked")
		QueryStep<Object> first = (QueryStep<Object>) this;
		while (first.getPrev() != null) {
			first = first.getPrev();
		}
		return first;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> QueryStep<R> map(QueryStep<R> queryStep) {
		((QueryStepImpl<R>) queryStep).prev = (QueryStep<Object>) this;
		add(queryStep);
		return queryStep;
	}

	@Override
	public <P> QueryStep<P> map(ChainableFunction<?, P> code) {
		return map(Query.map(code));
	}

	@Override
	public <I, R> QueryStep<R> map(Function<I, R> code) {
		return map(Query.map(code));
	}

	@Override
	public <P extends CtElement> QueryStep<P> scan(Filter<P> filter) {
		return map(new Scann()).map(Query.match(filter));
	}

	/**
	 * adds a consumer of elements produced by this step
	 * @param consumer
	 */
	protected void add(Consumer<Object> consumer) {
		next.add(consumer);
	}

	/**
	 * removes consumer of elements produced by this step
	 * @param consumer
	 */
	protected void remove(Consumer<Object> consumer) {
		next.remove(consumer);
	}

	/**
	 * sends the out to the all registered consumers of this step
	 * @param out
	 */
	protected void fireNext(Object out) {
		getNextConsumer().accept(out);
	}

	/**
	 * @return a consumer which can be used to send element to all registered consumers of this step
	 */
	protected Consumer<Object> getNextConsumer() {
		if (Launcher.LOGGER.isDebugEnabled()) {
			//if logging is enabled then we provide a consumer which logs each produced element
			return new Consumer<Object>() {
				@Override
				public void accept(Object element) {
					Launcher.LOGGER.debug(getDescription() + " " + element);
					next.accept(element);
				}
			};
		}
		return next;
	}

	/**
	 * helper method which provides description of this step. The description is visible in the log
	 * @return
	 */
	protected String getDescription() {
		return String.valueOf(getDepth()) + ")";
	}

	/**
	 * @return depth of this query step starting from the first element. The first element has depth 0.
	 */
	@SuppressWarnings("unchecked")
	private int getDepth() {
		int i = 0;
		QueryStep<Object> qs = (QueryStep<Object>) this;
		while (qs.getPrev() != null) {
			qs = qs.getPrev();
			i++;
		}
		return i;
	}

	@Override
	public List<O> list() {
		final List<O> list = new ArrayList<>();
		forEach(new Consumer<O>() {
			@Override
			public void accept(O out) {
				list.add(out);
			}
		});
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> void forEach(Consumer<R> consumer) {
		add((Consumer<Object>) consumer);
		try {
			getFirstStep().accept(null);
		} finally {
			remove((Consumer<Object>) consumer);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R> void apply(T input, Consumer<R> output) {
		add((Consumer<Object>) output);
		try {
			getFirstStep().accept(input);
		} finally {
			remove((Consumer<Object>) output);
		}
	}
}
