package spoon.support.visitor.java;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MethodHandleUtils {
	private MethodHandleUtils() {
		// no instance
	}

	private static Class<?> recordComponent = lookupRecordComponentClass();
	private static MethodHandle isRecord = lookupRecord();
	private static MethodHandle lookupRecordComponents = lookupRecordComponents();
	private static MethodHandle lookupRecordComponentName = lookupRecordComponentName();
	private static MethodHandle lookupRecordComponentType = lookupRecordComponentType();

	public static boolean isRecord(Class<?> clazz) {
		try {
			return (boolean) isRecord.invokeExact(clazz);
		} catch (Throwable e) {
			return false;
		}
	}



	private static Class<?> lookupRecordComponentClass() {
		try {
			return Class.forName("java.lang.reflect.RecordComponent");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

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
	public static String getRecordComponentName(AnnotatedElement component) {
		try {
			return (String) lookupRecordComponentName.invoke(component);
		} catch (Throwable e) {
			return null;
		}
	}

	public static Type getRecordComponentType(AnnotatedElement component) {
		try {
			return (Type) lookupRecordComponentType.invoke(component);
		} catch (Throwable e) {
			return null;
		}
	}

	private static MethodHandle lookupRecord() {
		try {
			return MethodHandles.lookup().findVirtual(Class.class, "isRecord", MethodType.methodType(boolean.class));
		} catch (Throwable e) {
			return null;
		}
	}
	private static MethodHandle lookupRecordComponents() {
		try {
			MethodType arrayOfRecordComponentType = MethodType.methodType(Array.newInstance(recordComponent, 0).getClass());
			return MethodHandles.lookup().findVirtual(Class.class, "getRecordComponents", arrayOfRecordComponentType);
		} catch (Throwable e) {
			return null;
		}
	}
	private static MethodHandle lookupRecordComponentType() {
		try {
			return MethodHandles.lookup().findVirtual(recordComponent, "getGenericType", MethodType.methodType(Type.class));
		} catch (Throwable e) {
			return null;
		}
	}

	private static MethodHandle lookupRecordComponentName() {
		try {
			return MethodHandles.lookup().findVirtual(recordComponent, "getName", MethodType.methodType(String.class));
		} catch (Throwable e) {
			return null;
		}
	}
}
