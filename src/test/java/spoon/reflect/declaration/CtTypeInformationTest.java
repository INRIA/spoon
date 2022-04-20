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
package spoon.reflect.declaration;


import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.RandomAccess;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.testclasses.ExtendsArrayList;
import spoon.reflect.declaration.testclasses.Subclass;
import spoon.reflect.declaration.testclasses.Subinterface;
import spoon.reflect.declaration.testclasses.TestInterface;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.adaption.TypeAdaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class CtTypeInformationTest {
	private Factory factory;

	@BeforeEach
	public void setUp() throws Exception {
		factory = build(ExtendsArrayList.class, Subclass.class, Subinterface.class, TestInterface.class);
	}

	@Test
	public void testSomeSubtypes() {
		// This test previously tested that the scanning ended early enough but that does not sound
		// like information I want to expose. And keeping a field around for that and accessing it
		// via reflection is *dirty*.
		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		final CtTypeReference<?> subinterface = this.factory.Type().createReference(Subinterface.class);
		final CtTypeReference<?> testInterface = this.factory.Type().createReference(TestInterface.class);
		final CtTypeReference<?> extendObject = this.factory.Type().createReference(ExtendsArrayList.class);
		final CtTypeReference<?> arrayList = this.factory.Type().createReference(ArrayList.class);
		final CtTypeReference<?> abstractList = this.factory.Type().createReference(AbstractList.class);
		final CtTypeReference<?> abstractCollection = this.factory.Type().createReference(AbstractCollection.class);
		final CtTypeReference<?> object = this.factory.Type().createReference(Object.class);

		{
			TypeAdaptor typeAdaptor = new TypeAdaptor(subClass);

			//contract: this.isSubttypeOf(this) == true
			assertTrue(typeAdaptor.isSubtypeOf(subClass.getReference()));

			assertTrue(typeAdaptor.isSubtypeOf(subinterface));

			assertTrue(typeAdaptor.isSubtypeOf(testInterface));

			assertTrue(typeAdaptor.isSubtypeOf(factory.createCtTypeReference(Comparable.class)));

			assertTrue(typeAdaptor.isSubtypeOf(extendObject));

			assertTrue(typeAdaptor.isSubtypeOf(arrayList));
			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			assertTrue(typeAdaptor.isSubtypeOf(extendObject));
			assertTrue(typeAdaptor.isSubtypeOf(subClass.getReference()));


			assertTrue(typeAdaptor.isSubtypeOf(factory.createCtTypeReference(RandomAccess.class)));

			assertTrue(typeAdaptor.isSubtypeOf(abstractList));

			assertTrue(typeAdaptor.isSubtypeOf(abstractCollection));

			assertTrue(typeAdaptor.isSubtypeOf(object));

			//contract: ClassTypingContext returns false on a type which is not a sub type of ctc scope.
			assertFalse(typeAdaptor.isSubtypeOf(factory.Type().createReference("java.io.InputStream")));
			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			assertTrue(typeAdaptor.isSubtypeOf(arrayList));
			assertTrue(typeAdaptor.isSubtypeOf(extendObject));
			assertTrue(typeAdaptor.isSubtypeOf(subClass.getReference()));
		}

		{
			//now try directly a type which is not a supertype
			TypeAdaptor typeAdaptor = new TypeAdaptor(subClass);
			//contract: ClassTypingContext returns false on a type which is not a sub type of ctc scope.
			assertFalse(typeAdaptor.isSubtypeOf(factory.Type().createReference("java.io.InputStream")));

			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			assertTrue(typeAdaptor.isSubtypeOf(arrayList));
			assertTrue(typeAdaptor.isSubtypeOf(extendObject));
			assertTrue(typeAdaptor.isSubtypeOf(subClass.getReference()));
		}
	}

	@Test
	public void testGetAllMethodsReturnsTheRightNumber() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/ExtendsObject.java");
		launcher.buildModel();
		int nbMethodsObject = launcher.getFactory().Type().get(Object.class).getAllMethods().size();

		final CtType<?> extendsObject = launcher.getFactory().Type().get("test.ExtendsObject");
		assertEquals(2, extendsObject.getMethods().size(), "It should contain only 'oneMethod' and 'toString' but also contains: " + StringUtils.join(extendsObject.getMethods(), "\n"));
		assertEquals(nbMethodsObject + 1, extendsObject.getAllMethods().size());
	}

	@Test
	public void testGetSuperclass() {
		final CtType<?> extendsArrayList = this.factory.Type().get(ExtendsArrayList.class);

		// only 1 method directly in this class
		assertEquals(1, extendsArrayList.getMethods().size());

		int nbMethodExtendedArrayList = extendsArrayList.getAllMethods().size();

		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		assertEquals(2, subClass.getMethods().size());

		// the abstract method from Comparable which is overridden should not be present in the model
		assertEquals(nbMethodExtendedArrayList + 2, subClass.getAllMethods().size());

		CtTypeReference<?> superclass = subClass.getSuperclass();
		assertEquals(ExtendsArrayList.class.getName(), superclass.getQualifiedName());

		assertEquals(ExtendsArrayList.class.getName(), superclass.getQualifiedName());

		assertNotNull(superclass.getSuperclass());

		// test superclass of interface type reference
		Set<CtTypeReference<?>> superInterfaces = subClass.getSuperInterfaces();
		assertEquals(1, superInterfaces.size());
		CtTypeReference<?> superinterface = superInterfaces.iterator().next();
		assertEquals(Subinterface.class.getName(), superinterface.getQualifiedName());
		assertNull(superinterface.getSuperclass());

		// test superclass of interface
		final CtType<?> type2 = this.factory.Type().get(Subinterface.class);
		assertNull(type2.getSuperclass());

		// the interface abstract method and the implementation method have the same signature
		CtMethod<?> fooConcrete = subClass.getMethodsByName("foo").get(0);
		CtMethod<?> fooAbstract = type2.getMethodsByName("foo").get(0);
		assertEquals(fooConcrete.getSignature(), fooAbstract.getSignature());
		// yet they are different AST node
		assertNotEquals(fooConcrete, fooAbstract);

		assertEquals(subClass.getMethodsByName("foo").get(0).getSignature(),
				type2.getMethodsByName("foo").get(0).getSignature());
	}

	@Test
	public void testGetAllMethodsWontReturnOverriddenMethod() {
		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		Set<CtMethod<?>> listCtMethods = subClass.getAllMethods();

		boolean detectedCompareTo = false;
		for (CtMethod<?> ctMethod : listCtMethods) {
			if ("compareTo".equals(ctMethod.getSimpleName())) {
				assertFalse(ctMethod.hasModifier(ModifierKind.ABSTRACT));
				assertFalse(ctMethod.getParameters().get(0).getType() instanceof CtTypeParameter);
				assertEquals("Object", ctMethod.getParameters().get(0).getType().getSimpleName());
				detectedCompareTo = true;
			}
		}

		assertTrue(detectedCompareTo);
	}
}
