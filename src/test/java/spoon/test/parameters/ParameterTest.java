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
package spoon.test.parameters;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ParameterTest {

	@Test
	public void testParameterInNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/parameter");
		launcher.setSourceOutputDirectory("./target/parameter");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.eclipse.draw2d.text.FlowUtilities");
		final CtParameter<?> parameter = aClass.getElements(new NamedElementFilter<>(CtParameter.class,"font")).get(0);

		assertEquals("font", parameter.getSimpleName());
		assertNotNull(parameter.getType());
		assertEquals("org.eclipse.swt.graphics.Font", parameter.getType().toString());
		assertEquals("org.eclipse.swt.graphics.Font font", parameter.toString());
	}

	@Test
	public void testGetParameterReferenceInLambdaNoClasspath() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Tacos.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtMethod<?> ctMethod = launcher.getFactory().Type().get("Tacos").getMethodsByName("setStarRatings").get(0);
		CtParameter ctParameter = ctMethod.getBody().getStatement(0).getElements(new TypeFilter<CtParameter>(CtParameter.class) {
			@Override
			public boolean matches(CtParameter element) {
				return "entryPair".equals(element.getSimpleName()) && super.matches(element);
			}
		}).get(0);
		assertNotNull(ctParameter.getReference());

		List<CtParameterReference> elements = ctMethod.getBody().getStatement(0).getElements(new TypeFilter<CtParameterReference>(CtParameterReference.class) {
			@Override
			public boolean matches(CtParameterReference element) {
				return "entryPair".equals(element.getSimpleName()) && super.matches(element);
			}
		});
		assertEquals(2, elements.size());
		for (CtParameterReference element : elements) {
			assertEquals(ctParameter, element.getDeclaration());
			assertEquals(ctParameter.getReference(), element);
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMultiParameterLambdaTypeReference() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/lambdas/MultiParameterLambda.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		List<CtParameter> parameters;

		// test string parameters
		parameters = launcher.getModel()
						.getElements(new NamedElementFilter<>(CtMethod.class,"stringLambda"))
						.get(0)
						.getElements(new TypeFilter<>(CtParameter.class));
		assertEquals(2, parameters.size());
		for (final CtParameter param : parameters) {
			CtTypeReference refType = param.getReference().getType();
			assertEquals(launcher.getFactory().Type().STRING, refType);
		}

		// test integer parameters
		parameters = launcher.getModel()
				.getElements(new NamedElementFilter<>(CtMethod.class,"integerLambda"))
				.get(0)
				.getElements(new TypeFilter<>(CtParameter.class));
		assertEquals(2, parameters.size());
		for (final CtParameter param : parameters) {
			CtTypeReference refType = param.getReference().getType();
			assertEquals(launcher.getFactory().Type().INTEGER, refType);
		}

		// test unknown parameters
		parameters = launcher.getModel()
				.getElements(new NamedElementFilter<>(CtMethod.class,"unknownLambda"))
				.get(0)
				.getElements(new TypeFilter<>(CtParameter.class));
		assertEquals(2, parameters.size());
		for (final CtParameter param : parameters) {
			CtTypeReference refType = param.getReference().getType();
			// unknown parameters have no type
			assertNull(refType);
		}
	}
}
