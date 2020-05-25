/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.reflect;

import spoon.SpoonException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * To be compatible with Java 6, RtParameter has been created from
 * the Parameter class in Java 8.
 */
public class RtParameter {
	private final String name;
	private final Class<?> type;
	private final Type genericType;
	private final RtMethod method;
	private final Constructor constructor;
	private final int index;

	public RtParameter(String name, Class<?> type, Type genericType, RtMethod method, Constructor constructor, int index) {
		this.name = name;
		this.type = type;
		this.genericType = genericType;
		this.method = method;
		this.constructor = constructor;
		this.index = index;
	}

	/**
	 * Returns the name of the parameter.
	 *
	 * @return The name of the parameter, either provided by the class
	 * file or synthesized if the class file does not provide
	 * a name.
	 */
	public String getName() {
		// Note: empty strings as parameter names are now outlawed.
		// The .equals("") is for compatibility with current JVM
		// behavior.  It may be removed at some point.
		if (name == null || name.isEmpty()) {
			return "arg" + index;
		} else {
			return name;
		}
	}

	/**
	 * Returns the {@code Class} which is the type of the parameter.
	 *
	 * @return The type of the parameter.
	 */
	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType;
	}

	/**
	 * Return annotations which declared on the parameter.
	 *
	 * @return Annotations declared on the parameter.
	 */
	public Annotation[] getDeclaredAnnotations() {
		if (method == null) {
			/*
			 * According to oracle sources (for jdk 8) for java.lang.reflect.Executable#getParameterAnnotations(),
			 * the length of the returned array may vary at the discretion of the compiler.
			 * It seems that eclipse and javac do not produce the same results.
			 * In our case the value of index is based on getGenericParameterTypes(), so if
			 * getParameterAnnotations() returns a smaller array, we can assume that
			 * a synthetic parameter pointing to the outer class has been added at the begining of
			 * getGenericParameterTypes() and not in getParameterAnnotations().
			 * The actual index is then shifted by the difference.
			 */
			int diff = constructor.getGenericParameterTypes().length - constructor.getParameterAnnotations().length;
			return constructor.getParameterAnnotations()[index - diff];
		}
		return method.getParameterAnnotations()[index];
	}

	/**
	 * Returns {@code true} if this parameter represents a variable
	 * argument list; returns {@code false} otherwise.
	 *
	 * @return {@code true} if an only if this parameter represents a
	 * variable argument list.
	 */
	public boolean isVarArgs() {
		if (method == null) {
			return constructor.isVarArgs() && index == constructor.getParameterTypes().length - 1;
		}
		return method.isVarArgs() && index == method.getParameterTypes().length - 1;
	}

	/**
	 * Compares based on the executable and the index.
	 *
	 * @param obj
	 * 		The object to compare.
	 * @return Whether or not this is equal to the argument.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof RtParameter) {
			RtParameter other = (RtParameter) obj;
			if (method == null) {
				return (other.constructor.equals(constructor) && other.index == index);
			}
			return (other.method.equals(method) && other.index == index);
		}
		return false;
	}

	/**
	 * Returns a hash code based on the executable's hash code and the
	 * index.
	 *
	 * @return A hash code based on the executable's hash code.
	 */
	public int hashCode() {
		if (method == null) {
			return constructor.hashCode() ^ index;
		}
		return method.hashCode() ^ index;
	}

	/**
	 * Get parameters in a method to spoon parameters.
	 *
	 * @param method
	 * 		Parent executable of parameters.
	 * @return Parameters of the executable.
	 */
	public static RtParameter[] parametersOf(RtMethod method) {
		RtParameter[] parameters = new RtParameter[method.getParameterTypes().length];
		for (int index = 0; index < method.getParameterTypes().length; index++) {
			parameters[index] = new RtParameter(null, method.getParameterTypes()[index], method.getGenericParameterTypes()[index], method, null, index);
		}
		return parameters;
	}

	/**
	 * Get parameters in a constructor to spoon parameters.
	 *
	 * @param constructor
	 * 		Parent executable of parameters.
	 * @return Parameters of the executable.
	 */
	public static RtParameter[] parametersOf(Constructor constructor) {
		RtParameter[] parameters;
		// Apparently getGenericParameterTypes and getParameterTypes could have different length
		// if the first parameter is implicit: a private non-static inner class will have an implicit parameter for its superclass
		// but it won't be present in the result of getGenericParameterTypes (e.g. ArrayList$SubList)
		// moreover if it's an enum, there will be 2 implicit parameters (String and int) we won't consider them in the model.
		int lengthGenericParameterTypes = constructor.getGenericParameterTypes().length;
		int lengthParameterTypes = constructor.getParameterTypes().length;

		int offset;
		if (lengthGenericParameterTypes == lengthParameterTypes) {
			parameters = new RtParameter[lengthParameterTypes];
			offset = 0;
		} else if (lengthGenericParameterTypes == lengthParameterTypes - 1) {
			parameters = new RtParameter[lengthGenericParameterTypes];
			offset = 1;
		} else if (constructor.getDeclaringClass().isEnum() && lengthGenericParameterTypes == lengthParameterTypes - 2) {
			parameters = new RtParameter[lengthGenericParameterTypes];
			offset = 2;
		} else {
			throw new SpoonException("Error while analyzing parameters of constructor: " + constructor + ". # of parameters: " + lengthParameterTypes + " - # of generic parameter types: " + lengthGenericParameterTypes);
		}

		for (int index = 0; index < constructor.getGenericParameterTypes().length; index++) {
			parameters[index] = new RtParameter(null, constructor.getParameterTypes()[index + offset], constructor.getGenericParameterTypes()[index], null, constructor, index);
		}
		return parameters;
	}
}
