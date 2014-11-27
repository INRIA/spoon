package spoon.test.annotation.testclasses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotArrayInnerClass {
	@Retention(value = RetentionPolicy.RUNTIME)
	public @interface Annotation {
		public Class<?>[] value() default { };
	}
}