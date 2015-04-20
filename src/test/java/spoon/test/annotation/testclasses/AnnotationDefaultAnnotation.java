package spoon.test.annotation.testclasses;

public @interface AnnotationDefaultAnnotation {
	InnerAnnot inner() default @InnerAnnot("");
}
