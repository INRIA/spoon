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
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.createFactory;

public class ExecutableFactoryTest {

	@Test
	public void testCreateReference() {
		Factory f = createFactory();
		ExecutableFactory ef = f.Executable();
		String signature = "boolean Object#equals(Object)";
		CtExecutableReference<Object> eref = ef.createReference(signature);

		String type = eref.getType().getQualifiedName();
		String decltype = eref.getDeclaringType().getQualifiedName();
		String name = eref.getSimpleName();
		List<CtTypeReference<?>> params = eref.getParameters();
		List<CtTypeReference<?>> atas = eref.getActualTypeArguments();

		assertEquals("boolean", type);
		assertEquals("Object", decltype);
		assertEquals("equals", name);
		assertEquals(1, params.size());
		assertEquals(0, atas.size());
	}
}
