package spoon.test.annotation.testclasses;

public @interface Inception {
	InnerAnnot value();
	InnerAnnot[] values() default {};
}
