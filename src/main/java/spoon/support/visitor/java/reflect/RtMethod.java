/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RtMethod {
	private final Class<?> clazz;
	private final Method method;
	private final String name;
	private final Class<?> returnType;
	private final Type genericReturnType;
	private final TypeVariable<Method>[] typeParameters;
	private final Class<?>[] parameterTypes;
	private final Type[] genericParameterTypes;
	private final Class<?>[] exceptionTypes;
	private final int modifiers;
	private final Annotation[] annotations;
	private final Annotation[][] parameterAnnotations;
	private final boolean isVarArgs;
	private final boolean isDefault;

	public RtMethod(Class<?> clazz, Method method, String name, Class<?> returnType, Type genericReturnType, TypeVariable<Method>[] typeParameters, Class<?>[] parameterTypes, Type[] genericParameterTypes, Class<?>[] exceptionTypes, int modifiers,
			Annotation[] annotations, Annotation[][] parameterAnnotations, boolean isVarArgs, boolean isDefault) {
		this.clazz = clazz;
		this.method = method;
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

	public Method getMethod() {
		return method;
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

		if (!Objects.equals(name, rtMethod.name)) {
			return false;
		}
		if (!Objects.equals(returnType, rtMethod.returnType)) {
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

	/**
	 * @param method is the {@link Method} which is to be wrapped in a RtMethod
	 * @return a wrapper around the passed method
	 */
	public static RtMethod create(Method method) {
		return new RtMethod(method.getDeclaringClass(), method, method.getName(), method.getReturnType(),
				method.getGenericReturnType(), method.getTypeParameters(), method.getParameterTypes(), method.getGenericParameterTypes(), method.getExceptionTypes(),
				method.getModifiers(), method.getDeclaredAnnotations(), method.getParameterAnnotations(),
				method.isVarArgs(),
				method.isDefault());
	}

	public static <T> RtMethod[] methodsOf(Class<T> clazz) {
		final RtMethod[] methods = new RtMethod[clazz.getDeclaredMethods().length];
		int i = 0;
		for (Method method : clazz.getDeclaredMethods()) {
			methods[i++] = create(method);
		}
		return methods;
	}

	/** Returns the methods of `klass` that have the same signature (according to runtime reflection) but a different return type of at least one of the methods
	 * in `comparedMethods` given as parameter.
	 */
	public static <T> RtMethod[] sameMethodsWithDifferentTypeOf(Class<T> klass, List<RtMethod> comparedMethods) {
		final List<RtMethod> methods = new ArrayList<>();
		for (Method method : klass.getDeclaredMethods()) {
			final RtMethod rtMethod = create(method);
			for (RtMethod potential : comparedMethods) {
				if (potential.isLightEquals(rtMethod) && !rtMethod.returnType.equals(potential.returnType)) {
					methods.add(rtMethod);
				}
			}
		}
		return methods.toArray(new RtMethod[0]);
	}

	private boolean isLightEquals(RtMethod rtMethod) {
		if (this == rtMethod) {
			return true;
		}
		if (rtMethod == null || getClass() != rtMethod.getClass()) {
			return false;
		}

		if (!Objects.equals(name, rtMethod.name)) {
			return false;
		}
		return Arrays.equals(parameterTypes, rtMethod.parameterTypes);
	}
}
