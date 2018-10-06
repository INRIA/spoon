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

import com.mysema.query.types.expr.ComparableExpressionBase;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.factory.testclasses3.Cooking;
import spoon.test.factory.testclasses3.Prepare;
import spoon.testing.utils.ModelUtils;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TypeFactoryTest {

	@Test
	public void testCreateTypeRef() {
		Launcher launcher = new Launcher();
		CtTypeReference<Object> ctTypeReference = launcher.getFactory().Code().createCtTypeReference(short.class);
		assertEquals("short", ctTypeReference.getSimpleName());
		assertEquals("short", ctTypeReference.getQualifiedName());

		ctTypeReference = launcher.getFactory().Code().createCtTypeReference(Object.class);
		assertEquals("Object", ctTypeReference.getSimpleName());
		assertEquals("java.lang.Object", ctTypeReference.getQualifiedName());

		ctTypeReference = launcher.getFactory().Code().createCtTypeReference(null);
		assertNull(ctTypeReference);

		ctTypeReference = launcher.getFactory().Code().createCtTypeReference(CtJavaDoc.CommentType.class);
		assertEquals("CommentType", ctTypeReference.getSimpleName());
		assertEquals("spoon.reflect.code.CtComment$CommentType", ctTypeReference.getQualifiedName());
	}

	@Test
	public void reflectionAPI() {
		// Spoon can be used as reflection API
		CtType s = new TypeFactory().get(String.class);
		assertEquals("String", s.getSimpleName());
		assertEquals("java.lang.String", s.getQualifiedName());
		assertEquals(3, s.getSuperInterfaces().size());
		assertEquals(2, s.getMethodsByName("toLowerCase").size());
	}

	@Test
	public void reflectionAPIWithTypeParameter() {
		// check the creation of the ComparableExpressionBase with the reflexion API
		CtType<Object> ctType = new Launcher().getFactory().Type().get(ComparableExpressionBase.class);
		assertEquals("ComparableExpressionBase", ctType.getSimpleName());
		CtMethod<?> method = ctType.getMethodsByName("castToNum").get(0);
		assertEquals("A extends java.lang.Number & java.lang.Comparable<? super A>", method.getFormalCtTypeParameters().get(0).toString());
	}

	@Test
	public void testGetClassInAnInterface() throws Exception {
		final CtType<Cooking> cook = ModelUtils.buildClass(Cooking.class);

		assertNotNull(cook.getFactory().Type().get("spoon.test.factory.testclasses3.Cooking$Tacos"));
		assertNotNull(cook.getFactory().Class().get("spoon.test.factory.testclasses3.Cooking$Tacos"));
		assertNotNull(cook.getFactory().Type().get(Cooking.Tacos.class));
		assertNotNull(cook.getFactory().Class().get(Cooking.Tacos.class));

		final CtType<Prepare> prepare = ModelUtils.buildClass(Prepare.class);

		assertNotNull(prepare.getFactory().Type().get("spoon.test.factory.testclasses3.Prepare$Tacos"));
		assertNotNull(prepare.getFactory().Interface().get("spoon.test.factory.testclasses3.Prepare$Tacos"));
		assertNotNull(prepare.getFactory().Type().get(Prepare.Pozole.class));
		assertNotNull(prepare.getFactory().Interface().get(Prepare.Pozole.class));
	}

	@Test
	public void testGetClassWithDollarAndNestedClass() {
		//Classes with name containing $ without being nested classes can contain nested classes
		Factory factory = ModelUtils.build(new File("./src/test/resources/dollar-and-nested-classes"));
		CtType<?> poorName = factory.Type().get("$Poor$Name");
		CtType<?> poorNameChoice = factory.Type().get("$Poor$Name$Choice");
		assertNotNull(poorName);
		assertNotNull(poorNameChoice);
		assertEquals(poorNameChoice,poorName.getMethodsByName("lookingForTroubles").get(0).getType().getTypeDeclaration());
	}
}
