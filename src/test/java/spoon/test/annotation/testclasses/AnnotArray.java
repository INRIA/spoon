package spoon.test.annotation.testclasses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface AnnotArray {
	public Class<?>[] value() default { };
}
