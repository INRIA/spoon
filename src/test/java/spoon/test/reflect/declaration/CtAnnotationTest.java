/**
 * Copyright (C) 2006-2019 INRIA and contributors
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
package spoon.test.reflect.declaration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;

public class CtAnnotationTest {

	@Test
	public void testGetValueAsObject() {
		CtClass<?> cl =
			Launcher.parseClass(
				"public class C { @SuppressWarnings(\"a+\"+Integer.SIZE) void m() {} }");
		CtAnnotation<?> annot = cl.getMethodsByName("m").get(0).getAnnotations().get(0);
		Object value = annot.getValueAsObject("value");
		assertEquals("a"+Integer.SIZE, value);
	}
}
