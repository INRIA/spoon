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
 * @param <T> the Type of the delegate object, which is target of method invocation
 */
public class SafeInvoker<T> {
	private final String methodName;

	private T delegate;
	private Class<?>[] paramTypes;
	private Method method;
	private int numParams;

	/**
	 *
	 * @param methodName
	 */
	public SafeInvoker(String methodName, int numParams) {
		this.methodName = methodName;
		this.numParams = numParams;
	}

	/**
	 * @return type of first parameter of invoked method or null if delegate does not have this method
	 */
	public Class<?> getParamType(int paramIdx) {
		return paramTypes[paramIdx];
	}

	/**
	 * @return the object on which is the method invoked
	 */
	public T getDelegate() {
		return delegate;
	}

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
	 *
	 * @param parameter
	 * @return true if parameter might be accepted by the invoked method
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

	public Object invoke(Object... parameter) {
		if (method != null && delegate != null && parameter != null) {
			try {
				return method.invoke(delegate, parameter);
			} catch (IllegalAccessException e) {
				throw new SpoonException(e);
			} catch (IllegalArgumentException e) {
				throw new SpoonException(e);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException) {
					throw (RuntimeException) e.getTargetException();
				}
				throw new SpoonException(e.getTargetException());
			}
		}
		return null;
	}

	protected void reportInputIgnored(int incompatibleParamIdx, Object[] parameters) {
		if (Launcher.LOGGER.isDebugEnabled()) {
			reportInputIgnored(parameters[incompatibleParamIdx].getClass().getName() + " cannot be cast to " + paramTypes[incompatibleParamIdx].getName(), parameters);
		}
	}

	public void onClassCastException(ClassCastException e, Object... parameters) {
		//note: we can detect acceptable types by parsing of e.getMessage()
		//spoon.support.reflect.reference.CtTypeReferenceImpl cannot be cast to spoon.reflect.declaration.CtClass
		if (Launcher.LOGGER.isTraceEnabled()) {
			Launcher.LOGGER.trace("Input ignored", e);
		} else {
			reportInputIgnored(e.getMessage(), parameters);
		}
	}

	public void reportInputIgnored(String message, Object... parameters) {
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
			Launcher.LOGGER.debug(sb.toString());
		}
	}
}
