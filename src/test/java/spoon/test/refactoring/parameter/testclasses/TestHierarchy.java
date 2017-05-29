package spoon.test.refactoring.parameter.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.LOCAL_VARIABLE})
public @interface TestHierarchy {
    /**
     * @return the list of hierarchy names where this method belongs to.
     */
    String[] value();
}
