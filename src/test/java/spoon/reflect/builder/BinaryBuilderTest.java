/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.builder;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.factory.Factory;

public class BinaryBuilderTest {

	@Test
	public void multiBuilderTest() {
		Factory factory = new Launcher().getFactory();
		Builder B = factory.Builder();
		CtUnaryOperator not = B.Not(B.Binary(B.Literal(2)).lower(B.Literal(1))).build();
		Assert.assertEquals("!(2 < 1)", not.toString());
	}

	@Test
	public void simpleBinaryTest() {
		Factory factory = new Launcher().getFactory();
		Builder B = factory.Builder();
		CtBinaryOperator binary = B.Binary(
				B.Literal(2))
				.multiplication(B.Literal(1))
				.equals(B.Literal(2))
				.build();
		Assert.assertEquals("(2 * 1) == 2", binary.toString());
	}

	@Test
	public void multiBinaryTest() {
		Factory factory = new Launcher().getFactory();
		Builder B = factory.Builder();
		CtBinaryOperator binary = B.Binary(
				B.Literal(2))
				.plus(B.Literal(1))
				.lower(
						B.Binary(B.Literal(10))
								.minus(B.Literal(5))
				).build();
		Assert.assertEquals("(2 + 1) < (10 - 5)", binary.toString());
	}
}