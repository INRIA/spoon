/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a helper for runtime reflection.
 */
public abstract class RtHelper {

	private RtHelper() {
	}

	/**
	 * Gets all the runtime fields for a given class (including the
	 * superclasses and superinterfaces).
	 */
	public static Field[] getAllFields(Class<?> c) {
		List<Field> fields = new ArrayList<>();
		addAllFields(c, fields);
		Field[] result = new Field[fields.size()];
		return fields.toArray(result);
	}

	private static void addAllFields(Class<?> c, List<Field> fields) {
		if (c != null && c != Object.class) {
			Collections.addAll(fields, c.getDeclaredFields());
			addAllFields(c.getSuperclass(), fields);
			for (Class<?> iface : c.getInterfaces()) {
				addAllFields(iface, fields);
			}
		}
	}

	/**
	 * Gets all the field references for a given class (including the
	 * superclasses').
	 */
	public static Collection<CtFieldReference<?>> getAllFields(Class<?> c, Factory factory) {
		Collection<CtFieldReference<?>> l = new ArrayList<>();
		for (Field f : getAllFields(c)) {
			l.add(factory.Field().createReference(f));
		}
		return l;
	}

	/**
	 * Gets all the runtime methods for a given class or interface (including
	 * the superclasses' or interfaces').
	 */
	public static Method[] getAllMethods(Class<?> c) {
		List<Method> methods = new ArrayList<>();
		if (c.isInterface()) {
			getAllIMethods(c, methods);
		} else {
			while (c != null && c != Object.class) {
				Collections.addAll(methods, c.getDeclaredMethods());
				c = c.getSuperclass();
			}
		}
		Method[] result = new Method[methods.size()];
		return methods.toArray(result);
	}

	private static void getAllIMethods(Class<?> c, List<Method> methods) {
		Collections.addAll(methods, c.getDeclaredMethods());
		for (Class<?> i : c.getInterfaces()) {
			getAllIMethods(i, methods);
		}
	}

	/**
	 * Actually invokes from a compile-time invocation (by using runtime
	 * reflection).
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(CtInvocation<T> i)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Object target = i.getTarget() == null ? null : ((CtLiteral<?>) i.getTarget()).getValue();
		List<Object> args = new ArrayList<>();
		for (CtExpression<?> e : i.getArguments()) {
			args.add(((CtLiteral<?>) e).getValue());
		}
		Class<?> c = i.getExecutable().getDeclaringType().getActualClass();
		ArrayList<Class<?>> argTypes = new ArrayList<>();
		for (CtTypeReference<?> type : i.getExecutable().getActualTypeArguments()) {
			argTypes.add(type.getActualClass());
		}
		return (T) c.getMethod(i.getExecutable().getSimpleName(), argTypes.toArray(new Class[0]))
				.invoke(target, args.toArray());
	}

	/**
	 * Return the set of modifiers defined by the modifiers integer
	 * (java.lang.reflect).
	 */
	public static Set<ModifierKind> getModifiers(int mod) {
		Set<ModifierKind> set = new HashSet<>();
		if (Modifier.isAbstract(mod)) {
			set.add(ModifierKind.ABSTRACT);
		}
		if (Modifier.isFinal(mod)) {
			set.add(ModifierKind.FINAL);
		}
		if (Modifier.isNative(mod)) {
			set.add(ModifierKind.NATIVE);
		}
		if (Modifier.isPrivate(mod)) {
			set.add(ModifierKind.PRIVATE);
		}
		if (Modifier.isProtected(mod)) {
			set.add(ModifierKind.PROTECTED);
		}
		if (Modifier.isPublic(mod)) {
			set.add(ModifierKind.PUBLIC);
		}
		if (Modifier.isStatic(mod)) {
			set.add(ModifierKind.STATIC);
		}
		if (Modifier.isStrict(mod)) {
			set.add(ModifierKind.STRICTFP);
		}
		if (Modifier.isSynchronized(mod)) {
			set.add(ModifierKind.SYNCHRONIZED);
		}
		if (Modifier.isTransient(mod)) {
			set.add(ModifierKind.TRANSIENT);
		}
		if (Modifier.isVolatile(mod)) {
			set.add(ModifierKind.VOLATILE);
		}
		return set;
	}

	/**
	 * return all executables of this class
	 */
	public static Collection<CtExecutableReference<?>> getAllExecutables(Class<?> clazz, Factory factory) {
		Collection<CtExecutableReference<?>> l = new ArrayList<>();
		for (Method m : clazz.getDeclaredMethods()) {
			l.add(factory.Method().createReference(m));
		}
		for (Constructor<?> c : clazz.getDeclaredConstructors()) {
			l.add(factory.Constructor().createReference(c));
		}
		return l;
	}

	/**
	 * Looks for first public method of clazz (or any super class or super interface),
	 * whose name is equal to methodName and number of parameters is numParams
	 * @param clazz
	 * @param methodName
	 * @param numParams
	 * @return the found method or null
	 */
	public static Method getMethod(Class<?> clazz, String methodName, int numParams) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.isSynthetic() == false && method.getName().equals(methodName)) {
				Class<?>[] params = method.getParameterTypes();
				if (params.length == numParams) {
					return method;
				}
			}
		}
		return null;
	}
}
