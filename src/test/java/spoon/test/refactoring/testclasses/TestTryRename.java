package spoon.test.refactoring.testclasses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.FIELD})
public @interface TestTryRename {
    /**
     * @return the list of names which should be tried by refactoring.
     * If the name starts with prefix "-", then this refactoring should fail on some validation issue
     */
    String[] value();
}
