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
package spoon.test.enums;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.annotation.AnnotationTest;
import spoon.test.enums.testclasses.Burritos;
import spoon.test.enums.testclasses.Foo;
import spoon.test.enums.testclasses.NestedEnums;
import spoon.test.enums.testclasses.Regular;
import spoon.test.enums.testclasses.EnumWithMembers;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumsTest {

	@Test
	public void testModelBuildingEnum() throws Exception {
		CtEnum<Regular> enumeration = build("spoon.test.enums.testclasses", "Regular");
		assertEquals("Regular", enumeration.getSimpleName());
		assertEquals(3, Regular.values().length);
		assertEquals(3, enumeration.getEnumValues().size());
		assertEquals("A", enumeration.getEnumValues().get(0).getSimpleName());
		assertEquals(5, enumeration.getFields().size());
	}

	@Test
	public void testAnnotationsOnEnum() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[]{
				"-i", "./src/test/java/spoon/test/enums/testclasses",
				"-o", "./target/spooned"
		});

		final CtEnum<?> foo = (CtEnum) launcher.getFactory().Type().get(Foo.class);
		assertEquals(1, foo.getFields().size());
		assertEquals(1, foo.getFields().get(0).getAnnotations().size());
		assertSame(Deprecated.class, AnnotationTest.getActualClassFromAnnotation(
				foo.getFields().get(0).getAnnotations().get(0)));
		assertEquals(
				"public enum Foo {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR + DefaultJavaPrettyPrinter.LINE_SEPARATOR
						+ "    @java.lang.Deprecated"
						+ DefaultJavaPrettyPrinter.LINE_SEPARATOR + "    Bar;}",
				foo.toString());
	}

	@Test
	public void testEnumWithoutField() throws Exception {
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		assertEquals("public enum Burritos {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    ;" + DefaultJavaPrettyPrinter.LINE_SEPARATOR + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    public static void m() {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    }" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "}", burritos.toString());
	}

	@Test
	public void testGetAllMethods() throws Exception {
		// contract: getAllMethods also returns the methods of Enum
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		CtMethod name = factory.Core().createMethod();
		name.setSimpleName("name"); // from Enum
		name.setType(factory.Type().createReference(String.class));
		assertTrue(burritos.hasMethod(name));
		assertTrue(burritos.getAllMethods().contains(name));
	}

	@Test
	public void testNestedPrivateEnumValues() throws Exception {
		// contract: enum values have correct modifiers
		CtType<?> ctClass = ModelUtils.buildClass(NestedEnums.class);
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PrivateENUM");
			assertEquals(asSet(ModifierKind.PRIVATE), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PRIVATE, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PublicENUM");
			assertEquals(asSet(ModifierKind.PUBLIC), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PUBLIC, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("ProtectedENUM");
			assertEquals(asSet(ModifierKind.PROTECTED), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PROTECTED, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PackageProtectedENUM");
			assertEquals(asSet(), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
	}

	private <T> Set<T> asSet(T... values) {
		return new HashSet<>(Arrays.asList(values));
	}

	@Test
	public void testPrintEnumValues() throws IOException {
		// contract: enum values constructor calls are correctly interpreted as implicit or not
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/EnumClass.java");
		launcher.setSourceOutputDirectory("./target/test-enum");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();

		File file = new File("target/test-enum/spoon/test/comment/testclasses/EnumClass.java");
		assertTrue(file.exists());

		String content = StringUtils.join(Files.readAllLines(file.toPath()), "\n");

		assertTrue(content.contains("FAIL,"));
		assertTrue(content.contains("KEEP_OLD_NODE(),"));

		assertTrue(content.contains("/**\n"
				+ "     * Add new {@link RootNode} after existing nodes\n"
				+ "     */\n"
				+ "    APPEND"));

		assertTrue(content.contains("/**\n"
				+ "     * Keep old {@link RootNode} and ignore requests to add new {@link RootNode}\n"
				+ "     */\n"
				+ "    KEEP_OLD_NODE(),"));
	}

	@Test
	public void testEnumValue() {
		// contract: constructorCall on enum values should be implicit if they're not declared

		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/EnumClass.java");
		CtModel model = launcher.buildModel();

		List<CtEnumValue> enumValues = model.getElements(new TypeFilter<>(CtEnumValue.class));

		assertEquals(4, enumValues.size());

		for (int i = 0; i < 3; i++) {
			CtEnumValue ctEnumValue = enumValues.get(i);
			CtExpression defaultExpression = ctEnumValue.getDefaultExpression();

			if (i != 2) {
				assertTrue(defaultExpression.isImplicit());
			} else {
				assertFalse(defaultExpression.isImplicit());
			}
		}
	}

	@Test
	public void testEnumMembersModifiers() throws Exception {
		// contract: enum members should have correct modifiers
		final Factory factory = build(EnumWithMembers.class);
		CtModel model = factory.getModel();

		CtField lenField = model.getElements(new TypeFilter<>(CtField.class)).stream()
				.filter(p -> "len".equals(p.getSimpleName()))
				.findFirst().get();

		assertTrue(lenField.isPrivate());
		assertTrue(lenField.isStatic());
		assertFalse(lenField.isFinal());
		assertFalse(lenField.isPublic());
		assertFalse(lenField.isProtected());
	}
}
