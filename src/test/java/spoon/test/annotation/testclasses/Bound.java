package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
public @interface Bound {
	int max() default 10;
}
