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
package spoon.test.factory;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.createFactory;

public class ClassFactoryTest {
	@Test
	public void testDeclaringClass() {
		final Factory factory = createFactory();
		final CtClass<Object> declaringClass = factory.Core().createClass();
		declaringClass.setSimpleName("DeclaringClass");

		final CtClass<Object> inner = factory.Class().create(declaringClass, "Inner");

		assertEquals("Inner", inner.getSimpleName());
		assertEquals(declaringClass, inner.getDeclaringType());
	}

	@Test
	public void testTopLevelClass() {
		final Factory factory = createFactory();
		final CtPackage aPackage = factory.Core().createPackage();
		aPackage.setSimpleName("spoon");

		final CtClass<Object> topLevel = factory.Class().create(aPackage, "TopLevel");

		assertEquals("TopLevel", topLevel.getSimpleName());
		assertEquals(aPackage, topLevel.getPackage());
	}
}
