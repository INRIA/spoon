package spoon.test.refactoring.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.TYPE_USE})
public @interface ExampleAnnotation {
	int someAnnotationMethod();
	String anotherAnnotationMethod() default "no";
}
