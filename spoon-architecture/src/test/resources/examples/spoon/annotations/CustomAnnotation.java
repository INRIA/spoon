package examples.spoon.annotations;

public @interface CustomAnnotation {
	String[] models() default "";
	int number() default 42;
}
