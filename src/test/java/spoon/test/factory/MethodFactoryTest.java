package spoon.test.factory;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.factory.testclasses4.Bar;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class MethodFactoryTest {

    @Test
    public void testCreateReference() {
        // contract: createReference creates a method reference of the foo method

        Factory factory = new Launcher().getFactory();
        CtClass<?> testClass = factory.Class().get(Bar.class);
        CtMethod<?> foo = testClass.getMethodsByName("foo").get(0);
        CtExecutableReference<?> expectedReference = testClass.getMethod("foo").getReference();
        MethodFactory methodFactory = testClass.getFactory().Method();
        CtExecutableReference<?> actualCreatedReference = null;

        actualCreatedReference = methodFactory.createReference(foo);

        assertThat(actualCreatedReference, is(expectedReference));
    }

    @Test
    public void testCreateReferenceWithActualMethod() throws ClassNotFoundException, NoSuchMethodException {
        // contract: createReference creates a method reference of a actual method foo

        // arrange
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        Class<?> testClass = Class.forName("spoon.test.factory.testclasses4.Bar");
        Method testMethod = testClass.getMethod("foo");

        CtExecutableReference<Void> expectedReference = factory.createExecutableReference();
        expectedReference.setSimpleName("foo");
        CtTypeReference<?> ctTypeReference = factory.Type().createReference(Bar.class);
        expectedReference.setDeclaringType(ctTypeReference);
        expectedReference.setType(launcher.getFactory().Type().voidPrimitiveType());

        MethodFactory methodFactory = factory.Method();
        CtExecutableReference<?> actualCreatedReference = null;

        // act
        actualCreatedReference = methodFactory.createReference(testMethod);

        // assert
        assertThat(actualCreatedReference, is(expectedReference));
    }
}