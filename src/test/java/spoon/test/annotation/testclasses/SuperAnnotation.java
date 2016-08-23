package spoon.test.annotation.testclasses;

public @interface SuperAnnotation {
	String value = "";

	String value() default value;

	String value1();
}
