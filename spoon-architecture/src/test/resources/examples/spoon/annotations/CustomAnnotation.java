package examples.spoon.annotations;

/**
 * This defines a annotation with numbers for index and models as string[].
 */
public @interface CustomAnnotation {
	String[] models() default "";
	int number() default 42;
}
