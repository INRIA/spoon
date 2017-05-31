/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
import spoon.reflect.visitor.filter.CtScannerFunction;

/**
 * The facade of {@link CtQuery} which represents a query bound to the {@link CtElement},
 * which is the constant input of this query.
 * It is used by {@link CtElement} implementations of {@link CtQueryable}.
 */
public class CtQueryImpl implements CtQuery {

	/**
	 * All the constant inputs of this query.
	 */
	private List<Object> inputs;

	private OutputFunctionWrapper outputStep = new OutputFunctionWrapper();
	private AbstractStep lastStep = outputStep;
	private AbstractStep firstStep = lastStep;

	private boolean terminated = false;

	public CtQueryImpl(Object... input) {
		setInput(input);
	}

	/**
	 * @return list of elements which will be used as input of the query
	 */
	public List<Object> getInputs() {
		return inputs == null ? Collections.emptyList() : inputs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CtQueryImpl setInput(Object... input) {
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
	public CtQueryImpl addInput(Object... input) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>();
		}
		if (input != null) {
			for (Object in : input) {
				this.inputs.add(in);
			}
		}
		return this;
	}

	@Override
	public <R> void forEach(CtConsumer<R> consumer) {
		outputStep.setNext(consumer);
		for (Object input : inputs) {
			firstStep.accept(input);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Object> List<R> list() {
		return (List<R>) list(Object.class);
	}

	@Override
	public <R> List<R> list(final Class<R> itemClass) {
		final List<R> list = new ArrayList<>();
		forEach(new CtConsumer<R>() {
			@Override
			public void accept(R out) {
				if (out != null && itemClass.isAssignableFrom(out.getClass())) {
					list.add(out);
				}
			}
		});
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	public <R> R first() {
		return (R) first(Object.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R first(final Class<R> itemClass) {
		final Object[] result = new Object[1];
		outputStep.setNext(new CtConsumer<R>() {
			@Override
			public void accept(R out) {
				if (out != null && itemClass.isAssignableFrom(out.getClass())) {
					result[0] = out;
					terminate();
				}
			}
		});
		for (Object input : inputs) {
			firstStep.accept(input);
			if (isTerminated()) {
				break;
			}
		}
		return (R) result[0];
	}

	private boolean logging = false;
	private QueryFailurePolicy failurePolicy = QueryFailurePolicy.FAIL;

	@Override
	public <I> CtQueryImpl map(CtConsumableFunction<I> code) {
		addStep(new LazyFunctionWrapper(code));
		return this;
	}

	@Override
	public <I, R> CtQueryImpl map(CtFunction<I, R> function) {
		addStep(new FunctionWrapper(function));
		return this;
	}

	@Override
	public <R extends CtElement> CtQueryImpl filterChildren(Filter<R> filter) {
		map(new CtScannerFunction());
		if (filter != null) {
			select(filter);
		}
		return this;
	}

	@Override
	public <R extends CtElement> CtQueryImpl select(final Filter<R> filter) {
		map(new CtFunction<R, Boolean>() {
			@Override
			public Boolean apply(R input) {
				return filter.matches(input);
			}
		});
		stepFailurePolicy(QueryFailurePolicy.IGNORE);
		return this;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}
	@Override
	public void terminate() {
		terminated = true;
	}

	/**
	 * Evaluates this query, ignoring bound input - if any
	 *
	 * @param input represents the input element of the first mapping function of this query
	 * @param outputConsumer method accept of the outputConsumer is called for each element produced by last mapping function of this query
	 */
	public <I, R> void evaluate(I input, CtConsumer<R> outputConsumer) {
		outputStep.setNext(outputConsumer);
		firstStep.accept(input);
	}

	@Override
	public CtQueryImpl name(String name) {
		lastStep.setName(name);
		return this;
	}

	@Override
	public CtQueryImpl failurePolicy(QueryFailurePolicy policy) {
		failurePolicy = policy;
		return this;
	}

	public CtQueryImpl stepFailurePolicy(QueryFailurePolicy policy) {
		lastStep.setLocalFailurePolicy(policy);
		return this;
	}
	/**
	 * Enable/disable logging for this query
	 *
	 * Note: it is not possible to enable logging of all queries globally by Launcher.LOGGER.isDebugEnabled()
	 * because it causes StackOverflow.
	 * Reason: Query chains are used internally during writing of log messages too. So it would write logs for ever...
	 */
	public CtQueryImpl logging(boolean logging) {
		this.logging = logging;
		return this;
	}

	protected void handleListenerSetQuery(Object target) {
		if (target instanceof CtQueryAware) {
			((CtQueryAware) target).setQuery(this);
		}
	}

	private void addStep(AbstractStep step) {
		step.nextStep = outputStep;
		lastStep.nextStep = step;
		lastStep = step;
		if (firstStep == outputStep) {
			firstStep = step;
		}
		step.setName(String.valueOf(getStepIndex(step) + 1));
	}

	private int getStepIndex(AbstractStep step) {
		int idx = 0;
		AbstractStep s = firstStep;
		while (s != outputStep) {
			if (s == step) {
				return idx;
			}
			s = (AbstractStep) s.nextStep;
			idx++;
		}
		return -1;
	}

	private boolean isLogging() {
		return logging;
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param step the step which thrown CCE
	 * @param e
	 * @param parameters
	 */
	private void onClassCastException(AbstractStep step, ClassCastException e, Object... parameters) {
		if (step.isFailOnCCE()) {
			throw new SpoonException(getStepDescription(step, e.getMessage(), parameters), e);
		} else if (Launcher.LOGGER.isTraceEnabled()) {
			//log expected CCE ... there might be some unexpected too!
			Launcher.LOGGER.trace(e);
		}
		log(step, e.getMessage(), parameters);
	}

	private void log(AbstractStep step, String message, Object... parameters) {
		if (isLogging() && Launcher.LOGGER.isInfoEnabled()) {
			Launcher.LOGGER.info(getStepDescription(step, message, parameters));
		}
	}

	private String getStepDescription(AbstractStep step, String message, Object... parameters) {
		StringBuilder sb = new StringBuilder("Step ");
		sb.append(step.getName()).append(") ");
		sb.append(message);
		for (int i = 0; i < parameters.length; i++) {
			sb.append("\nParameter ").append(i + 1).append(") ");
			if (parameters[i] != null) {
				sb.append(parameters[i].getClass().getSimpleName());
				sb.append(": ");
			}
			sb.append(parameters[i]);
		}
		return sb.toString();
	}

	/**
	 * Holds optional name and local QueryFailurePolicy of each step
	 */
	private abstract class AbstractStep implements CtConsumer<Object> {
		String name;
		QueryFailurePolicy localFailurePolicy = null;
		CtConsumer<Object> nextStep;

		/**
		 * @return name of this Step - for debugging purposes
		 */
		private String getName() {
			return name;
		}
		/**
		 * @param name of the step - for debugging purposes
		 */
		private void setName(String name) {
			this.name = name;
		}
		/**
		 * @return true if this step should throw {@link ClassCastException} in case of
		 * step input type incompatibility
		 */
		private boolean isFailOnCCE() {
			if (localFailurePolicy != null) {
				return localFailurePolicy  == QueryFailurePolicy.FAIL;
			} else {
				return failurePolicy == QueryFailurePolicy.FAIL;
			}
		}
		private void setLocalFailurePolicy(QueryFailurePolicy localFailurePolicy) {
			this.localFailurePolicy = localFailurePolicy;
		}
	}

	/**
	 * Wrapper around terminal {@link CtConsumer}, which accepts output of this query
	 */
	private class OutputFunctionWrapper extends AbstractStep {
		@Override
		public void accept(Object element) {
			if (element == null || isTerminated()) {
				return;
			}
			try {
				nextStep.accept(element);
			} catch (ClassCastException e) {
				if (Launcher.LOGGER.isTraceEnabled()) {
					//log expected CCE ... there might be some unexpected too!
					Launcher.LOGGER.trace(e);
				}
			}
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		<R> void setNext(CtConsumer<R> out) {
			//we are preparing new query execution.
			reset();
			nextStep = (CtConsumer) out;
			handleListenerSetQuery(nextStep);
		}
	}

	/**
	 * Called before query is evaluated again
	 */
	protected void reset() {
		terminated = false;
	}

	private class LazyFunctionWrapper extends AbstractStep {
		private final CtConsumableFunction<Object> fnc;

		@SuppressWarnings("unchecked")
		LazyFunctionWrapper(CtConsumableFunction<?> fnc) {
			super();
			this.fnc = (CtConsumableFunction<Object>) fnc;
			handleListenerSetQuery(this.fnc);
		}

		@Override
		public void accept(Object input) {
			if (input == null || isTerminated()) {
				return;
			}
			try {
				fnc.apply(input, nextStep);
			} catch (ClassCastException e) {
				onClassCastException(this, e, input);
				return;
			}
		}
	}

	/**
	 * a step which calls Function. Implements contract of {@link CtQuery#map(CtFunction)}
	 */
	private class FunctionWrapper extends AbstractStep {
		private CtFunction<Object, Object> fnc;

		@SuppressWarnings("unchecked")
		FunctionWrapper(CtFunction<?, ?> code) {
			super();
			fnc = (CtFunction<Object, Object>) code;
			handleListenerSetQuery(fnc);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void accept(Object input) {
			if (input == null || isTerminated()) {
				return;
			}
			Object result;
			try {
				result = fnc.apply(input);
			} catch (ClassCastException e) {
				onClassCastException(this, e, input);
				return;
			}
			if (result == null || isTerminated()) {
				return;
			}
			if (result instanceof Boolean) {
				//the code is a predicate. send the input to output if result is true
				if ((Boolean) result) {
					nextStep.accept(input);
				} else {
					log(this, "Skipped element, because CtFunction#accept(input) returned false", input);
				}
				return;
			}
			if (result instanceof Iterable) {
				//send each item of Iterable to the next step
				for (Object out : (Iterable<Object>) result) {
					nextStep.accept(out);
					if (isTerminated()) {
						return;
					}
				}
				return;
			}
			if (result.getClass().isArray()) {
				//send each item of Array to the next step
				for (int i = 0; i < Array.getLength(result); i++) {
					nextStep.accept(Array.get(result, i));
					if (isTerminated()) {
						return;
					}
				}
				return;
			}
			nextStep.accept(result);
		}
	}
}
