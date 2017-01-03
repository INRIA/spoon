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
import java.util.List;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

/**
 * Contains the default implementation of the generic {@link CtBaseQuery} methods
 */
public class CtBaseQueryImpl<O> implements CtBaseQuery<O> {

	private List<AbstractStep> steps = new ArrayList<>();

	private boolean logging = false;
	private QueryFailurePolicy failurePolicy = QueryFailurePolicy.FAIL;

	public CtBaseQueryImpl() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> CtBaseQuery<P> map(CtLazyFunction<?, P> code) {
		steps.add(new LazyFunctionWrapper(code));
		return (CtBaseQuery<P>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I, R> CtBaseQuery<R> map(CtFunction<I, R> function) {
		steps.add(new FunctionWrapper(function));
		return (CtBaseQuery<R>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtElement> CtBaseQuery<T> filterChildren(Filter<T> filter) {
		map(new ChildrenFilteringFunction(filter));
		stepFailurePolicy(QueryFailurePolicy.IGNORE);
		return (CtBaseQuery<T>) this;
	}

	@Override
	public void apply(Object input, CtConsumer<O> outputConsumer) {
		new CurrentStep(outputConsumer).accept(input);
	}

	@Override
	public CtBaseQuery<O> name(String name) {
		getLastStep().setName(name);
		return this;
	}
	@Override
	public CtBaseQuery<O> failurePolicy(QueryFailurePolicy policy) {
		failurePolicy = policy;
		return this;
	}
	public CtBaseQuery<O> stepFailurePolicy(QueryFailurePolicy policy) {
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
	public CtBaseQuery<O> logging(boolean logging) {
		this.logging = logging;
		return this;
	}

	/**
	 * @return number of steps of this query
	 */
	public int getLength() {
		return steps.size();
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
	 * and handles response of current step and sends it to next step
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
					AbstractStep step = steps.get(stepIdx - 1);
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
			return getStep().isFailOnCCE();
		}

		private AbstractStep getStep() {
			return CtBaseQueryImpl.this.getStep(stepIdx - 1);
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
		private final CtLazyFunction<Object, Object> fnc;

		@SuppressWarnings("unchecked")
		LazyFunctionWrapper(CtLazyFunction<?, ?> fnc) {
			super();
			this.fnc = (CtLazyFunction<Object, Object>) fnc;
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
	 * a step which calls Function. Implements contract of {@link CtBaseQuery#map(CtFunction)}
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
	private class ChildrenFilteringFunction extends CtScanner implements CtLazyFunction<CtElement, CtElement> {

		protected CurrentStep next;
		private Filter<CtElement> filter;

		@SuppressWarnings("unchecked")
		ChildrenFilteringFunction(Filter<? extends CtElement> filter) {
			this.filter = (Filter<CtElement>) filter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void apply(CtElement input, CtConsumer<CtElement> outputConsumer) {
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
