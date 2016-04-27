package spoon.test.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

public class TypeFactoryTest {

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
    
    @Test
	public void reflectionAPI() throws Exception {
		// Spoon can be used as reflection API
		CtType s = new TypeFactory().get(String.class);
		assertEquals("String", s.getSimpleName());
		assertEquals("java.lang.String", s.getQualifiedName());
		assertEquals(3, s.getSuperInterfaces().size());
		assertEquals(2,s.getMethodsByName("toLowerCase").size());
	}
}
