package spoon.test.enums;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtEnum;

public class EnumsTest {

	@Test 
	public void testModelBuildingEnum() throws Exception {
		CtEnum<Regular> enumeration = build ("spoon.test.enums",  "Regular");
		assertEquals("Regular", enumeration.getSimpleName());
		assertEquals(3, Regular.values().length);
		assertEquals(3, enumeration.getValues().size());
		assertEquals("A", enumeration.getValues().get(0).getSimpleName());
		assertEquals(5, enumeration.getFields().size());
	}
}
