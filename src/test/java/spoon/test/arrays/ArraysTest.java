package spoon.test.arrays;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtSimpleType;

public class ArraysTest {

	@Test 
	public void testModelBuildingArrays() throws Exception {
		CtSimpleType type = build ("spoon.test.arrays",  "ArrayClass");
		assertEquals("ArrayClass", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().toString());
	}

}
