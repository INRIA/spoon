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
package spoon.test.limits.utils;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.limits.utils.testclasses.ContainInternalClass;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static spoon.testing.utils.ModelUtils.build;

public class InternalTest {

	@Test
	public void testInternalClasses() throws Exception {
		CtClass<?> type = build("spoon.test.limits.utils.testclasses", "ContainInternalClass");
		assertEquals("ContainInternalClass", type.getSimpleName());
		List<CtClass<?>> classes = type.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(4, classes.size());
		CtClass<?> c1 = classes.get(1);
		assertEquals("InternalClass", c1.getSimpleName());
		assertEquals(
				"spoon.test.limits.utils.testclasses.ContainInternalClass$InternalClass",
				c1.getQualifiedName());
		assertEquals("spoon.test.limits.utils.testclasses", c1.getPackage().getQualifiedName());
		assertSame(ContainInternalClass.InternalClass.class, c1.getActualClass());

		CtClass<?> c2 = classes.get(2);
		assertEquals("InsideInternalClass", c2.getSimpleName());
		assertEquals(
				"spoon.test.limits.utils.testclasses.ContainInternalClass$InternalClass$InsideInternalClass",
				c2.getQualifiedName());
		assertSame(ContainInternalClass.InternalClass.InsideInternalClass.class, c2.getActualClass());
	}

	@Test
	public void testStaticFinalFieldInAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.limits.utils.testclasses", "ContainInternalClass");
		List<CtClass<?>> classes = type.getElements(new TypeFilter<>(CtClass.class));
		CtClass<?> c3 = classes.get(3);
		List<CtNamedElement> fields = c3
				.getElements(new NamedElementFilter<>(CtNamedElement.class,"serialVersionUID"));
		assertEquals(1, fields.size());
	}
}
