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
    public void testRemoveAnnotation() {
        // contract: removeAnnotation returns true after removing an annotation of a class containing a single
        // annotation, and returns false when a non existing annotation is tried to be removed

        // arrange
        Launcher spoon = new Launcher();
        spoon.addInputResource(new FileSystemFile("./src/test/java/spoon/test/ctElement/CtElementImplTest.java"));
        spoon.buildModel();
        Factory factory = spoon.getFactory();

        CtClass<Annotated> annotatedClass = factory.Class().get(Annotated.class);
        assertEquals(1, annotatedClass.getAnnotations().size());
        CtAnnotation annotationToBeRemoved = annotatedClass.getAnnotations().get(0);

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