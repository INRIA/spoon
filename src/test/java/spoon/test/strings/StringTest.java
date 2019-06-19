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
package spoon.test.strings;

import org.junit.Test;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class StringTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<?> type = build("spoon.test.strings.testclasses", "Main");
		assertEquals("Main", type.getSimpleName());

		CtBinaryOperator<?> op = type.getElements(
				new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class))
				.get(0);
		assertEquals("\"a\" + \"b\"", op.toString());
		assertEquals(BinaryOperatorKind.PLUS, op.getKind());
		assertTrue(op.getLeftHandOperand() instanceof CtLiteral);
		assertTrue(op.getRightHandOperand() instanceof CtLiteral);
	}
}
