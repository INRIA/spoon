package spoon.testing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This meta-annotation marks test cases which should have a spoon model built for them.
 * When a test is marked with this annotation, any {@link spoon.Launcher}, {@link spoon.reflect.factory.Factory} and
 * {@link spoon.reflect.CtModel} parameters will cause a model to be built and are automatically injected.
 * This allows you to write code like
 *
 * <pre>{@code
 * @ModelTest(value = {"src/test/resources/foo"}, autoImport = true)
 * public void foo(Factory factory, CtModel model, Launcher launcher) {
 *     // do sth with them :)
 * }
 * }</pre>
 * <p>
 * The order of parameters does not matter and all of them can be elided. You only need to declare what you need.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(ModelTestParameterResolver.class)
@Test
public @interface ModelTest {

	/**
	 * @return the input resources passed to {@link spoon.Launcher#addInputResource(String)}
	 */
	String[] value();

	boolean noClasspath() default true;

	boolean commentsEnabled() default true;

	boolean autoImport() default false;

	int complianceLevel() default -1;
}
