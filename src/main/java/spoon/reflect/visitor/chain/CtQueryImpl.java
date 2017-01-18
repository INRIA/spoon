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
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

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

	public <R> void forEach(CtConsumer<R> consumer) {
		for (Object input : inputs) {
			evaluate(input, consumer);
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

	private List<AbstractStep> steps = new ArrayList<>();

	private boolean logging = false;
	private QueryFailurePolicy failurePolicy = QueryFailurePolicy.FAIL;

	@Override
	public <I> CtQueryImpl map(CtConsumableFunction<I> code) {
		steps.add(new LazyFunctionWrapper(code));
		return this;
	}

	@Override
	public <I, R> CtQueryImpl map(CtFunction<I, R> function) {
		steps.add(new FunctionWrapper(function));
		return this;
	}

	@Override
	public <R extends CtElement> CtQueryImpl filterChildren(Filter<R> filter) {
		map(new ChildrenFilteringFunction(filter));
		stepFailurePolicy(QueryFailurePolicy.IGNORE);
		return this;
	}

	/**
	 * Evaluates this query, ignoring bound input - if any
	 *
	 * @param input represents the input element of the first mapping function of this query
	 * @param outputConsumer method accept of the outputConsumer is called for each element produced by last mapping function of this query
	 */
	public <I, R> void evaluate(I input, CtConsumer<R> outputConsumer) {
		new CurrentStep(outputConsumer).accept(input);
	}

	@Override
	public CtQueryImpl name(String name) {
		getLastStep().setName(name);
		return this;
	}

	@Override
	public CtQueryImpl failurePolicy(QueryFailurePolicy policy) {
		failurePolicy = policy;
		return this;
	}

	public CtQueryImpl stepFailurePolicy(QueryFailurePolicy policy) {
		getLastStep().setLocalFailurePolicy(policy);
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

	private AbstractStep getStep(int stepIdx) {
		if (stepIdx >= steps.size()) {
			return null;
		}
		return steps.get(stepIdx);
	}

	private AbstractStep getLastStep() {
		if (steps.isEmpty()) {
			throw new SpoonException("There is no step in the query");
		}
		return getStep(steps.size() - 1);
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
	private void onClassCastException(CurrentStep step, ClassCastException e, Object... parameters) {
		if (step.isFailOnCCE()) {
			throw new SpoonException(getStepDescription(step, e.getMessage(), parameters), e);
		} else if (Launcher.LOGGER.isTraceEnabled()) {
			//log expected CCE ... there might be some unexpected too!
			Launcher.LOGGER.trace(e);
		}
		log(step, e.getMessage(), parameters);
	}

	private void log(CurrentStep step, String message, Object... parameters) {
		if (isLogging() && Launcher.LOGGER.isInfoEnabled()) {
			Launcher.LOGGER.info(getStepDescription(step, message, parameters));
		}
	}

	private String getStepDescription(CurrentStep step, String message, Object... parameters) {
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
	 * The thread local implementation of CtConsumer,
	 * which knows index of actually processed step
	 * and handles response of current step and sends it to next step.
	 *
	 * This class plays a role of an orchestrator to move the step cursor forward,
	 * get the step, apply it and finally to call the output consumer.
	 */
	private class CurrentStep implements CtConsumer<Object> {
		private final CtConsumer<?> outputConsumer;
		private int stepIdx = 0;

		CurrentStep(CtConsumer<?> outputConsumer) {
			this.outputConsumer = outputConsumer;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void accept(Object input) {
			if (input == null) {
				return;
			}
			stepIdx++;
			try {
				if (stepIdx <= steps.size()) {
					//process next intermediate step
					AbstractStep step = getStep();
					log(this, "received", input);
					try {
						step.apply(input, this);
					} catch (ClassCastException e) {
						onClassCastException(this, e, input);
					}
				} else {
					//send element to outputConsumer, it means return one value of the query
					log(this, "returning", input);
					try {
						((CtConsumer<Object>) outputConsumer).accept(input);
					} catch (ClassCastException e) {
						onClassCastException(this, e, input);
					}
				}
			} finally {
				stepIdx--;
			}
		}

		private String getName() {
			AbstractStep stepFunction = getStep();
			if (stepFunction == null) {
				return "outputConsumer";
			}
			String name = stepFunction.getName();
			if (name == null) {
				name = String.valueOf(stepIdx);
			}
			return name;
		}

		private boolean isFailOnCCE() {
			AbstractStep step = getStep();
			if (step == null) {
				//it is final consumer. Never throw CCE on final forEach consumer
				return false;
			}
			return step.isFailOnCCE();
		}

		private AbstractStep getStep() {
			return CtQueryImpl.this.getStep(stepIdx - 1);
		}

		@Override
		public String toString() {
			return "Step " + getName();
		}
	}

	/**
	 * Holds optional name and local QueryFailurePolicy of each step
	 */
	private abstract class AbstractStep {
		String name;
		QueryFailurePolicy localFailurePolicy = null;

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

		abstract void apply(Object input, CurrentStep outputConsumer);
	}

	private class LazyFunctionWrapper extends AbstractStep {
		private final CtConsumableFunction<Object> fnc;

		@SuppressWarnings("unchecked")
		LazyFunctionWrapper(CtConsumableFunction<?> fnc) {
			super();
			this.fnc = (CtConsumableFunction<Object>) fnc;
		}

		@Override
		public void apply(Object input, CurrentStep outputConsumer) {
			try {
				fnc.apply(input, outputConsumer);
			} catch (ClassCastException e) {
				onClassCastException(outputConsumer, e, input);
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
		}

		@SuppressWarnings("unchecked")
		@Override
		public void apply(Object input, CurrentStep outputConsumer) {
			Object result;
			try {
				result = fnc.apply(input);
			} catch (ClassCastException e) {
				onClassCastException(outputConsumer, e, input);
				return;
			}
			if (result == null) {
				return;
			}
			if (result instanceof Boolean) {
				//the code is a predicate. send the input to output if result is true
				if ((Boolean) result) {
					outputConsumer.accept(input);
				} else {
					log(outputConsumer, "Skipped element, because CtFunction#accept(input) returned false", input);
				}
				return;
			}
			if (result instanceof Iterable) {
				//send each item of Iterable to the next step
				for (Object out : (Iterable<Object>) result) {
					outputConsumer.accept(out);
				}
				return;
			}
			if (result.getClass().isArray()) {
				//send each item of Array to the next step
				for (int i = 0; i < Array.getLength(result); i++) {
					outputConsumer.accept(Array.get(result, i));
				}
				return;
			}
			outputConsumer.accept(result);
		}
	}

	/**
	 * a step which scans all children of input element and only elements matching filter go to the next step
	 */
	private class ChildrenFilteringFunction extends CtScanner implements CtConsumableFunction<CtElement> {

		protected CurrentStep next;
		private Filter<CtElement> filter;

		@SuppressWarnings("unchecked")
		ChildrenFilteringFunction(Filter<? extends CtElement> filter) {
			this.filter = (Filter<CtElement>) filter;
		}

		@Override
		public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
			next = (CurrentStep) (CtConsumer<?>) outputConsumer;
			scan(input);
		}
		@Override
		public void scan(CtElement element) {
			processFilter(element);
			super.scan(element);
		}

		private void processFilter(CtElement element) {
			if (element == null) {
				return;
			}
			boolean matches = false;
			try {
				matches = filter.matches(element);
			} catch (ClassCastException e) {
				onClassCastException(next, e, element);
				return;
			}
			if (matches) {
				//send input to output, because Fitler.matches returned true
				next.accept(element);
			} else {
				log(next, "Skipped child element, because Filter#matches(input) returned false", element);
			}
		}
	}
}
