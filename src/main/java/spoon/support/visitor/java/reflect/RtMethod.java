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
package spoon.support.visitor.java.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spoon.SpoonException;

public class RtMethod {
	private Class<?> clazz;
	private String name;
	private Class<?> returnType;
	private Type genericReturnType;
	private TypeVariable<Method>[] typeParameters;
	private Class<?>[] parameterTypes;
	private Type[] genericParameterTypes;
	private Class<?>[] exceptionTypes;
	private int modifiers;
	private Annotation[] annotations;
	private Annotation[][] parameterAnnotations;
	private boolean isVarArgs;
	private boolean isDefault;

	public RtMethod(Class<?> clazz, String name, Class<?> returnType, Type genericReturnType, TypeVariable<Method>[] typeParameters, Class<?>[] parameterTypes, Type[] genericParameterTypes, Class<?>[] exceptionTypes, int modifiers, Annotation[] annotations,
			Annotation[][] parameterAnnotations, boolean isVarArgs, boolean isDefault) {
		this.clazz = clazz;
		this.name = name;
		this.returnType = returnType;
		this.genericReturnType = genericReturnType;
		this.typeParameters = typeParameters;
		this.parameterTypes = parameterTypes;
		this.genericParameterTypes = genericParameterTypes;
		this.exceptionTypes = exceptionTypes;
		this.modifiers = modifiers;
		this.annotations = annotations;
		this.parameterAnnotations = parameterAnnotations;
		this.isVarArgs = isVarArgs;
		this.isDefault = isDefault;
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public TypeVariable<Method>[] getTypeParameters() {
		return typeParameters;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Class<?>[] getExceptionTypes() {
		return exceptionTypes;
	}

	public int getModifiers() {
		return modifiers;
	}

	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	public Annotation[][] getParameterAnnotations() {
		return parameterAnnotations;
	}

	public boolean isVarArgs() {
		return isVarArgs;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public Type getGenericReturnType() {
		return genericReturnType;
	}

	public Type[] getGenericParameterTypes() {
		return genericParameterTypes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RtMethod rtMethod = (RtMethod) o;

		if (name != null ? !name.equals(rtMethod.name) : rtMethod.name != null) {
			return false;
		}
		if (returnType != null ? !returnType.equals(rtMethod.returnType) : rtMethod.returnType != null) {
			return false;
		}
		if (!Arrays.equals(parameterTypes, rtMethod.parameterTypes)) {
			return false;
		}
		return Arrays.equals(exceptionTypes, rtMethod.exceptionTypes);
	}

	@Override
	public int hashCode() {
		return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
	}

	public static RtMethod create(Method method) {
		return new RtMethod(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getGenericReturnType(),
				method.getTypeParameters(), method.getParameterTypes(), method.getGenericParameterTypes(), method.getExceptionTypes(), method.getModifiers(),
				method.getDeclaredAnnotations(), method.getParameterAnnotations(), method.isVarArgs(),
				//spoon is compatible with Java 7, so compilation fails here
				//method.isDefault());
				_java8_isDefault(method));
	}

	private static Method _method_isDefault;
	static {
		try {
			_method_isDefault = Method.class.getMethod("isDefault");
		} catch (NoSuchMethodException | SecurityException e) {
			//spoon is running with java 7 JDK
			_method_isDefault = null;
		}
	}

	private static boolean _java8_isDefault(Method method) {
		if (_method_isDefault == null) {
			//spoon is running with java 7 JDK, all methods are not default, because java 7 does not have default methods
			return false;
		}
		try {
			return (Boolean) _method_isDefault.invoke(method);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new SpoonException("Calling of Java8 Method#isDefault() failed", e);
		} catch (InvocationTargetException e) {
			throw new SpoonException("Calling of Java8 Method#isDefault() failed", e.getTargetException());
		}
	}

	public static <T> RtMethod[] methodsOf(Class<T> clazz) {
		final RtMethod[] methods = new RtMethod[clazz.getDeclaredMethods().length];
		int i = 0;
		for (Method method : clazz.getDeclaredMethods()) {
			methods[i++] = create(method);
		}
		return methods;
	}

	public static <T> RtMethod[] sameMethodsWithDifferentTypeOf(Class<T> superClass, List<RtMethod> comparedMethods) {
		final List<RtMethod> methods = new ArrayList<>();
		for (Method method : superClass.getDeclaredMethods()) {
			final RtMethod rtMethod = create(method);
			for (RtMethod potential : comparedMethods) {
				if (potential.isLightEquals(rtMethod) && !rtMethod.returnType.equals(potential.returnType)) {
					methods.add(rtMethod);
				}
			}
		}
		return methods.toArray(new RtMethod[methods.size()]);
	}

	private boolean isLightEquals(RtMethod rtMethod) {
		if (this == rtMethod) {
			return true;
		}
		if (rtMethod == null || getClass() != rtMethod.getClass()) {
			return false;
		}

		if (name != null ? !name.equals(rtMethod.name) : rtMethod.name != null) {
			return false;
		}
		return Arrays.equals(parameterTypes, rtMethod.parameterTypes);
	}
}
