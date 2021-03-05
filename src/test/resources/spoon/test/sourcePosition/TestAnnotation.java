package spoon.test.sourcePosition;

// resource for https://github.com/INRIA/spoon/issues/3606
public @interface TestAnnotation {
    String value() default "";
}
