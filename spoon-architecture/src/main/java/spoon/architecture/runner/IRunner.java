package spoon.architecture.runner;

import java.lang.reflect.Method;

/**
 * This interface defines the method selection and invocation algorithm for a method.
 * The {@code #selectMethods(Object)} filters a meta model for suitable methods.
 * If a method is suitable as architecture test case is user defined.
 * <p>
 * The generic parameter defines the meta model in which the runner searches for architecture test methods.
 */
public interface IRunner<T> {
	/**
	 * Finds all suitable methods in a meta model suitable as architecture test case.
	 * @param model used a search room.
	 * @return an iterable of methods suitable as architecture test cases. Never returns null.
	 */
	Iterable<Method> selectMethods(T model);

	/**
	 * Invokes a given method. Invoking a method consists of creating the target, supplying the arguments and call the method.
	 * @param method  to be invoked. Never null.
	 */
	void invokeMethod(Method method);
}
