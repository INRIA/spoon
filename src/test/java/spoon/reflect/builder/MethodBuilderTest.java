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
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class MethodBuilderTest {

	@Test
	public void test() {
		Factory factory = new Launcher().getFactory();
		Builder x = factory.Builder();

		CtReturn<Object> aReturn = factory.Core().createReturn();
		aReturn.setReturnedExpression(x.Literal(1).build());
		CtMethod m = x.Method("m")
				.type(int.class)
				.setPrivate()
				.setStatic()
				.addParam(int.class, "x")
				.inBody(aReturn).build();
		Assert.assertEquals("private static int m(int x) {\n"
				+ "    return 1;\n"
				+ "}", m.toString());

	}

}