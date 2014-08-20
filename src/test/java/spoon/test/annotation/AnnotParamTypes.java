package spoon.test.annotation;

public @interface AnnotParamTypes {

	int integer();
	int[] integers();

	String string();
	String[] strings();

	Class<?> clazz();
	Class<?>[] classes();
}
