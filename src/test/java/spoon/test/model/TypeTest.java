package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.util.Collection;

import org.junit.Test;

import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;

public class TypeTest {

	@Test
	public void test() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		assertEquals(1, type.getDeclaredFields().size());
		assertEquals(2, ((CtType) type).getMethods().size());
		assertEquals(3, type.getDeclaredExecutables().size());
		assertEquals(2, type.getAllFields().size());
		
		// we have 4  methods + one explicit constructor + two implicit constructors for Bar and Baz
		Collection<CtExecutableReference<?>> allExecutables = type.getAllExecutables();
		assertEquals(7, allExecutables.size());
	}
}
