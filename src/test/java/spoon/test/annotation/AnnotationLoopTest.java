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
package spoon.test.annotation;

import org.junit.Test;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.annotation.testclasses.Pozole;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AnnotationLoopTest {

	@Test
	public void testAnnotationDeclaredInForInit() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);

		final CtFor aLoop = aPozole.getMethod("cook").getElements(new TypeFilter<>(CtFor.class)).get(0);
		assertEquals(3, aLoop.getForInit().size());
		assertSame(SuppressWarnings.class, aLoop.getForInit().get(0).getAnnotations().get(0).getAnnotationType().getActualClass());
		assertSame(SuppressWarnings.class, aLoop.getForInit().get(1).getAnnotations().get(0).getAnnotationType().getActualClass());
		assertSame(SuppressWarnings.class, aLoop.getForInit().get(2).getAnnotations().get(0).getAnnotationType().getActualClass());

		assertEquals("u", ((CtLocalVariable) aLoop.getForInit().get(0)).getSimpleName());
		assertEquals("p", ((CtLocalVariable) aLoop.getForInit().get(1)).getSimpleName());
		assertEquals("e", ((CtLocalVariable) aLoop.getForInit().get(2)).getSimpleName());

		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(0)).getType());
		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(1)).getType());
		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(2)).getType());

		final String nl = System.lineSeparator();
		final String expected = "for (@java.lang.SuppressWarnings(\"rawtypes\")" + nl + "java.lang.String u = \"\", p = \"\", e = \"\"; u != e; u = p , p = \"\") {" + nl + "}";
		assertEquals(expected, aLoop.toString());
	}
}
