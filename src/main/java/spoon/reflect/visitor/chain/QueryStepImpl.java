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

/**
 * Contains the default implementation of the generic {@link QueryStep} methods
 * Just the {@link #accept(Object)} method is implemented, by children classes
 */
public abstract class QueryStepImpl<O> implements QueryStep<O> {

	private QueryStep<Object> prev;
	private MultiConsumer<Object> next = new MultiConsumer<>();
	private boolean logging = false;

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
	public <P> QueryStep<P> map(ChainableFunction<?, P> code) {
		return addQueryStep(Query.map(code));
	}

	@Override
	public <I, R> QueryStep<R> map(Function<I, R> code) {
		return addQueryStep(Query.map(code));
	}

	@Override
	public <P extends CtElement> QueryStep<P> scan(Filter<P> filter) {
		return addQueryStep(Query.scan(filter));
	}

	/**
	 * appends the first step of queryStep as last step of self
	 * and return queryStep, which is now the last step of the query chain
	 */
	@SuppressWarnings("unchecked")
	protected <R> QueryStep<R> addQueryStep(QueryStep<R> queryStep) {
		//add first QueryStep of the provided chain to the last step of self
		QueryStep<Object> first = queryStep.getFirstStep();
		((QueryStepImpl<R>) first).prev = (QueryStep<Object>) this;
		add(first);
		//return last step of queryStep as last step of this chain
		return queryStep;
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
		if (isLogging()) {
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
		apply(null, (Consumer<O>) consumer);
	}

	@Override
	public abstract void accept(Object t);

	@SuppressWarnings("unchecked")
	@Override
	public void apply(Object input, Consumer<O> output) {
		add((Consumer<Object>) output);
		try {
			getFirstStep().accept(input);
		} finally {
			remove((Consumer<Object>) output);
		}
	}

	public boolean isLogging() {
		QueryStep<Object> first = getFirstStep();
		if (first == this) {
			return this.logging;
		} else {
			return first.isLogging();
		}
	}

	public QueryStep<O> setLogging(boolean logging) {
		QueryStep<Object> prev = getPrev();
		if (prev == null) {
			this.logging = logging;
		} else {
			prev.setLogging(logging);
		}
		next.setLogging(logging);
		return this;
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param e
	 * @param parameters
	 */
	protected void onClassCastException(String message, ClassCastException e, Object... parameters) {
		if (isLogging()) {
			StringBuilder sb = new StringBuilder();
			sb.append(message);
			sb.append("[");
			for (int i = 0; i < parameters.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(parameters[i]);
			}
			sb.append("] ignored because ").append(e.getMessage());
			if (Launcher.LOGGER.isTraceEnabled() && e != null) {
				Launcher.LOGGER.trace(sb.toString(), e);
			} else {
				Launcher.LOGGER.debug(sb.toString());
			}
		}
	}
}
