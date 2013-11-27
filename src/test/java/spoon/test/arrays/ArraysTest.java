package spoon.test.arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtArrayTypeReference;

public class ArraysTest {

	@Test
	public void testArrayReferences() throws Exception {
		CtSimpleType<?> type = build("spoon.test.arrays", "ArrayClass");
		assertEquals("ArrayClass", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().toString());
        assertEquals(3,((CtArrayTypeReference)type.getField("i").getType()).getDimensionCount());

        CtField<?> x = type.getField("x");
        assertTrue(x.getType() instanceof CtArrayTypeReference);
        assertTrue(((CtArrayTypeReference)x.getType()).getComponentType().getActualClass().equals(int.class));
    }

}
