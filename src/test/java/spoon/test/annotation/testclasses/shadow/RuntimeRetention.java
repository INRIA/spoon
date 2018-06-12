package spoon.test.annotation.testclasses.shadow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface RuntimeRetention {
    String[] role();
}
