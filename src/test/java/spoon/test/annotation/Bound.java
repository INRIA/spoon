package spoon.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is used to annotate our stack class to make it bounded.
 */
@Target(ElementType.TYPE)
public @interface Bound {
    int max() default 10;
}
