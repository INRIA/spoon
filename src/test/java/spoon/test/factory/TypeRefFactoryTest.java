package spoon.test.factory;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.reference.CtTypeReference;

import static org.junit.Assert.assertEquals;

public class TypeRefFactoryTest {

    @Test
    public void testCreateTypeRef() {
        Launcher launcher = new Launcher();
        CtTypeReference<Object> ctTypeReference = launcher.getFactory().Code().createCtTypeReference(short.class);
        assertEquals("short", ctTypeReference.getSimpleName());
        assertEquals("short", ctTypeReference.getQualifiedName());

        ctTypeReference = launcher.getFactory().Code().createCtTypeReference(Object.class);
        assertEquals("Object", ctTypeReference.getSimpleName());
        assertEquals("java.lang.Object", ctTypeReference.getQualifiedName());

        ctTypeReference = launcher.getFactory().Code().createCtTypeReference(null);
        assertEquals(null, ctTypeReference);
    }
}
