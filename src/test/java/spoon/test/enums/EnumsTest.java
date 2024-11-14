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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.SpoonTestHelpers;
import spoon.test.annotation.AnnotationTest;
import spoon.test.enums.testclasses.Burritos;
import spoon.test.enums.testclasses.EnumWithMembers;
import spoon.test.enums.testclasses.NestedEnums;
import spoon.test.enums.testclasses.Regular;
import spoon.testing.utils.GitHubIssue;
import spoon.testing.utils.ModelTest;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.test.SpoonTestHelpers.containsRegexMatch;
import static spoon.test.SpoonTestHelpers.contentEquals;
import static spoon.testing.utils.ModelUtils.build;

public class EnumsTest {

	@Test
	void testModelBuildingEnum() throws Exception {
		// contract: an enum built by spoon equals the real enum in following aspects:
		// - the simple name
		// - the amount of enum values
		// - the order of enum values
		// - the amount of overall fields
		CtEnum<Regular> enumeration = build("spoon.test.enums.testclasses", "Regular");
		assertThat(enumeration.getSimpleName(), is("Regular"));
		assertThat(enumeration.getEnumValues().size(), is(Regular.values().length));
		assertThat(map(CtEnumValue::getSimpleName, enumeration.getEnumValues()), is(Arrays.asList("A", "B", "C")));
		assertThat(enumeration.getFields().size(), is(5));
	}

	@Test
	void testAnnotationsOnEnum() throws Exception {
		// contract: an annotation on an enum value is represented in the spoon model
		final CtEnum<?> foo = build("spoon.test.enums.testclasses", "Foo");
		assertThat(foo.getEnumValues().size(), is(1));
		CtEnumValue<?> value = foo.getEnumValues().get(0);
		assertThat(value.getAnnotations().size(), is(1));
		assertThat(map(AnnotationTest::getActualClassFromAnnotation, value.getAnnotations()), hasItem(Deprecated.class));
		assertSame(Deprecated.class, AnnotationTest.getActualClassFromAnnotation(
				foo.getFields().get(0).getAnnotations().get(0)));
		// finding with a regex to avoid printer-specific output
		assertThat(foo.prettyprint(), containsRegexMatch("@(java\\.lang\\.)?Deprecated\\W+Bar"));
	}

	private static <I, O> List<O> map(Function<I, O> function, List<I> input) {
		return input.stream().map(function).collect(Collectors.toList());
	}

	@Test
	void testEnumWithoutValue() throws Exception {
		// contract: an enum without values contains a ; before any other members
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		assertThat(burritos.prettyprint(), containsRegexMatch("Burritos \\{\\W+;"));
	}

	@Test
	void testGetAllMethods() throws Exception {
		// contract: getAllMethods also returns the methods of Enum
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		CtMethod<String> name = factory.Core().createMethod();
		name.setSimpleName("name"); // from Enum
		name.setType(factory.Type().createReference(String.class));
		assertTrue(burritos.hasMethod(name));
		assertTrue(burritos.getAllMethods().contains(name));
	}

	@ParameterizedTest
	@ArgumentsSource(NestedEnumTypeProvider.class)
	void testNestedPrivateEnumValues(CtEnum<?> type) {
		// contract: enum values have correct modifiers matching the produced bytecode
		assertThat(type.getField("VALUE").getExtendedModifiers(), contentEquals(
				CtExtendedModifier.implicit(ModifierKind.PUBLIC),
				CtExtendedModifier.implicit(ModifierKind.STATIC),
				CtExtendedModifier.implicit(ModifierKind.FINAL)
		));
	}

	@Test
	void testPrintEnumValues() throws IOException {
		// contract: enum values constructor calls are correctly interpreted as implicit or not
		// TODO this test is incomplete, it does not cover anonymous enum constants
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
	void testEnumValue() {
		// contract: constructorCall on enum values should be implicit if they're not declared
		// TODO this test is incomplete, it does not cover anonymous enum constants
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/EnumClass.java");
		CtModel model = launcher.buildModel();

		List<CtEnumValue<?>> enumValues = model.getElements(new TypeFilter<>(CtEnumValue.class));

		assertThat(enumValues.size(), is(4));

		for (int i = 0; i < 3; i++) {
			CtEnumValue<?> ctEnumValue = enumValues.get(i);
			CtExpression<?> defaultExpression = ctEnumValue.getDefaultExpression();

			if (i != 2) {
				assertTrue(defaultExpression.isImplicit());
			} else {
				assertFalse(defaultExpression.isImplicit());
			}
		}
	}

	@Test
	void testEnumMembersModifiers() throws Exception {
		// contract: enum members should have correct modifiers
		final Factory factory = build(EnumWithMembers.class);
		CtModel model = factory.getModel();

		CtField<?> lenField = model.getElements(new TypeFilter<>(CtField.class)).stream()
				.filter(p -> "len".equals(p.getSimpleName()))
				.findFirst().get();

		assertTrue(lenField.isPrivate());
		assertTrue(lenField.isStatic());
		assertFalse(lenField.isFinal());
		assertFalse(lenField.isPublic());
		assertFalse(lenField.isProtected());
	}

	@Test
	void testEnumClassPublicFinalEnum() throws Exception {
		// contract: enum modifiers are applied correctly (JLS 8.9)
		// package-level enum, isn't static but implicitly final
		CtType<?> publicFinalEnum = build("spoon.test.enums.testclasses", "Burritos");
		assertThat(publicFinalEnum.getExtendedModifiers(), contentEquals(
				CtExtendedModifier.explicit(ModifierKind.PUBLIC),
				CtExtendedModifier.implicit(ModifierKind.FINAL)
		));
	}

	@ModelTest(value = "src/test/java/spoon/test/enums/testclasses", complianceLevel = 11)
	void testEnumClassModifiersPublicEnum(CtModel model) {
		// contract: enum modifiers are applied correctly (JLS 8.9)
		// pre Java 17, enums aren't implicitly final if an enum value declares an anonymous type
		CtType<?> publicEnum = model.getAllTypes().stream().filter(v -> v.getSimpleName().equals("AnonEnum")).findFirst().get();

		assertThat(publicEnum.getExtendedModifiers(), contentEquals(
				CtExtendedModifier.explicit(ModifierKind.PUBLIC)
		));
	}

	@Test
	void testEnumClassModifiersPublicEnumJava17() throws Exception {
		// contract: enum modifiers are applied correctly (JLS 8.9)
		// in Java 17, enums are implicitly sealed if an enum value declares an anonymous type
		// and the permitted types are the anonymous types declared by the enum values
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/enums/testclasses/AnonEnum.java");
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.buildModel();
		CtType<?> publicEnum = launcher.getFactory().Type().get("spoon.test.enums.testclasses.AnonEnum");
		assertThat(publicEnum.getExtendedModifiers(), contentEquals(
				new CtExtendedModifier(ModifierKind.PUBLIC, false),
				new CtExtendedModifier(ModifierKind.SEALED, true)
		));
	}

	@Test
	void testEnumValueModifiers() throws Exception {
		// contract: anonymous enum classes are always final
		CtEnum<?> publicEnum = build("spoon.test.enums.testclasses", "AnonEnum");
		// get the '{ }' of 'A { }'
		CtExpression<?> defaultExpression = publicEnum.getEnumValues().get(0).getDefaultExpression();
		// it has to be a CtNewClass, otherwise it wouldn't declare an anonymous type
		assertTrue(defaultExpression instanceof CtNewClass);
		assertThat(((CtNewClass<?>) defaultExpression).getAnonymousClass().getExtendedModifiers(), contentEquals(
				CtExtendedModifier.implicit(ModifierKind.FINAL)
		));
	}

	@Test
	void testLocalEnumExists() {
		// contract: local enums and their members are part of the model
		String code = SpoonTestHelpers.wrapLocal(
				"		enum MyEnum {\n" +
						"			A,\n" +
						"			B;\n" +
						"			public void doNothing() { }\n" +
						"		}\n"
		);
		CtModel model = SpoonTestHelpers.createModelFromString(code, 16);
		CtBlock<?> block = SpoonTestHelpers.getBlock(model);

		assertThat("The local enum does not exist in the model", block.getStatements().size(), is(1));

		CtStatement statement = block.getStatement(0);
		Assertions.assertTrue(statement instanceof CtEnum<?>);
		CtEnum<?> enumType = (CtEnum<?>) statement;

		assertThat(enumType.isLocalType(), is(true));
		assertThat(enumType.getSimpleName(), is("1MyEnum"));
		assertThat(enumType.getEnumValues().size(), is(2));
		assertThat(enumType.getMethods().size(), is(1));
		assertThat(enumType.getExtendedModifiers(), contentEquals(
				CtExtendedModifier.implicit(ModifierKind.STATIC),
				CtExtendedModifier.implicit(ModifierKind.FINAL)
		));
	}

	@Test
	@GitHubIssue(issueNumber = 4758, fixed = true)
	@DisplayName("Implicit enum constructors do not contain a super call")
	void testImplicitEnumConstructorSuperCall() {
		CtEnum<?> myEnum = (CtEnum<?>) Launcher.parseClass("enum Foo { CONSTANT; }");
		CtConstructor<?> constructor = myEnum.getConstructors().iterator().next();

		assertThat(constructor.isImplicit(), is(true));
		
		for (CtStatement statement : constructor.getBody().getStatements()) {
			if (!(statement instanceof CtInvocation)) {
				continue;
			}
			CtExecutableReference<?> executable = ((CtInvocation<?>) statement).getExecutable();
			if (!executable.getDeclaringType().getQualifiedName().equals("java.lang.Enum")) {
				continue;
			}
			assertThat(executable.getSimpleName(), not(is("<init>")));
		}
	}

	static class NestedEnumTypeProvider implements ArgumentsProvider {
		private final CtType<?> ctClass;

		NestedEnumTypeProvider() throws Exception {
			this.ctClass = ModelUtils.buildClass(NestedEnums.class);
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of("Private", "PackageProtected", "Protected", "Public")
					.map(s -> s + "ENUM")
					.map(ctClass::getNestedType)
					.map(Arguments::of);
		}
	}
}
