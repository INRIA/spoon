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
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.AnnotationFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class AnnotationFactoryTest {

	@Test
	public void testAnnotate() throws Exception {

		CtClass<?> type = build("spoon.test.testclasses", "SampleClass");

		AnnotationFactory af = type.getFactory().Annotation();
		af.annotate(type, SampleAnnotation.class, "names", new String[]{"foo", "bar"});

		final CtAnnotation<SampleAnnotation> annotation = type.getAnnotation(type.getFactory().Annotation().createReference(SampleAnnotation.class));
		assertTrue(annotation.getValue("names") instanceof CtNewArray);
		final CtNewArray names = annotation.getValue("names");
		assertEquals(2, names.getElements().size());
		assertEquals("foo", ((CtLiteral) names.getElements().get(0)).getValue());
		assertEquals("bar", ((CtLiteral) names.getElements().get(1)).getValue());
	}
}

@interface SampleAnnotation {
	String[] names();
}
