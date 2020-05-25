/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CtScannerFunction;
import spoon.support.util.RtHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			Collections.addAll(this.inputs, input);
		}
		return this;
	}

	public CtQueryImpl addInput(Iterable<?> input) {
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
	public <R> List<R> list() {
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
		CtFunction fnc = new CtFunction<R, Boolean>() {
			@Override
			public Boolean apply(R input) {
				return filter.matches(input);
			}
		};
		FunctionWrapper fw = new FunctionWrapper(fnc);
		//set the expected type by real filter and not by helper wrapper above
		fw.onCallbackSet(fnc.getClass().getName(), "apply", filter.getClass(), "matches", 1, 0);
		addStep(fw);
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
		Class<?> expectedClass;
		String cceStacktraceClass;
		String cceStacktraceMethodName;

		@Override
		public final void accept(Object input) {
			if (input == null || isTerminated()) {
				return;
			}
			if (isAcceptableType(input) == false) {
				return;
			}
			Object result;
			try {
				result = _accept(input);
			} catch (ClassCastException e) {
				onClassCastException(e, input);
				return;
			}
			if (result == null || isTerminated()) {
				return;
			}
			handleResult(result, input);
		}

		protected abstract Object _accept(Object input);

		protected void handleResult(Object result, Object input) {
		}

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

		/**
		 * check whether `input` can be used to call a function.
		 * @param input the to be checked value
		 * @return true if it can be used or if we do not know that yet
		 */
		protected boolean isAcceptableType(Object input) {
			if (isFailOnCCE()) {
				//do not check type if it has to fail on cce
				return true;
			}
			if (expectedClass != null && expectedClass.isAssignableFrom(input.getClass()) == false) {
				if (isLogging()) {
					log(this, input.getClass().getName() + " cannot be cast to " + expectedClass.getName(), input);
				}
				return false;
			}
			return true;
		}

		/**
		 * Sets up type checking following the type of input parameter of callback method
		 * @param stackClass - name of class of method in the stacktrace, if ClassCastException is thrown on the input parameter of lambda expression
		 * @param stackMethodName - name of method in the stacktrace, if ClassCastException is thrown on the input parameter of lambda expression
		 * @param callbackClass - the class of callback method
		 * @param callbackMethod - the name of callback method
		 * @param nrOfParams - total number of input parameters of callback method
		 * @param idxOfInputParam - index of input parameter, whose type has to be checked
		 */
		protected void onCallbackSet(String stackClass, String stackMethodName, Class<?> callbackClass, String callbackMethod, int nrOfParams, int idxOfInputParam) {
			this.cceStacktraceClass = stackClass;
			this.cceStacktraceMethodName = stackMethodName;
			if (callbackClass.getName().contains("$$Lambda$")) {
				//lambda expressions does not provide runtime information about type of input parameter
				//clear it now. We can detect input type from first ClassCastException
				this.expectedClass = null;
			} else {
				Method method = RtHelper.getMethod(callbackClass, callbackMethod, nrOfParams);
				if (method == null) {
					throw new SpoonException("The method " + callbackMethod + " with one parameter was not found on the class " + callbackClass.getName());
				}
				this.expectedClass = method.getParameterTypes()[idxOfInputParam];
			}
		}

		/**
		 * Is used to log that invocation was not processed
		 * @param e - the CCE caught during last call of callback
		 * @param input - the value sent as input to last call of callback
		 */
		protected void onClassCastException(ClassCastException e, Object input) {
			if (isFailOnCCE() || expectedClass != null) {
				//expected class is known so it was checked before the call, so the CCE must be thrown by something else. Report it directly as it is. It is bug in client's code
				throw e;
			}
			if (indexOfCallerInStack < 0) {
				//this is an exotic JVM, where we cannot detect type of parameter of Lambda expression
				//Silently ignore this CCE, which was may be expected or may be problem in client's code.
				return;
			}
			//we can detect whether CCE was thrown in client's code (unexpected - must be rethrown) or Query engine (expected - has to be ignored)
			StackTraceElement[] stackEles = e.getStackTrace();
			if (stackEles.length == 0) {
				/*
				 * The java runtime detected that this ClassCastException is thrown often and recompiled code to use faster pre-alocated exception,
				 * which doesn't provide stacktrace.
				 * So exceptions, which doesn't provide stacktrace can be ignored too, because they were already ignored before many times.
				 *
				 * See http://www.oracle.com/technetwork/java/javase/relnotes-139183.html#vm
				 *---------------------------------------------------------------------------------------------------------------
				 * The compiler in the server VM now provides correct stack backtraces for all "cold" built-in exceptions.
				 * For performance purposes, when such an exception is thrown a few times, the method may be recompiled.
				 * After recompilation, the compiler may choose a faster tactic using preallocated exceptions that do not provide a stack trace.
				 * To disable completely the use of preallocated exceptions, use this new flag: -XX:-OmitStackTraceInFastThrow.
				 *---------------------------------------------------------------------------------------------------------------
				 */
				return;
			}
			StackTraceElement stackEle = stackEles[indexOfCallerInStack];
			if (stackEle.getMethodName().equals(cceStacktraceMethodName) && stackEle.getClassName().equals(cceStacktraceClass)) {
				/*
				 * the CCE exception was thrown in the expected method - OK, it can be ignored
				 * Detect type of parameter of Lambda expression from the CCE message and store it in expectedClass
				 * so we can check expected type before next call and to avoid slow throwing of ClassCastException
				 */
				expectedClass = detectTargetClassFromCCE(e, input);
				if (expectedClass == null) {
					/*
					 * It wasn't able to detect expected class from the CCE.
					 * OK, so we cannot optimize next call and we have to let JVM to throw next CCE, but it is only performance problem. Not functional.
					 */
				}
				log(this, e.getMessage(), input);
				return;
			}
			//Do not ignore this exception in client's code. It is not expected. It cannot be ignored.
			throw e;
		}
	}

	/**
	 * Wrapper around terminal {@link CtConsumer}, which accepts output of this query
	 */
	private class OutputFunctionWrapper extends AbstractStep {
		OutputFunctionWrapper() {
			localFailurePolicy = QueryFailurePolicy.IGNORE;
		}
		@Override
		protected Object _accept(Object element) {
			nextStep.accept(element);
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		<R> void setNext(CtConsumer<R> out) {
			//we are preparing new query execution.
			reset();
			nextStep = (CtConsumer) out;
			handleListenerSetQuery(nextStep);
			onCallbackSet(this.getClass().getName(), "_accept", nextStep.getClass(), "accept", 1, 0);
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
			this.fnc = (CtConsumableFunction<Object>) fnc;
			handleListenerSetQuery(this.fnc);
			onCallbackSet(this.getClass().getName(), "_accept", fnc.getClass(), "apply", 2, 0);
		}

		@Override
		protected Object _accept(Object input) {
			fnc.apply(input, nextStep);
			return null;
		}
	}

	/**
	 * a step which calls Function. Implements contract of {@link CtQuery#map(CtFunction)}
	 */
	private class FunctionWrapper extends AbstractStep {
		private final CtFunction<Object, Object> fnc;

		@SuppressWarnings("unchecked")
		FunctionWrapper(CtFunction<?, ?> code) {
			fnc = (CtFunction<Object, Object>) code;
			handleListenerSetQuery(fnc);
			onCallbackSet(this.getClass().getName(), "_accept", fnc.getClass(), "apply", 1, 0);
		}

		@Override
		protected Object _accept(Object input) {
			return fnc.apply(input);
		}

		@Override
		protected void handleResult(Object result, Object input) {
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

	private static final String JDK9_BASE_PREFIX = "java.base/";

	//Pre jdk11 ClassCastException message parsing
	private static final Pattern cceMessagePattern = Pattern.compile("(\\S+) cannot be cast to (\\S+)");

	//In some implementation of jdk11 the message for ClassCastException is slightly different
	private static final Pattern cceMessagePattern2 = Pattern.compile("class (\\S+) cannot be cast to class (\\S+)(.*)");

	private static final int indexOfCallerInStack = getIndexOfCallerInStackOfLambda();
	/**
	 * JVM implementations reports exception in call of lambda in different way.
	 * A) the to be called lambda expression whose input parameters are invalid is on top of stack trace
	 * B) the to be called lambda expression whose input parameters are invalid is NOT in stack trace at all
	 *
	 * This method detects actual behavior of JVM, so the code, which decides whether ClassCastException is expected (part of filtering process)
	 * or unexpected - thrown by clients wrong code works on all JVM implementations
	 */
	private static int getIndexOfCallerInStackOfLambda() {
		CtConsumer<CtType<?>> f = (CtType<?> t) -> { };
		CtConsumer<Object> unchecked = (CtConsumer) f;
		Object obj = new Integer(1);
		try {
			unchecked.accept(obj);
			throw new SpoonException("The lambda expression with input type CtType must throw ClassCastException when input type is Integer. Basic CtQuery contract is violated by JVM!");
		} catch (ClassCastException e) {
			StackTraceElement[] stack = e.getStackTrace();
			for (int i = 0; i < stack.length; i++) {
				if ("getIndexOfCallerInStackOfLambda".equals(stack[i].getMethodName())) {
					//check whether we can detect type of lambda input parameter from CCE
					Class<?> detectedClass = detectTargetClassFromCCE(e, obj);
					if (detectedClass == null || CtType.class.equals(detectedClass) == false) {
						//we cannot detect type of lambda input parameter from ClassCastException on this JVM implementation
						//mark it by negative index, so the query engine will fall back to eating of all CCEs and slow implementation
						return -1;
					}
					return i;
				}
			}
			throw new SpoonException("Spoon cannot detect index of caller of lambda expression in stack trace.", e);
		}
	}

	private static Class<?> processCCE(String objectClassName, String expectedClassName, Object input) {
		if (objectClassName.startsWith(JDK9_BASE_PREFIX)) {
			objectClassName = objectClassName.substring(JDK9_BASE_PREFIX.length());
		}
		if (objectClassName.equals(input.getClass().getName())) {
			try {
				return Class.forName(expectedClassName);
			} catch (ClassNotFoundException e1) {
				/*
				 * It wasn't able to load the expected class from the CCE.
				 * OK, so we cannot optimize next call and we have to let JVM to throw next CCE, but it is only performance problem. Not functional.
				 */
			}
		}
		return null;
	}

	private static Class<?> detectTargetClassFromCCE(ClassCastException e, Object input) {
		//detect expected class from CCE message, because we have to quickly and silently ignore elements of other types
		String message = e.getMessage();
		if (message != null) {
			Matcher m = cceMessagePattern.matcher(message);
			if (m.matches()) {
				return processCCE(m.group(1), m.group(2), input);
			} else {
				Matcher m2 = cceMessagePattern2.matcher(message);
				if (m2.matches()) {
					return processCCE(m2.group(1), m2.group(2), input);
				}
			}
		}
		return null;
	}
}
