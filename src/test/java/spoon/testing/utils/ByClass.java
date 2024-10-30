package spoon.testing.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a parameter of a test method is annotated with this annotation,
 * and the parameter type is {@link spoon.reflect.declaration.CtType} or a subtype,
 * the parameter will be filled with the type with the fully qualified name of the class
 * given by {@link #value()}.
 * <p>
 * If no matching type exists, the test will fail with a
 * {@link org.junit.jupiter.api.extension.ParameterResolutionException}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ByClass {

	Class<?> value();
}
