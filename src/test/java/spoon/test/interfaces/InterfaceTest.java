/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.interfaces;

import spoon.test.SpoonTestHelpers;
import spoon.test.interfaces.testclasses.RedefinesStaticMethodInterface;
import spoon.SpoonModelBuilder;
import spoon.reflect.factory.Factory;
import spoon.test.interfaces.testclasses.RedefinesDefaultMethodInterface;
import spoon.reflect.code.CtStatement;
import spoon.test.interfaces.testclasses.InterfaceWithDefaultMethods;
import spoon.reflect.declaration.CtInterface;
import spoon.test.interfaces.testclasses.ExtendsStaticMethodInterface;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import spoon.support.reflect.CtExtendedModifier;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.CtType;
import spoon.reflect.code.CtBlock;
import spoon.test.interfaces.testclasses.ExtendsDefaultMethodInterface;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.testing.utils.ModelTest;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.Collection;
import java.io.File;

import static spoon.test.SpoonTestHelpers.contentEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class InterfaceTest {

	private Factory factory;

	@BeforeEach
	public void setUp() {
		final File testDirectory = new File("./src/test/java/spoon/test/interfaces/testclasses/");

		final Launcher launcher = new Launcher();

		this.factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(8);
		SpoonModelBuilder compiler = launcher.createCompiler(this.factory);

		compiler.addInputSource(testDirectory);
		compiler.build();
	}

	@Test
	public void testDefaultMethodInInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(InterfaceWithDefaultMethods.class);

		final CtMethod<?> ctMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertTrue(ctMethod.isDefaultMethod(), "The method in the interface must to be default");

		// contract: the toString does not show the implicit modifiers (here "public")
		final String expected =
				"default java.time.ZonedDateTime getZonedDateTime(java.lang.String zoneString) {"
						+ System.lineSeparator()
						+ "    return java.time.ZonedDateTime.of(getLocalDateTime(), spoon.test.interfaces.testclasses.InterfaceWithDefaultMethods.getZoneId(zoneString));"
						+ System.lineSeparator() + "}";
		assertEquals(expected, ctMethod.toString(), "The default method must to be well printed");
	}

	@Test
	public void testDefaultMethodInConsumer() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(Consumer.class);

		final CtMethod<?> ctMethod = ctInterface.getMethodsByName("andThen").get(0);
		assertTrue(ctMethod.isDefaultMethod(), "The method in the interface must to be default");
	}

	@Test
	public void testExtendsDefaultMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(ExtendsDefaultMethodInterface.class);

		assertEquals(1, ctInterface.getMethods().size(), "Sub interface must have only one method in its interface");
		assertEquals(6, ctInterface.getAllMethods().size(), "Sub interface must have 6 methods in its interface and its super interfaces");

		final CtMethod<?> getZonedDateTimeMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertTrue(getZonedDateTimeMethod.isDefaultMethod(), "Method in the sub interface must be a default method");
		assertSame(ExtendsDefaultMethodInterface.class, getZonedDateTimeMethod.getDeclaringType().getActualClass(), "Interface of the default method must be the sub interface");
	}

	@Test
	public void testRedefinesDefaultMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(RedefinesDefaultMethodInterface.class);

		assertEquals(1, ctInterface.getMethods().size(), "Sub interface must have only one method in its interface");
		assertEquals(6, ctInterface.getAllMethods().size(), "Sub interface must have 6 methods in its interface and its super interfaces");

		final CtMethod<?> getZonedDateTimeMethod = ctInterface.getMethodsByName("getZonedDateTime").get(0);
		assertFalse(getZonedDateTimeMethod.isDefaultMethod(), "Method in the sub interface mustn't be a default method");
		assertSame(RedefinesDefaultMethodInterface.class, getZonedDateTimeMethod.getDeclaringType().getActualClass(), "Interface of the default method must be the sub interface");
	}

	@Test
	public void testExtendsStaticMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(ExtendsStaticMethodInterface.class);

		assertEquals(1, ctInterface.getMethods().size(), "Sub interface must have only one method in its interface");
		assertEquals(6, ctInterface.getAllMethods().size(), "Sub interface must have 6 methods in its interface and its super interfaces");

		final CtMethod<?> getZoneIdMethod = ctInterface.getMethodsByName("getZoneId").get(0);
		assertTrue(getZoneIdMethod.getModifiers().contains(ModifierKind.STATIC), "Method in the sub interface must be a static method");
		assertSame(ExtendsStaticMethodInterface.class, getZoneIdMethod.getDeclaringType().getActualClass(), "Interface of the static method must be the sub interface");
	}

	@Test
	public void testRedefinesStaticMethodInSubInterface() {
		final CtInterface<?> ctInterface = (CtInterface<?>) factory.Type().get(RedefinesStaticMethodInterface.class);

		assertEquals(1, ctInterface.getMethods().size(), "Sub interface must have only one method in its interface");
		assertEquals(6, ctInterface.getAllMethods().size(), "Sub interface must have 6+12(from java.lang.Object) methods in its interface and its super interfaces");

		final CtMethod<?> getZoneIdMethod = ctInterface.getMethodsByName("getZoneId").get(0);
		assertFalse(getZoneIdMethod.getModifiers().contains(ModifierKind.STATIC), "Method in the sub interface mustn't be a static method");
		assertSame(RedefinesStaticMethodInterface.class, getZoneIdMethod.getDeclaringType().getActualClass(), "Interface of the static method must be the sub interface");
	}

	@org.junit.jupiter.api.Test
	void testLocalInterfaceExists() {
		// contract: local interfaces and their members are part of the model
		String code = SpoonTestHelpers.wrapLocal(
				"		interface MyInterface {\n" +
						"			static final int A = 1;\n" +
						"			void doNothing();\n" +
						"		}\n"
		);
		CtModel model = SpoonTestHelpers.createModelFromString(code, 16);
		CtBlock<?> block = SpoonTestHelpers.getBlock(model);

		assertThat("The local interface does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		Assertions.assertTrue(statement instanceof CtInterface<?>);
		CtInterface<?> interfaceType = (CtInterface<?>) statement;

		assertThat(interfaceType.isLocalType(), is(true));
		assertThat(interfaceType.getSimpleName(), is("1MyInterface"));
		assertThat(interfaceType.getFields().size(), is(1));
		assertThat(interfaceType.getMethods().size(), is(1));
		MatcherAssert.assertThat(interfaceType.getExtendedModifiers(), contentEquals(
				CtExtendedModifier.implicit(ModifierKind.STATIC),
				CtExtendedModifier.implicit(ModifierKind.ABSTRACT)
		));
	}

	@org.junit.jupiter.api.Test
	void testPackageLevelInterfaceModifiers() throws Exception {
		// contract: a simple interface has the correct modifiers applied
		// see https://docs.oracle.com/javase/specs/jls/se17/html/jls-9.html#jls-9.1.1
		CtType<?> emptyInterface = build("spoon.test.interfaces.testclasses", "EmptyInterface");
		assertThat(emptyInterface.getExtendedModifiers(), contentEquals(
				CtExtendedModifier.implicit(ModifierKind.ABSTRACT),
				CtExtendedModifier.explicit(ModifierKind.PUBLIC)
		));
	}

	@ModelTest("src/test/resources/nestedInInterface")
	public void testNestedTypesInInterfaceArePublic(CtModel model) {
		// contract: nested types in interfaces are implicitly public
		// (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)
		Collection<CtType<?>> types = model.getAllTypes()
				.stream()
				.flatMap(it -> it.getNestedTypes().stream())
				.collect(Collectors.toList());

		assertEquals(4, types.size());

		for (CtType<?> type : types) {
			assertTrue(type.isPublic(), "Nested type " + type.getQualifiedName() + " is not public");
			CtExtendedModifier modifier = type.getExtendedModifiers()
					.stream()
					.filter(it -> it.getKind() == ModifierKind.PUBLIC)
					.findFirst()
					.get();
			assertTrue(modifier.isImplicit(), "nested type " + type.getQualifiedName() + " has explicit modifier"
			);
		}
	}

	@ModelTest("src/test/resources/nestedInInterface")
	public void testNestedTypesInInterfaceAreStatic(CtModel model) {
		// contract: nested types in interfaces are implicitly static
		// (https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.5)
		Collection<CtType<?>> types = model.getAllTypes()
				.stream()
				.flatMap(it -> it.getNestedTypes().stream())
				.collect(Collectors.toList());

		assertEquals(4, types.size());

		for (CtType<?> type : types) {
			assertTrue(type.isStatic(), "Nested type " + type.getQualifiedName() + " is not static");
			CtExtendedModifier modifier = type.getExtendedModifiers()
					.stream()
					.filter(it -> it.getKind() == ModifierKind.STATIC)
					.findFirst()
					.get();
			assertTrue(modifier.isImplicit(), "nested type " + type.getQualifiedName() + " has explicit modifier"
			);
		}
	}

	@Test
	public void testImplicitPublicModifierInNestedInterfaceTypeIsRemoved() {
		// contract: implicit public modifier for nested types is deleted when they are removed from the interface
		Factory factory = createFactory();
		CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
		CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
		ctInterface.addNestedType(nestedClass);

		assertTrue(nestedClass.isPublic(), "Class wasn't made public");
		ctInterface.removeNestedType(nestedClass);

		assertFalse(nestedClass.isPublic(), "public modifier wasn't removed");
	}

	@Test
	public void testImplicitStaticModifierInNestedInterfaceTypeIsRemoved() {
		// contract: implicit static modifier for nested types is deleted when they are removed from the interface
		Factory factory = createFactory();
		CtInterface<?> ctInterface = factory.Interface().create("foo.Bar");
		CtClass<?> nestedClass = factory.Class().create("foo.Bar$Inner");
		ctInterface.addNestedType(nestedClass);

		assertTrue(nestedClass.isStatic(), "Class wasn't made static");
		ctInterface.removeNestedType(nestedClass);

		assertFalse(nestedClass.isStatic(), "static modifier wasn't removed");
	}

}
