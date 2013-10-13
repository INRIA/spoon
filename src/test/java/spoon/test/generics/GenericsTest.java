package spoon.test.generics;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeParameterReference;

public class GenericsTest {

	@Test 
	public void testModelBuildingTree() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.generics",  "Tree");
		assertEquals("Tree", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type.getFormalTypeParameters().get(0);
		assertEquals("V", generic.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable]", generic.getBounds().toString());
	}
}
