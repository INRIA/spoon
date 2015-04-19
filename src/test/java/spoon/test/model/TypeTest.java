package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.util.Collection;

import org.junit.Test;

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;

public class TypeTest {

	@Test
	public void testGetAllExecutables() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		assertEquals(1, type.getDeclaredFields().size());
		assertEquals(3, type.getMethods().size());
		assertEquals(4, type.getDeclaredExecutables().size());
		assertEquals(2, type.getAllFields().size());
		
		// we have 4  methods + one explicit constructor + two implicit
		// constructors for Bar, Baz and Baz.Inner
		Collection<CtExecutableReference<?>> allExecutables = type.getAllExecutables();
		assertEquals(8, allExecutables.size());
	}

	@Test
	public void testGetUsedTypes() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		assertEquals(3, type.getUsedTypes(true).size());
		assertEquals(0, type.getUsedTypes(false).size());
	}
}
