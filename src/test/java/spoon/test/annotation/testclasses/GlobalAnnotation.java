package spoon.test.annotation.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({
		ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
		ElementType.TYPE_PARAMETER, ElementType.CONSTRUCTOR, ElementType.FIELD,
		ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE,
		ElementType.PARAMETER
})
public @interface GlobalAnnotation {
}
