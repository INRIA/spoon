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
package spoon.test.parent;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class TopLevelTypeTest
{
	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}


	@Test
	public void testTopLevelType() {
		CtClass<?> foo = factory.Class().get(Foo.class);
		assertEquals(foo, foo.getTopLevelType());
		CtMethod<?> internalClassMethod = foo.getMethod("internalClass");
		assertEquals(foo, internalClassMethod.getDeclaringType());
		assertEquals(foo, internalClassMethod.getTopLevelType());
		CtClass<?> internalClass = (CtClass<?>)internalClassMethod.getBody().getStatement(0);
		assertEquals(foo, internalClassMethod.getDeclaringType());
		assertEquals(foo, internalClassMethod.getTopLevelType());
		CtMethod<?> mm = internalClass.getMethod("m");
		assertEquals(internalClass, mm.getDeclaringType());
		assertEquals(foo, mm.getTopLevelType());
	}

}
