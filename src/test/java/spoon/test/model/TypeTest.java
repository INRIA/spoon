package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

public class TypeTest {

	@Test
	public void testGetAllExecutables() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		assertEquals(1, type.getDeclaredFields().size());
		assertEquals(3, type.getMethods().size());
		assertEquals(4, type.getDeclaredExecutables().size());
		assertEquals(2, type.getAllFields().size());
		
		// we have 4  methods + one explicit constructor + 3 implicit
		// constructors for Bar, Baz and Baz.Inner
		Collection<CtExecutableReference<?>> allExecutables = type.getAllExecutables();
		assertEquals(8, allExecutables.size());
	}

	@Test
	public void testGetUsedTypes() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		TypeFactory tf = type.getFactory().Type();
		
		Set<CtTypeReference<?>> usedTypes = type.getUsedTypes(true);
		assertEquals(3, usedTypes.size());
		assertTrue(usedTypes.contains(tf.createReference(Bar.class)));
		assertTrue(usedTypes.contains(tf.createReference(Baz.class)));
		assertTrue(usedTypes.contains(tf.createReference(Baz.Inner.class)));
		
		assertEquals(0, type.getUsedTypes(false).size());
	}

	@Test
	public void superclassTest() throws Exception {
		CtType<?> type = build("spoon.test.model", "InterfaceSuperclass");

		Set<CtTypeReference<?>> interfaces = type.getSuperInterfaces();
		assertEquals(1, interfaces.size());

		CtTypeReference<?> inface = interfaces.iterator().next();
		assertNull(inface.getSuperclass());
	}
}
