package spoon.test.model;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

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

	@Test
	public void testGetUsedTypesForTypeInRootPackage() throws Exception {
		CtClass<?> cl = createFactory().Code().createCodeSnippetStatement("class X { X x; }").compile();
		assertEquals(0, cl.getUsedTypes(false).size());
	}

	@Test
	public void testGetDeclaredOrIheritedFieldOnType() throws Exception {
		CtType<?> type = build("spoon.test.model", "ClassWithSuperAndIFace");

		assertEquals("classField", type.getDeclaredOrInheritedField("classField").getSimpleName());
		assertEquals("i", type.getDeclaredOrInheritedField("i").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("fooMethod"));
		assertEquals("j", type.getDeclaredOrInheritedField("j").getSimpleName());
		assertEquals("IFACE_FIELD_1", type.getDeclaredOrInheritedField("IFACE_FIELD_1").getSimpleName());
		assertEquals("IFACE_FIELD_2", type.getDeclaredOrInheritedField("IFACE_FIELD_2").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("notExists"));
	}

	@Test
	public void testGetDeclaredOrIheritedFieldOnTypeRef() throws Exception {
		CtTypeReference<?> type = build("spoon.test.model", "ClassWithSuperAndIFace").getReference();

		assertEquals("classField", type.getDeclaredOrInheritedField("classField").getSimpleName());
		assertEquals("i", type.getDeclaredOrInheritedField("i").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("fooMethod"));
		assertEquals("j", type.getDeclaredOrInheritedField("j").getSimpleName());
		assertEquals("IFACE_FIELD_1", type.getDeclaredOrInheritedField("IFACE_FIELD_1").getSimpleName());
		assertEquals("IFACE_FIELD_2", type.getDeclaredOrInheritedField("IFACE_FIELD_2").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("notExists"));
	}
	@Test

	public void testGetDeclaredOrIheritedFieldByReflection() throws Exception {
		CtTypeReference<?> type = build("spoon.test.model", "ClassWithSuperOutOfModel").getReference();

		assertEquals("buf", type.getDeclaredOrInheritedField("buf").getSimpleName());
		assertEquals("count", type.getDeclaredOrInheritedField("count").getSimpleName());
		
	}
}
