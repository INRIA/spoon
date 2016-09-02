package spoon.reflect.declaration;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Marcel Steinbeck
 */
public class UnknownDeclarationTest {

    private static class ExecutableReferenceVisitor extends CtScanner {

        int referenceCounter = 0;

        @Override
        public <T> void visitCtExecutableReference(final CtExecutableReference<T> reference) {
            // even for the multi dimensional array a valid CtTypeReference must be available
            final CtTypeReference typeReference = reference.getDeclaringType();
            assertNotNull(typeReference);
            // we only look for calls to 'UnknownClass'
            if (typeReference.getSimpleName().equals("UnknownClass")) {
                // the actual executable should not be available as the source of UnknownClass
                // is missing
                final CtExecutable executable = reference.getDeclaration();
                assertNull(executable);
                referenceCounter++;
            }
        }
    }

    @Test
    public void testUnknownCalls() {
        final Launcher runLaunch = new Launcher();
        runLaunch.getEnvironment().setNoClasspath(true);
        runLaunch.addInputResource("./src/test/resources/noclasspath/UnknownCalls.java");
        runLaunch.buildModel();

        final CtPackage rootPackage = runLaunch.getFactory().Package().getRootPackage();
        final ExecutableReferenceVisitor visitor = new ExecutableReferenceVisitor();
        visitor.scan(rootPackage);
        // UnknownClass (single dimension) constructor +
        // UnknownClass (single dimension) method +
        // UnknownClass (multi dimension) constructor +
        // UnknownClass (multi dimension) method
        assertEquals(4, visitor.referenceCounter);
    }
}
