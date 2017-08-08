package spoon.test.position.testclasses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={})
@Retention(RetentionPolicy.RUNTIME)  
public abstract @interface FooAnnotation {
	String value();
}