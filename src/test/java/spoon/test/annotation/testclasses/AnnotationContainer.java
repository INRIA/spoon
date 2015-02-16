package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({
		ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE
})
public @interface AnnotationContainer {
	AnnotationRepeated[] value();
}
