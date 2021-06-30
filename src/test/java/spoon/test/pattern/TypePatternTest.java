/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.pattern;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.code.CtTypePatternImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		CtTypePattern<?> pattern = (CtTypePattern<?>) instanceOf.getRightHandOperand();
		assertEquals("java.lang.String", pattern.getVariable().getType().toString());
		assertEquals("s", pattern.getVariable().getSimpleName());
	}
}
