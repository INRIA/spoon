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
import java.util.Iterator;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

/**
 * Contains the default implementation of the generic {@link CtBaseQuery} methods
 */
public class CtBaseQueryImpl<O> implements CtBaseQuery<O> {

	/**
	 * first step of the query
	 */
	private Step firstStep;
	/**
	 * last step of the query
	 */
	private Step lastStep;
	/**
	 * the Step which wraps final Consumer, which is always present in each (even empty) query
	 */
	private Step tail;

	private boolean logging = false;
	private QueryFailurePolicy failurePolicy = QueryFailurePolicy.FAIL;

	public CtBaseQueryImpl() {
		tail = new TailConsumer();
		tail.setName("TailConsumer");
		firstStep = tail;
		lastStep = tail;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> CtBaseQuery<P> map(CtLazyFunction<?, P> code) {
		add(new QueryStepWrapper(code));
		return (CtBaseQuery<P>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I, R> CtBaseQuery<R> map(CtFunction<I, R> function) {
		add(new FunctionWrapper(function));
		return (CtBaseQuery<R>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtElement> CtBaseQuery<T> filterChildren(Filter<T> filter) {
		add(new ChildrenFilteringWrapper(filter));
		return (CtBaseQuery<T>) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void apply(Object input, CtConsumer<O> output) {
		tail.setNext((CtConsumer<Object>) output);
		try {
			firstStep.accept(input);
		} finally {
			tail.setNext(null);
		}
	}

	@Override
	public CtBaseQuery<O> name(String name) {
		if (lastStep == tail) {
			throw new SpoonException("Cannot set name of the step on the chain with no step");
		}
		lastStep.setName(name);
		return this;
	}
	@Override
	public CtBaseQuery<O> failurePolicy(QueryFailurePolicy policy) {
		if (lastStep == tail) {
			throw new SpoonException("Cannot set ignoreIncompatibleInput of the step on the chain with no step");
		}
		failurePolicy = policy;
		return this;
	}

	private boolean isLogging() {
		return logging;
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

	private void add(Step step) {
		if (lastStep == tail) {
			firstStep = step;
			lastStep = step;
		} else {
			lastStep.setNext(step);
			lastStep = step;
		}
		step.setNext(tail);
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
			s = (Step) s.getNext();
		}
		return len;
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param step the step which thrown CCE
	 * @param e
	 * @param parameters
	 */
	private void onClassCastException(Step step, ClassCastException e, Object... parameters) {
		if (step.isFailOnCCE()) {
			throw e;
		} else if (Launcher.LOGGER.isTraceEnabled()) {
			//log expected CCE ... there might be some unexpected too!
			Launcher.LOGGER.trace(e);
		}
		log(step, e.getMessage(), parameters);
	}

	private void log(Step step, String message, Object... parameters) {
		if (isLogging() && Launcher.LOGGER.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("Step ");
			sb.append(step.getName()).append(") ");
			sb.append(message);
			for (int i = 0; i < parameters.length; i++) {
				sb.append("\nParameter ").append(i + 1).append(") ");
				sb.append(parameters[i]);
			}
			Launcher.LOGGER.info(sb.toString());
		}
	}

	/**
	 * Each Step must implement it
	 */
	private interface Step extends CtConsumer<Object> {
		/**
		 * @return next {@link CtConsumer}
		 */
		CtConsumer<Object> getNext();
		/**
		 * sets next {@link CtConsumer}
		 */
		void setNext(CtConsumer<Object> next);
		/**
		 * @return name of this Step - for debugging purposes
		 */
		String getName();
		/**
		 * @param name of the step - for debugging purposes
		 */
		void setName(String name);
		/**
		 * @return true if this step should throw {@link ClassCastException} in case of
		 * step input type incompatibility
		 */
		boolean isFailOnCCE();
	}

	/**
	 * abstract step which knows next Consumer and then name
	 */
	private abstract class AbstractStep implements Step {
		private String name;
		protected CtConsumer<Object> next;
		@Override
		public CtConsumer<Object> getNext() {
			return next;
		}
		@Override
		public void setNext(CtConsumer<Object> next) {
			this.next = next;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public boolean isFailOnCCE() {
			return failurePolicy == QueryFailurePolicy.FAIL;
		}
		public final void accept(Object input) {
			if (input == null) {
				return;
			}
			log(this, "received", input);
			processInput(input);
		}
		protected abstract void processInput(Object input);
	}

	/**
	 * There is always one TailConsumer in each query, which sends result to output
	 */
	private class TailConsumer extends AbstractStep {
		@Override
		public void processInput(Object out) {
			if (next != null) {
				try {
					next.accept(out);
				} catch (ClassCastException e) {
					onClassCastException(this, e, out);
				}
			}
		}
	}

	/**
	 * a step which calls ChainableFunction. Implements contract of {@link CtBaseQuery#map(CtLazyFunction)}
	 */
	private class QueryStepWrapper extends AbstractStep {
		private CtLazyFunction<Object, Object> fnc;

		@SuppressWarnings("unchecked")
		QueryStepWrapper(CtLazyFunction<?, ?> code) {
			fnc = (CtLazyFunction<Object, Object>) code;
		}
		@Override
		public void processInput(Object input) {
			try {
				fnc.apply(input, next);
			} catch (ClassCastException e) {
				onClassCastException(this, e, input);
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
			fnc = (CtFunction<Object, Object>) code;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void processInput(Object input) {
			Object result;
			try {
				result = fnc.apply(input);
			} catch (ClassCastException e) {
				onClassCastException(this, e, input);
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
					log(this, "Skipped element, because CtFunction#accept(input) returned false", input);
				}
				return;
			}
			if (result instanceof Iterable) {
				//send each item of Iterable to the next step
				for (Object out : (Iterable<Object>) result) {
					next.accept(out);
				}
				return;
			}
			if (result instanceof Iterator) {
				Iterator<Object> iter = (Iterator<Object>) result;
				//send each item of Iterator to the next step
				while (iter.hasNext()) {
					next.accept(iter.next());
				}
				return;
			}
			if (result.getClass().isArray()) {
				//send each item of Array to the next step
				for (int i = 0; i < Array.getLength(result); i++) {
					next.accept(Array.get(result, i));
				}
				return;
			}
			next.accept(result);
		}
	}

	/**
	 * a step which scans all children of input element and only elements matching filter go to the next step
	 */
	private class ChildrenFilteringWrapper extends CtScanner implements Step {

		private String name;
		protected CtConsumer<Object> next;
		private Filter<CtElement> filter;

		@SuppressWarnings("unchecked")
		ChildrenFilteringWrapper(Filter<? extends CtElement> filter) {
			this.filter = (Filter<CtElement>) filter;
		}

		@Override
		public void accept(Object input) {
			if (input == null) {
				return;
			}
			log(this, "CtScanner received", input);
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
				onClassCastException(this, e, element);
				return;
			}
			if (matches) {
				//send input to output, because Fitler.matches returned true
				next.accept(element);
			} else {
				log(this, "Skipped child element, because Filter#matches(input) returned false", element);
			}
		}

		@Override
		public CtConsumer<Object> getNext() {
			return next;
		}
		@Override
		public void setNext(CtConsumer<Object> next) {
			this.next = next;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public boolean isFailOnCCE() {
			//never fail on CCE during Filter.matching
			return false;
		}
	}
}
