/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This defines multiple utility methods for working with method handles. These methods are all calls to future jdk methods and maybe not available on jdk8.
 * All errors from these virtual calls are transformed to null or false results.
 */
@SuppressWarnings({"JavaLangInvokeHandleSignature", "ReturnOfNull"})
class MethodHandleUtils {
	private MethodHandleUtils() {
		// no instance
	}

	private static Class<?> recordComponent = lookupRecordComponentClass();
	private static MethodHandle isRecord = lookupRecord();
	private static MethodHandle lookupRecordComponents = lookupRecordComponents();
	private static MethodHandle lookupRecordComponentName = lookupRecordComponentName();
	private static MethodHandle lookupRecordComponentType = lookupRecordComponentType();

	private static MethodHandle lookupPermittedSubclasses = lookupPermittedSubclasses();

	/**
	 * Checks if the given class is a record.
	 * @param clazz  the class to check
	 * @return  true if the given class is a record, false otherwise.
	 */
	public static boolean isRecord(Class<?> clazz) {
		try {
			return (boolean) isRecord.invokeExact(clazz);
		} catch (Throwable e) {
			return false;
		}
	}


	/**
	 * Returns the class object of record component from the jdk if present.
	 * @return  the class object of record component from the jdk if present, null otherwise.
	 */
	private static @Nullable Class<?> lookupRecordComponentClass() {
		try {
			return Class.forName("java.lang.reflect.RecordComponent");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Gets the record components of the given class, if it has any.
	 * <p>
	 * This method is a call to the jdk method {@code java.lang.reflect.Record#getRecordComponents()}.
	 * Because we cant use the jdk method directly, we need to use reflection to get the method.
	 * @param clazz  the class to get the record components from.
	 * @return  the record components of the given class, if it has any, empty list otherwise.
	 */
	public static List<AnnotatedElement> getRecordComponents(Class<?> clazz) {
		if (recordComponent == null) {
			return Collections.emptyList();
		}
		AnnotatedElement[] components = null;
		try {
			components = (AnnotatedElement[]) lookupRecordComponents.invoke(clazz);
		} catch (Throwable e) {
			return Collections.emptyList();
		}
		return Arrays.asList(components);
	}
	/**
	 * Gets the name of the given record component. The given class <b>must</b> be a record component.
	 * <p>
	 * This method is a call to the jdk method {@code java.lang.reflect.RecordComponent#getName()}.
	 * Because we cant use the jdk method directly, we need to use reflection to get the method.
	 * We cant use the record component type directly so we use the upper type annotated element here. Passing any other element will return null.
	 *
	 * @param component  the record component to get the name from.
	 * @return  the name of the given record component, null otherwise.
	 */
	public static @Nullable String getRecordComponentName(AnnotatedElement component) {
		try {
			return (String) lookupRecordComponentName.invoke(component);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * Gets the type of the given record component. The given class <b>must</b> be a record component.
	 * <p>
	 * This method is a call to the jdk method {@code java.lang.reflect.RecordComponent#getGenericType()}.
	 * Because we cant use the jdk method directly, we need to use reflection to get the method.
	 * We cant use the record component type directly so we use the upper type annotated element here. Passing any other element will return null.
	 * @param component
	 * @return
	 */
	public static @Nullable Type getRecordComponentType(AnnotatedElement component) {
		try {
			return (Type) lookupRecordComponentType.invoke(component);
		} catch (Throwable e) {
			return null;
		}
	}

	public static @Nullable Class<?>[] getPermittedSubclasses(Class<?> clazz) {
		try {
			return (Class<?>[]) lookupPermittedSubclasses.invoke(clazz);
		} catch (Throwable e) {
			return null;
		}
	}


	private static @Nullable MethodHandle lookupRecord() {
		try {
			return MethodHandles.lookup().findVirtual(Class.class, "isRecord", MethodType.methodType(boolean.class));
		} catch (Throwable e) {
			return null;
		}
	}
	private static @Nullable MethodHandle lookupRecordComponents() {
		try {
			MethodType arrayOfRecordComponentType = MethodType.methodType(Array.newInstance(recordComponent, 0).getClass());
			return MethodHandles.lookup().findVirtual(Class.class, "getRecordComponents", arrayOfRecordComponentType);
		} catch (Throwable e) {
			return null;
		}
	}
	private static @Nullable MethodHandle lookupRecordComponentType() {
		try {
			return MethodHandles.lookup().findVirtual(recordComponent, "getGenericType", MethodType.methodType(Type.class));
		} catch (Throwable e) {
			return null;
		}
	}

	private static @Nullable MethodHandle lookupRecordComponentName() {
		try {
			return MethodHandles.lookup().findVirtual(recordComponent, "getName", MethodType.methodType(String.class));
		} catch (Throwable e) {
			return null;
		}
	}

	private static @Nullable MethodHandle lookupPermittedSubclasses() {
		try {
			return MethodHandles.lookup().findVirtual(Class.class, "getPermittedSubclasses", MethodType.methodType(Class[].class));
		} catch (Throwable e) {
			return null;
		}
	}
}
