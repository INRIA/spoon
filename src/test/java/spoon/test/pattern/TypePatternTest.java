/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.code.CtTypePatternImpl;
import spoon.testing.utils.ModelTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypePatternTest {

	private static CtModel createModelFromString(String code) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new VirtualFile(code));
		return launcher.buildModel();
	}

	@Test
	public void testWithInstanceOf() {
		// contract: type patterns are parsed correctly into the spoon metamodel
		String code =
				"class X {\n" +
				"	String typePattern(Object obj) {\n" +
				"		if (obj instanceof String s) {\n" +
				"			return s;\n" +
				"		}\n" +
				"		return \"\";\n" +
				"	}\n" +
				"}\n";
		CtModel model = createModelFromString(code);
		CtBinaryOperator<Boolean> instanceOf = model.getElements(new TypeFilter<CtBinaryOperator<Boolean>>(CtBinaryOperator.class)).get(0);
		assertEquals(BinaryOperatorKind.INSTANCEOF, instanceOf.getKind());
		assertEquals(CtTypePatternImpl.class, instanceOf.getRightHandOperand().getClass());
		CtTypePattern pattern = (CtTypePattern) instanceOf.getRightHandOperand();
		assertEquals("java.lang.String", pattern.getVariable().getType().toString());
		assertEquals("s", pattern.getVariable().getSimpleName());
	}

	@Test
	public void testValidateParent() {
		// contract: When setting a type pattern's parent, only CtBinaryOperator (and null) should be allowed
		Launcher launcher = new Launcher();
		CtTypePattern pattern = launcher.getFactory().Core().createTypePattern();

		// setting to a binary operator must work
		assertDoesNotThrow((Executable) () -> pattern.setParent(launcher.getFactory().createBinaryOperator()));
		// setting to null must work
		assertDoesNotThrow(() -> pattern.setParent(null));
		// setting something else as parent must fail
		assertThrows(SpoonException.class, () -> pattern.setParent(launcher.getFactory().createBlock()));
	}

	@ModelTest(value = "src/test/resources/patternmatching/InstanceofPatternMatch.java", complianceLevel = 16)
	void testTypePatternSourcePosition(Factory factory) {
		// contract: the source position of the CtTypePattern is equal to its CtLocalVariableDeclaration
		CtType<?> x = factory.Type().get("X");
		CtTypePattern typePattern = x.getElements(new TypeFilter<>(CtTypePattern.class)).get(0);
		assertTrue(typePattern.getPosition().isValidPosition());
		assertThat(typePattern.getPosition(), equalTo(typePattern.getVariable().getPosition()));
	}
}
