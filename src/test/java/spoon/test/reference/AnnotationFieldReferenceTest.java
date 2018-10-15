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
package spoon.test.reference;

import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Mole;
import spoon.test.reference.testclasses.Parameter;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AnnotationFieldReferenceTest {
	@Test
	public void testAnnotationFieldReference() throws Exception {
		final Factory factory = ModelUtils.build(Parameter.class, Mole.class);
		final CtMethod<Object> make = factory.Class().get(Mole.class).getMethod("make", factory.Type().createReference(Parameter.class));
		final CtInvocation<?> annotationInv = make.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class)).get(0);
		final CtExecutable<?> executableDeclaration = annotationInv.getExecutable().getExecutableDeclaration();
		assertNotNull(executableDeclaration);
		final CtMethod<?> value = factory.Annotation().get(Parameter.class).getMethod("value");
		assertNotNull(value);
		assertEquals(value.getSimpleName(), executableDeclaration.getSimpleName());
		assertEquals(value.getType(), executableDeclaration.getType());
	}
}
