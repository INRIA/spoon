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
import spoon.SpoonException;
import spoon.reflect.visitor.filter.Scann;

/**
 * A) scan(Filter) scan all child elements of input element and send to output only these elements, which matches the filter
 * B) then(AsyncFnc) initialize filter with input element and then scan all children of the start element returned by filter and send to output only these elements, which matches the filter
 * C) scan all children of the start element returned by filter and send to output only these elements, which matches the filter - ignore input
 * D) matches(Predicate) - send input to output if it matches filter
 *
 *
 * @param <I> - input type
 * @param <O> - output type
 */
public abstract class QueryStep<O> implements Consumer<Object> {

	private QueryStep<? extends Object> prev;
	private MultiConsumer<Object> next = new MultiConsumer<>();

	protected QueryStep() {
	}

	@SuppressWarnings("unchecked")
	public <R> QueryStep<R> then(QueryStep<R> queryStep) {
		queryStep.prev = this;
		add(queryStep);
		return queryStep;
	}

	public <P> QueryStep<P> then(AsyncFunction<?, P> code) {
		return then(new AsyncFunctionQueryStep<>(code));
	}

	/**
	 * It behaves depending on the type of returned value like this:
	 * <table>
	 * <tr><td><b>Return type</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Sends input to the next step if returned value is true
	 * <tr><td>{@link Iterable}<td>Sends each item of Iterable to the next step
	 * <tr><td>? extends {@link Object}<td>Sends returned value to the next step
	 * </table><br>
	 * @param code a Function with one parameter of type I returning value of type R
	 * @return
	 */
	public <I, R> QueryStep<R> then(Function<I, R> code) {
		return then(new FunctionQueryStep<R>(code));
	}

	/**
	 * scan all child elements of input element. Only these elements are sent to, which predicate.matches(element)==true
	 *
	 * @param predicate filters scanned
	 * @return
	 */
	public <P> QueryStep<P> scan(final Predicate<P> predicate) {
		return (QueryStep<P>) then(new Scann()).then(new PredicateQueryStep<P>(predicate));
	}

	public <P> QueryStep<O> thenConsume(Consumer<P> consumer) {
		add(consumer);
		return this;
	}

	@SuppressWarnings("unchecked")
	protected void add(Consumer<?> consumer) {
		next.add((Consumer<Object>) consumer);
	}
	protected void remove(Consumer<Object> consumer) {
		next.remove(consumer);
	}

	protected void fireNext(Object out) {
		getNextConsumer().accept(out);
	}

	protected Consumer<Object> getNextConsumer() {
		if (Launcher.LOGGER.isDebugEnabled()) {
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

	protected String getDescription() {
		return String.valueOf(getDepth()) + ")";
	}

	private int getDepth() {
		int i = 0;
		QueryStep<?> qs = this;
		while (qs.prev != null) {
			qs = qs.prev;
			i++;
		}
		return i;
	}

	public void run(Object... input) {
		QueryStep<?> start = getStartStep();
		if (input.length > 0) {
			if (start instanceof StartQueryStep && ((StartQueryStep<?>) start).getInputs().size() > 0) {
				throw new SpoonException("Cannot accept exta input, because input of this QueryStep chain is alredy defined");
			}
			for (Object in : input) {
				start.accept(in);
			}
		} else {
			start.accept(null);
		}
	}

	public List<O> list(Object... input) {
		final List<O> list = new ArrayList<>();
		forEach(new Consumer<O>() {
			@Override
			public void accept(O out) {
				list.add(out);
			}
		}, input);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <R> void forEach(Consumer<R> consumer, Object... input) {
		thenConsume(consumer);
		try {
			run(input);
		} finally {
			remove((Consumer<Object>) consumer);
		}
	}

	public QueryStep<?> getStartStep() {
		QueryStep<?> first = this;
		while (first.prev != null) {
			first = first.prev;
		}
		return first;
	}

}
