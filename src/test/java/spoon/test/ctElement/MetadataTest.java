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
package spoon.test.ctElement;

import org.junit.Test;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.ctElement.testclasses.Returner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.testing.utils.ModelUtils.build;

public class MetadataTest {

	@Test
	public void testMetadata() throws Exception {

		final Factory factory = build(Returner.class);
		final CtClass<Returner> returnerClass = factory.Class().get(Returner.class);
		final CtMethod<?> staticMethod = returnerClass.getMethodsByName("get").get(0);
		final CtReturn<Integer> ret = staticMethod.getBody().getLastStatement();

		assertNotNull(ret.getMetadataKeys());

		final CtMethod<?> staticMethod2 = returnerClass.getMethodsByName("get2").get(0);
		final CtReturn<Integer> ret2 = staticMethod2.getBody().getLastStatement();

		ret.putMetadata("foo", "bar");
		ret.putMetadata("fiz", 1);

		assertNotNull(ret.getMetadata("fiz"));
		assertNull(ret2.getMetadata("fiz"));
		assertEquals(1, ret.getMetadata("fiz"));
		assertEquals("bar", ret.getMetadata("foo"));
	}
}
