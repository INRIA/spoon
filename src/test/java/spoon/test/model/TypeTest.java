/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.model;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.ModelBuildingException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class TypeTest {

	@Test
	public void testGetDifferentElements() throws Exception {
		// contract: All elements of a model must be found.
		CtClass<?> type = build("spoon.test.model", "Foo");
		assertAll(
				() -> assertEquals(1, type.getDeclaredFields().size()),
				() -> assertEquals(3, type.getMethods().size()),
				() -> assertEquals(4, type.getDeclaredExecutables().size()),
				() -> assertEquals(2, type.getAllFields().size()),
				() -> assertEquals(1, type.getConstructors().size()),
				() -> assertTrue(16 == type.getAllMethods().size() || 15 == type.getAllMethods().size()),
				() -> assertTrue(12 == type.getFactory().Type().get(Object.class).getAllMethods().size()
													|| 11 == type.getFactory().Type().get(Object.class).getAllMethods().size()));

		// we have 3  methods in Foo + 2 in Baz - 1 common in Foo.bar (m) + 12 in Object + 1 explicit constructor in Foo
		/*
		This assertion is needed because in java.lang.object the method registerNative was removed.
		See https://bugs.openjdk.java.net/browse/JDK-8232801 for details.
		To fit this change and support new jdks and the CI both values are correct.
		In jdk8 object has 12 methods and in newer jdk object has 11
		 */
		assertTrue(17 == type.getAllExecutables().size() || 16 == type.getAllExecutables().size());
	}

	@Test
	public void testAllTypeMembersFunctionMode() throws Exception {
		// contract: AllTypeMembersFunction can be configured to return all members or only internally visible members
		CtClass<?> type = build("spoon.test.model", "Foo");
		List<CtMethod<?>> internallyAccessibleMethods = type.map(new AllTypeMembersFunction(CtMethod.class).setMode(AllTypeMembersFunction.Mode.SKIP_PRIVATE)).list();
		List<CtMethod<?>> allMethods = type.map(new AllTypeMembersFunction(CtMethod.class)).list();
		assertAll(
			() -> assertEquals(16, internallyAccessibleMethods.size()),
			() -> assertTrue(17 == allMethods.size() || 16 == allMethods.size()));

		/*
		This if-clause is needed because in java.lang.object the method registerNative was removed.
		See https://bugs.openjdk.java.net/browse/JDK-8232801 for details.
		*/
		allMethods.removeAll(internallyAccessibleMethods);
		if (allMethods.size() == 1) {
			assertEquals(1, allMethods.size());
			assertEquals("registerNatives()", allMethods.get(0).getSignature());
		}
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
		CtType<?> type = build("spoon.test.model.testclasses", "InterfaceSuperclass");

		Set<CtTypeReference<?>> interfaces = type.getSuperInterfaces();
		assertEquals(1, interfaces.size());

		CtTypeReference<?> inface = interfaces.iterator().next();
		assertNull(inface.getSuperclass());
	}

	@Test
	public void testGetUsedTypesForTypeInRootPackage() {
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
		CtTypeReference<?> type = build("spoon.test.model.testclasses", "ClassWithSuperOutOfModel").getReference();

		assertEquals("buf", type.getDeclaredOrInheritedField("buf").getSimpleName());
		assertEquals("count", type.getDeclaredOrInheritedField("count").getSimpleName());
		
	}

	@Test
	public void testTypeInfoIsInterface() throws Exception {
		//contract: isInterface returns true only for interfaces
		CtType<?> clazz = build("spoon.test.model.testclasses", "ClassWithSuperOutOfModel");
		checkIsSomething("class", clazz);
		CtType<?> type = build("spoon.test.model.testclasses", "InterfaceWrithFields");
		checkIsSomething("interface", type);
		checkIsSomething("enum", type.getFactory().Enum().create(type.getPackage(), "someEnum"));
		CtType<?> ctAnnotation = type.getFactory().Annotation().create(type.getPackage(), "someAnnotation");
		checkIsSomething("annotation", ctAnnotation);
		CtTypeParameter ctTypeParam = type.getFactory().Core().createTypeParameter();
		ctTypeParam.setSimpleName("T");
		clazz.addFormalCtTypeParameter(ctTypeParam);
		checkIsSomething("generics", ctTypeParam);
	}

	@Test(expected = ModelBuildingException.class)
	public void testMultiClassNotEnable() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/resources/multiclass/module1");
		spoon.addInputResource("src/test/resources/multiclass/module2");
		spoon.buildModel();
	}

	@Test
	public void testMultiClassEnable() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/resources/multiclass/module1");
		spoon.addInputResource("src/test/resources/multiclass/module2");
		spoon.getEnvironment().setIgnoreDuplicateDeclarations(true);
		spoon.buildModel();
		assertNotNull(spoon.getFactory().Class().get("A"));
	}
	
	private void checkIsSomething(String expectedType, CtType type) {
		_checkIsSomething(expectedType, type);
		_checkIsSomething(expectedType, type.getReference());
	}
	private void _checkIsSomething(String expectedType, CtTypeInformation type) {
		assertEquals("interface".equals(expectedType), type.isInterface());
		assertEquals("class".equals(expectedType), type.isClass());
		assertEquals("annotation".equals(expectedType), type.isAnnotationType());
		assertEquals("anonymous".equals(expectedType), type.isAnonymous());
		assertEquals("enum".equals(expectedType), type.isEnum());
		assertEquals("generics".equals(expectedType), type.isGenerics());
	}
}
