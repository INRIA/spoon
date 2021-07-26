package spoon.test.ctElement;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;

import static org.junit.jupiter.api.Assertions.*;

public class CtElementImplTest {

    @Test
    void testRemoveAnnotation() {
        // contract: removeAnnotation returns true after removing an annotation of a class containing a single
        // annotation, and returns false when a non existing annotation is tried to be removed

        // arrange
        CtClass<?> annotatedClass = Launcher.parseClass("@SuppressWarnings(\"unchecked\") class Annotated { }");
        assertEquals(1, annotatedClass.getAnnotations().size());
        CtAnnotation<?> annotationToBeRemoved = annotatedClass.getAnnotations().get(0);

        // act
        boolean shouldBeTrue = annotatedClass.removeAnnotation(annotationToBeRemoved);
        boolean shouldBeFalse = annotatedClass.removeAnnotation(annotationToBeRemoved);

        // assert
        assertEquals(0, annotatedClass.getAnnotations().size());
        assertTrue(shouldBeTrue);
        assertFalse(shouldBeFalse);
    }

    @SuppressWarnings("unchecked")
    private static class Annotated { }
}
