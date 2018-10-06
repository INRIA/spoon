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
package spoon.test.varargs;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.trycatch.testclasses.Main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class VarArgsTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build("spoon.test.varargs.testclasses", "VarArgsSample");
		assertEquals("VarArgsSample", type.getSimpleName());
		CtMethod<?> m = type.getMethodsByName("foo").get(0);

		CtParameter<?> param0 = m.getParameters().get(0);
		assertFalse(param0.isVarArgs());

		CtParameter<?> param1 = m.getParameters().get(1);
		assertTrue(param1.isVarArgs());
		assertEquals("java.lang.String[]", param1.getType().toString());
		assertEquals("String[]", param1.getType().getSimpleName());
		assertEquals("java.lang.String[]", param1.getType().getQualifiedName());
		assertEquals("java.lang.String", ((CtArrayTypeReference<?>)param1.getType()).getComponentType().toString());
		// we can even rewrite the vararg
		assertEquals("void foo(int arg0, java.lang.String... args) {"
				+ DefaultJavaPrettyPrinter.LINE_SEPARATOR + "}", m.toString());
	}


}
