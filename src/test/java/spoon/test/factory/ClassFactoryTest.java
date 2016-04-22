package spoon.test.factory;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.createFactory;

public class ClassFactoryTest {
	@Test
	public void testDeclaringClass() throws Exception {
		final Factory factory = createFactory();
		final CtClass<Object> declaringClass = factory.Core().createClass();
		declaringClass.setSimpleName("DeclaringClass");

		final CtClass<Object> inner = factory.Class().create(declaringClass, "Inner");

		assertEquals("Inner", inner.getSimpleName());
		assertEquals(declaringClass, inner.getDeclaringType());
	}
}
