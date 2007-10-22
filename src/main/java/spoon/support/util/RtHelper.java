/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

/**
 * This class is a helper for runtime reflection.
 */
public abstract class RtHelper {

	private RtHelper() {
	}

	/**
	 * Gets all the runtime fields for a given class (including the
	 * superclasses').
	 */
	public static Field[] getAllFields(Class<?> c) {
		List<Field> fields = new ArrayList<Field>();
		while (c != null && c != Object.class) {
			for (Field f : c.getDeclaredFields()) {
				fields.add(f);
			}
			// fields.addAll(Arrays.asList(c.getDeclaredFields()));
			c = c.getSuperclass();
		}
		Field[] result = new Field[fields.size()];
		return fields.toArray(result);
	}

	/**
	 * Gets all the runtime methods for a given class or interface (including
	 * the superclasses' or interfaces').
	 */
	public static Method[] getAllMethods(Class<?> c) {
		List<Method> methods = new ArrayList<Method>();
		if (c.isInterface()) {
			getAllIMethods(c, methods);
		} else {
			while (c != null && c != Object.class) {
				for (Method m : c.getDeclaredMethods())
					methods.add(m);
				// methods.addAll(Arrays.asList(c.getDeclaredMethods()));
				c = c.getSuperclass();
			}
		}
		Method[] result = new Method[methods.size()];
		return methods.toArray(result);
	}

	private static void getAllIMethods(Class<?> c, List<Method> methods) {
		for (Method m : c.getDeclaredMethods())
			methods.add(m);
		for (Class<?> i : c.getInterfaces()) {
			getAllIMethods(i, methods);
		}
	}

	/**
	 * Actually invokes from a compile-time invocation (by using runtime
	 * reflection).
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(CtInvocation<T> i) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Object target = i.getTarget() == null ? null : ((CtLiteral<?>) i
				.getTarget()).getValue();
		List<Object> args = new ArrayList<Object>();
		for (CtExpression e : i.getArguments()) {
			args.add(((CtLiteral<?>) e).getValue());
		}
		Class<?> c = i.getExecutable().getDeclaringType().getActualClass();
		ArrayList<Class<?>> argTypes = new ArrayList<Class<?>>();
		for (CtTypeReference<?> type : i.getExecutable().getParameterTypes()) {
			argTypes.add(type.getActualClass());
		}
		return (T) c.getMethod(i.getExecutable().getSimpleName(),
				argTypes.toArray(new Class[argTypes.size()])).invoke(target,
				args.toArray());
	}

	/**
	 * Return the set of modifiers defined by the modifiers integer
	 * (java.lang.reflect).
	 */
	public static Set<ModifierKind> getModifiers(int mod) {
		Set<ModifierKind> set = new TreeSet<ModifierKind>();
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

}
