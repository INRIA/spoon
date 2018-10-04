package spoon.test.annotation.testclasses.typeandfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
public @interface AnnotTypeAndField {
}
