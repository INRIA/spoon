package spoon.test.arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtArrayTypeReference;

public class ArraysTest {

	@Test
	public void testArrayReferences() throws Exception {
		CtType<?> type = build("spoon.test.arrays", "ArrayClass");
		assertEquals("ArrayClass", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().toString());
        assertEquals(3,((CtArrayTypeReference<?>)type.getField("i").getType()).getDimensionCount());

        CtField<?> x = type.getField("x");
        assertTrue(x.getType() instanceof CtArrayTypeReference);
        assertEquals("Array",x.getType().getSimpleName());
        assertEquals("java.lang.reflect.Array",x.getType().getQualifiedName());
        assertEquals("int",((CtArrayTypeReference<?>)x.getType()).getComponentType().getSimpleName());
        assertTrue(((CtArrayTypeReference<?>)x.getType()).getComponentType().getActualClass().equals(int.class));
    }

}
