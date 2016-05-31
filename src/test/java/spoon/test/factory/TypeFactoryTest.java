package spoon.test.factory;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.factory.testclasses3.Cooking;
import spoon.test.factory.testclasses3.Prepare;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

	@Test
	public void testGetClassInAnInterface() throws Exception {
		final CtType<Cooking> cook = ModelUtils.buildClass(Cooking.class);

		assertNotNull(cook.getFactory().Type().get("spoon.test.factory.testclasses3.Cooking$Tacos"));
		assertNotNull(cook.getFactory().Class().get("spoon.test.factory.testclasses3.Cooking$Tacos"));
		assertNotNull(cook.getFactory().Type().get(Cooking.Tacos.class));
		assertNotNull(cook.getFactory().Class().get(Cooking.Tacos.class));

		final CtType<Prepare> prepare = ModelUtils.buildClass(Prepare.class);

		assertNotNull(prepare.getFactory().Type().get("spoon.test.factory.testclasses3.Prepare$Tacos"));
		assertNotNull(prepare.getFactory().Interface().get("spoon.test.factory.testclasses3.Prepare$Tacos"));
		assertNotNull(prepare.getFactory().Type().get(Prepare.Pozole.class));
		assertNotNull(prepare.getFactory().Interface().get(Prepare.Pozole.class));
	}
}
