package spoon.test.annotation.testclasses.spring;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Code get from: https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/annotation/AliasFor.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {

    /**
     * Alias for {@link #attribute}.
     * <p>Intended to be used instead of {@link #attribute} when {@link #annotation}
     * is not declared &mdash; for example: {@code @AliasFor("value")} instead of
     * {@code @AliasFor(attribute = "value")}.
     */
    @AliasFor("attribute")
    String value() default "";

    /**
     * The name of the attribute that <em>this</em> attribute is an alias for.
     * @see #value
     */
    @AliasFor("value")
    String attribute() default "";

    /**
     * The type of annotation in which the aliased {@link #attribute} is declared.
     * <p>Defaults to {@link Annotation}, implying that the aliased attribute is
     * declared in the same annotation as <em>this</em> attribute.
     */
    Class<? extends Annotation> annotation() default Annotation.class;

}
