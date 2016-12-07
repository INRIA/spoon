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
	@SuppressWarnings("unchecked")
	public <R> QueryStep<R> map(QueryStep<R> queryStep) {
		((QueryStepImpl<R>) queryStep).prev = (QueryStep<Object>) this;
		add(queryStep);
		return queryStep;
	}

	@Override
	public <P> QueryStep<P> map(AsyncFunction<?, P> code) {
		return map(new AsyncFunctionQueryStep<>(code));
	}

	@Override
	public <I, R> QueryStep<R> map(Function<I, R> code) {
		return map(new FunctionQueryStep<R>(code));
	}

	@Override
	public <P> QueryStep<P> scan(final Predicate<P> predicate) {
		return (QueryStep<P>) map(new Scann()).map(new PredicateQueryStep<P>(predicate));
	}

	@SuppressWarnings("unchecked")
	protected void add(Consumer<Object> consumer) {
		next.add(consumer);
	}

	@SuppressWarnings("unchecked")
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

	public void run(Object... input) {
		QueryStep<Object> start = getStartStep();
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
			run();
		} finally {
			remove((Consumer<Object>) consumer);
		}
	}

	public QueryStep<Object> getStartStep() {
		@SuppressWarnings("unchecked")
		QueryStep<Object> first = (QueryStep<Object>) this;
		while (first.getPrev() != null) {
			first = first.getPrev();
		}
		return first;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R> void apply(T input, Consumer<R> output) {
		add((Consumer<Object>) output);
		try {
			getStartStep().accept(input);
		} finally {
			remove((Consumer<Object>) output);
		}
	}

}
