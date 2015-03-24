package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Target({
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE
})
@Repeatable(AnnotationContainer.class)
public @interface AnnotationRepeated {
	String value();
}
