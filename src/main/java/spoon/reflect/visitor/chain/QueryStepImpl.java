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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.Scann;

/**
 * Contains the default implementation of the generic {@link QueryStep} methods
 */
public class QueryStepImpl<O> implements QueryStep<O> {

	private List<Object> inputs;

	private Step firstStep;
	private Step lastStep;
	private Step tail;

	private boolean logging = false;

	public QueryStepImpl() {
		tail = new TailConsumer();
		firstStep = tail;
		lastStep = tail;
	}

	@SuppressWarnings("unchecked")
	public <T> QueryStepImpl(T input) {
		this();
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
	 * @param inputs
	 * @return this to support fluent API
	 */
	public QueryStep<O> setInput(O input) {
		if (inputs != null) {
			inputs.clear();
		}
		return addInput(input);
	}

	/**
	 * adds list of elements which will be used as input of the query too
	 * @param inputs
	 * @return this to support fluent API
	 */
	public QueryStep<O> addInput(O input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		this.inputs.add(input);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> QueryStep<P> map(ChainableFunction<?, P> code) {
		add(new ChainableFunctionWrapper(code));
		return (QueryStep<P>) this;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <I, R> QueryStep<R> map(CtFunction<I, R> code) {
		add(new FunctionWrapper(code));
		return (QueryStep<R>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtElement> QueryStep<T> scan(Filter<T> filter) {
		map(new Scann());
		add(new FilterWrapper(filter));
		return (QueryStep<T>) this;
	}


	@Override
	public void accept(Object input) {
		firstStep.accept(input);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void apply(Object input, Consumer<O> output) {
		tail.next = (Consumer<Object>) output;
		try {
			if (input == null) {
				if (inputs != null) {
					for (Object in : inputs) {
						accept(in);
					}
				}
			} else {
				if (inputs != null) {
					throw new SpoonException("Do not add QueryStep inputs if you want to use query for extra input");
				}
				accept(input);
			}
		} finally {
			tail.next = null;
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

	@SuppressWarnings("unchecked")
	@Override
	public <R> void forEach(Consumer<R> consumer) {
		apply(null, (Consumer<O>) consumer);
	}

	@Override
	public QueryStep<O> name(String name) {
		if (lastStep == tail) {
			throw new SpoonException("Cannot set name of the step on the chain with no step");
		}
		lastStep.name = name;
		return this;
	}

	@Override
	public boolean isLogging() {
		return logging;
	}

	@Override
	public QueryStep<O> setLogging(boolean logging) {
		this.logging = logging;
		return this;
	}

	private void add(Step step) {
		if (lastStep == tail) {
			firstStep = step;
			lastStep = step;
		} else {
			lastStep.next = step;
			lastStep = step;
		}
		step.next = tail;
		name(String.valueOf(getLength()));
	}

	/**
	 * @return number of steps of this query
	 */
	public int getLength() {
		int len = 0;
		Step s = firstStep;
		while (s != tail) {
			len++;
			s = (Step) s.next;
		}
		return len;
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param e
	 * @param parameters
	 */
	protected void log(String message, ClassCastException e, Object... parameters) {
		if (isLogging()) {
			StringBuilder sb = new StringBuilder();
			sb.append(message);
			if (parameters.length > 0) {
				sb.append(" [");
				for (int i = 0; i < parameters.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(parameters[i]);
				}
				sb.append("]");
			}
			if (e != null) {
				sb.append(" ignored because ").append(e.getMessage());
			}
			if (Launcher.LOGGER.isTraceEnabled() && e != null) {
				Launcher.LOGGER.trace(sb.toString(), e);
			} else {
				Launcher.LOGGER.debug(sb.toString());
			}
		}
	}

	/**
	 * abstract step which knows next Consumer and then name
	 */
	private abstract class Step implements Consumer<Object> {
		protected String name;
		protected Consumer<Object> next;
	}

	/**
	 * There is always one TailConsumer in each query, which sends result to output
	 */
	private class TailConsumer extends Step {
		@Override
		public void accept(Object out) {
			if (out == null) {
				return;
			}
			if (next != null) {
				try {
					next.accept(out);
				} catch (ClassCastException e) {
					log("Query output skipped for value", e, out);
				}
			}
		}
	}

	/**
	 * a step which calls ChainableFunction. Implements contract of {@link QueryStep#map(ChainableFunction)}
	 */
	private class ChainableFunctionWrapper extends Step {
		private ChainableFunction<Object, Object> fnc;

		@SuppressWarnings("unchecked")
		ChainableFunctionWrapper(ChainableFunction<?, ?> code) {
			fnc = (ChainableFunction<Object, Object>) code;
		}
		@Override
		public void accept(Object input) {
			if (input == null) {
				return;
			}
			try {
				fnc.apply(input, next);
			} catch (ClassCastException e) {
				log("Calling of step " + name + " failed", e, input);
			}
		}
	}

	/**
	 * a step which calls Function. Implements contract of {@link QueryStep#map(CtFunction)}
	 */
	private class FunctionWrapper extends Step {
		private CtFunction<Object, Object> fnc;

		FunctionWrapper(CtFunction<?, ?> code) {
			fnc = (CtFunction<Object, Object>) code;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void accept(Object input) {
			if (input == null) {
				return;
			}
			Object result;
			try {
//					result = code.invoke(input);
				result = fnc.apply(input);
			} catch (ClassCastException e) {
				log("Function call skipped on input ", e, input);
				return;
			}
			if (result == null) {
				return;
			}
			if (result instanceof Boolean) {
				//the code is a predicate. send the input to output if result is true
				if ((Boolean) result) {
					next.accept(input);
				} else {
					log("Predicate is false on ", null, input);
				}
			}
			if (result instanceof Iterable) {
				//send each item of Iterable to the next step
				for (Object out : (Iterable<Object>) result) {
					next.accept(out);
				}
			} else if (result.getClass().isArray()) {
				//send each item of Array to the next step
				for (int i = 0; i < Array.getLength(result); i++) {
					next.accept(Array.get(result, i));
				}
			} else {
				next.accept(result);
			}
		}
	}

	/**
	 * a step which proceeds only elements matching filter
	 */
	private class FilterWrapper extends Step {

		private Filter<CtElement> filter;

		@SuppressWarnings("unchecked")
		FilterWrapper(Filter<? extends CtElement> filter) {
			this.filter = (Filter<CtElement>) filter;
		}

		@Override
		public void accept(Object input) {
			if (input == null) {
				return;
			}
			boolean matches = false;
			try {
				matches = (Boolean) filter.matches((CtElement) input);
			} catch (ClassCastException e) {
				log("Filter call skipped on input ", e, input);
			}
			if (matches) {
				//send input to output, because Fitler.matches returned true
				next.accept(input);
			} else {
				log("Filter is false on ", null, input);
			}
		}
	}
}
