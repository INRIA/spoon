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
package spoon.support.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import spoon.Launcher;
import spoon.SpoonException;

/**
 * The helper object, which provides safe calling of defined method of delegate object by the safe way.
 * The safe means, that if the input parameters cannot be cast to the input parameters of the called method
 * and ClassCastException is thrown then such call is optionally logged and silently ignored
 *
 * @param <T> the Type of the delegate object, which is target of the method invocation
 */
public class SafeInvoker<T> {
	private final String methodName;

	private T delegate;
	private Class<?>[] paramTypes;
	private Method method;
	private int numParams;

	/**
	 * @param methodName - the name of the method, which will be invoked on the delegate object
	 * @param numParams - the number of parameters of the called method
	 */
	public SafeInvoker(String methodName, int numParams) {
		this.methodName = methodName;
		this.numParams = numParams;
	}

	/**
	 * @return type of idx-th parameter of invoked method or null if delegate does not have this method
	 */
	public Class<?> getParamType(int idx) {
		return paramTypes == null ? null : paramTypes[idx];
	}

	/**
	 * @return the object on which is the method invoked
	 */
	public T getDelegate() {
		return delegate;
	}

	/**
	 * sets the object on which will be the this of invoked method
	 *
	 * @param delegate
	 */
	public void setDelegate(T delegate) {
		if (delegate != null) {
			method = RtHelper.getMethod(delegate.getClass(), methodName, this.numParams);
			if (method != null) {
				paramTypes = method.getParameterTypes();
				method.setAccessible(true);
			} else {
				paramTypes = null;
			}
		} else {
			paramTypes = null;
			method = null;
		}
		this.delegate = delegate;
	}

	/**
	 * @return true if delegate has a required method
	 */
	public boolean hasMethod() {
		return method != null;
	}

	/**
	 * @param parameter
	 * @return true if parameter might be accepted by the invoked method.
	 * Note that in some cases (Lambda expressions) we cannot detect runtime type of the parameters and therefore the invocation will cause ClassCastException. Handle it.
	 */
	public boolean isParameterTypeAssignableFrom(Object... parameters) {
		if (paramTypes == null) {
			return false;
		}
		if (parameters.length != numParams) {
			throw new SpoonException("Invalid number of parameters");
		}
		for (int i = 0; i < paramTypes.length; i++) {
			if (parameters[i] != null && paramTypes[i].isAssignableFrom(parameters[i].getClass()) == false) {
				reportInputIgnored(i, parameters);
				return false;
			}
		}
		return true;
	}

	/**
	 * Invokes the defined method on the defined delegate object
	 * @param parameter
	 * @return
	 */
	public Object invoke(Object... parameter) {
		if (method != null && delegate != null && parameter != null) {
			try {
				return method.invoke(delegate, parameter);
			} catch (IllegalAccessException e) {
				throw new SpoonException(e);
			} catch (IllegalArgumentException e) {
				throw new SpoonException(e);
			} catch (InvocationTargetException e) {
				Throwable targetE = e.getTargetException();
				if (targetE instanceof RuntimeException) {
					if (targetE instanceof ClassCastException) {
						//log that invocation was ignored, because of incompatible input parameters
						onClassCastException((ClassCastException) targetE, parameter);
					}
					/*
					 * needed to re-throw ClassCastException.
					 * TODO: we should check the stacktrace of the CCE and eat it silently only when it is thrown from the method invocation code.
					 * We should throw normal ClassCastException when the code fails later, because of the bug in the client's code!
					 *
					 * Note: The CCE message has form like
					 * spoon.support.reflect.reference.CtTypeReferenceImpl cannot be cast to spoon.reflect.declaration.CtClass
					 * By parsing of this exception we would be able to detect
					 * acceptable parameter type of the Lambda expression and avoid the CCE during the future calls - it might improve performance.
					 */
					throw (RuntimeException) targetE;
				}
				throw new SpoonException(targetE);
			}
		}
		return null;
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param incompatibleParamIdx
	 * @param parameters
	 */
	protected void reportInputIgnored(int incompatibleParamIdx, Object[] parameters) {
		if (Launcher.LOGGER.isDebugEnabled()) {
			reportInputIgnored(parameters[incompatibleParamIdx].getClass().getName() + " cannot be cast to " + paramTypes[incompatibleParamIdx].getName(), null, parameters);
		}
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param e
	 * @param parameters
	 */
	protected void onClassCastException(ClassCastException e, Object... parameters) {
		if (Launcher.LOGGER.isDebugEnabled()) {
			reportInputIgnored(e.getMessage(), e, parameters);
		}
	}

	/**
	 * Is used to log that invocation was not processed
	 * @param message
	 * @param e
	 * @param parameters
	 */
	protected void reportInputIgnored(String message, Throwable e, Object... parameters) {
		if (Launcher.LOGGER.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Input [");
			for (int i = 0; i < parameters.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(parameters[i]);
			}
			sb.append("] ignored because ").append(message);
			if (Launcher.LOGGER.isTraceEnabled() && e != null) {
				Launcher.LOGGER.trace(sb.toString(), e);
			} else {
				Launcher.LOGGER.debug(sb.toString());
			}
		}
	}
}
