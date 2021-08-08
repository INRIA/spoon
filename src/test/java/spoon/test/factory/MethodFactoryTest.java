package spoon.test.factory;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.reference.CtExecutableReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class MethodFactoryTest {

    private static final String TEST_CLASS_STRING =
            "class TestClass { " +
                    "private void foo() { }" +
            "}";

    @Test
    public void testCreateReference() {
        // contract: createReference creates a method reference of the foo method

        CtClass<?> testClass = Launcher.parseClass(TEST_CLASS_STRING);
        CtMethod<?> foo = testClass.getMethodsByName("foo").get(0);
        CtExecutableReference<?> expectedReference = testClass.getMethod("foo").getReference();
        MethodFactory methodFactory = testClass.getFactory().Method();
        CtExecutableReference<?> actualCreatedReference = null;

        actualCreatedReference = methodFactory.createReference(foo);

        assertThat(actualCreatedReference, is(expectedReference));
    }
}
