package spoon.test.annotation.testclasses.repeatable;

import java.lang.annotation.Repeatable;

@Repeatable(Tags.class)
public @interface Tag {
    String value() default "";
}
