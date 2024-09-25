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
package spoon.test.constructor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.constructor.testclasses.AClass;
import spoon.test.constructor.testclasses.ImplicitConstructor;
import spoon.test.constructor.testclasses.Tacos;
import spoon.testing.utils.ModelUtils;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class ConstructorTest {
	private Factory factory;
	private CtClass<?> aClass;

	@BeforeEach
	public void setUp() {
		SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/constructor/testclasses/",
				"-o", "./target/spooned/"
		});
		factory = launcher.getFactory();
		aClass = factory.Class().get(Tacos.class);
	}

	@Test
	public void testImplicitConstructor() throws Exception {
		CtClass<?> ctType = (CtClass) ModelUtils.buildClass(ImplicitConstructor.class);

		assertTrue(ctType.getConstructor().isImplicit());
		assertFalse(aClass.getConstructor().isImplicit());
	}

	@Test
	public void testTransformationOnConstructorWithInsertBegin() {
		final CtConstructor<?> ctConstructor = aClass.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0);
		ctConstructor.getBody().insertBegin(factory.Code().createCodeSnippetStatement("int i = 0"));

		assertEquals(2, ctConstructor.getBody().getStatements().size());
		assertEquals("super()", ctConstructor.getBody().getStatement(0).toString());

		canBeBuilt("./target/spooned/spoon/test/constructor/testclasses/", 8);
	}

	@Test
	public void testTransformationOnConstructorWithInsertBefore() {
		final CtConstructor<?> ctConstructor = aClass.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0);
		try {
			ctConstructor.getBody().getStatement(0).insertBefore(factory.Code().createCodeSnippetStatement("int i = 0"));
			fail();
		} catch (RuntimeException ignore) {
		}
		assertEquals(1, ctConstructor.getBody().getStatements().size());
		assertEquals("super()", ctConstructor.getBody().getStatement(0).toString());
	}

	@Test
	public void callParamConstructor() {
		CtClass<Object> aClass = factory.Class().get(AClass.class);
		CtConstructor<Object> constructor = aClass.getConstructors().iterator().next();
		assertEquals("{" + System.lineSeparator()
				+ "    enclosingInstance.super();" + System.lineSeparator()
				+ "}", constructor.getBody().toString());
	}

	@Test
	public void testConstructorCallFactory() {
		CtTypeReference<ArrayList> ctTypeReference = factory.Code()
				.createCtTypeReference(ArrayList.class);
		CtConstructorCall<ArrayList> constructorCall = factory.Code()
				.createConstructorCall(ctTypeReference);
		assertEquals("new java.util.ArrayList()", constructorCall.toString());

		CtConstructorCall<ArrayList> constructorCallWithParameter = factory.Code()
				.createConstructorCall(ctTypeReference, constructorCall);

		assertEquals("new java.util.ArrayList(new java.util.ArrayList())", constructorCallWithParameter.toString());
	}

	@Test
	public void testTypeAnnotationOnExceptionDeclaredInConstructors() {
		final CtConstructor<?> aConstructor = aClass.getConstructor(factory.Type().objectType());

		assertEquals(1, aConstructor.getThrownTypes().size());
		Set<CtTypeReference<? extends Throwable>> thrownTypes = aConstructor.getThrownTypes();
		final CtTypeReference[] thrownTypesReference = thrownTypes.toArray(new CtTypeReference[thrownTypes.size()]);
		assertEquals(1, thrownTypesReference.length);
		assertEquals(1, thrownTypesReference[0].getAnnotations().size());
		assertEquals("java.lang.@spoon.test.constructor.testclasses.Tacos.TypeAnnotation(integer = 1)" + System.lineSeparator() + "Exception", thrownTypesReference[0].toString());
	}

	@Test
	public void testTypeAnnotationWithConstructorsOnFormalType() {
		final CtConstructor<?> aConstructor = aClass.getConstructor(factory.Type().objectType());

		assertEquals(1, aConstructor.getFormalCtTypeParameters().size());

		// New type parameter declaration.
		CtTypeParameter typeParameter = aConstructor.getFormalCtTypeParameters().get(0);
		assertEquals("T", typeParameter.getSimpleName());
		assertEquals(1, typeParameter.getAnnotations().size());
		assertIntersectionTypeInConstructor(typeParameter.getSuperclass());
	}

	private void assertIntersectionTypeInConstructor(CtTypeReference<?> boundingType1) {
		assertTrue(boundingType1 instanceof CtIntersectionTypeReference);
		CtIntersectionTypeReference<?> boundingType = boundingType1.asCtIntersectionTypeReference();

		final List<CtTypeReference<?>> bounds = boundingType.getBounds().stream().collect(Collectors.toList());
		CtTypeReference<?> genericTacos = bounds.get(0);
		assertEquals("Tacos", genericTacos.getSimpleName());
		assertEquals(1, genericTacos.getAnnotations().size());

		assertEquals(1, genericTacos.getActualTypeArguments().size());
		CtTypeParameterReference wildcard = (CtTypeParameterReference) genericTacos.getActualTypeArguments().get(0);
		assertEquals("?", wildcard.getSimpleName());
		assertEquals(1, wildcard.getAnnotations().size());
		assertEquals("C", wildcard.getBoundingType().getSimpleName());
		assertEquals(1, wildcard.getBoundingType().getAnnotations().size());

		assertEquals("Serializable", bounds.get(1).getSimpleName());
		assertEquals(1, bounds.get(1).getAnnotations().size());
	}

	@Test
	public void test_addParameterAt_addsParameterToSpecifiedPosition() {
		// contract: the parameter should be added at the specified position
		Factory factory = new Launcher().getFactory();

		CtConstructor<?> constructor = factory.createConstructor();

		CtParameter<String> first = factory.createParameter();
		CtTypeReference<String> firstType = factory.Type().stringType();
		first.setSimpleName("x");
		first.setType(firstType);

		CtParameter<Integer> second = factory.createParameter();
		CtTypeReference<Integer> secondType = factory.Type().integerType();
		second.setSimpleName("y");
		second.setType(secondType);

		CtParameter<Boolean> third = factory.createParameter();
		CtTypeReference<Boolean> thirdType = factory.Type().booleanType();
		third.setSimpleName("z");
		third.setType(thirdType);

		constructor.addParameterAt(0, second);
		constructor.addParameterAt(1, third);
		constructor.addParameterAt(0, first);

		assertThat(constructor.getParameters(), equalTo(Arrays.asList(first, second, third)));
	}

	@Test
	public void test_addParameterAt_throwsOutOfBoundsException_whenPositionIsOutOfBounds() {
		// contract: `addParameterAt` should throw an out of bounds exception when the specified position is out of
		// bounds of the parameter collection
		Factory factory = new Launcher().getFactory();
		CtConstructor<?> constructor = factory.createConstructor();
		CtParameter<?> paramater = factory.createParameter();

		assertThrows(IndexOutOfBoundsException.class,
				() -> constructor.addParameterAt(2, paramater));
	}

	@Test
	public void test_addFormalCtTypeParameterAt_addsTypeParameterToSpecifiedPosition() {
		// contract: addFormalCtTypeParameterAt should respect the position provided to it.

		// arrange
		Factory factory = new Launcher().getFactory();

		CtConstructor<?> constructor = factory.createConstructor();

		CtTypeParameter first = factory.createTypeParameter();
		first.setSimpleName("T");
		CtTypeParameter second = factory.createTypeParameter();
		second.setSimpleName("E");
		CtTypeParameter third = factory.createTypeParameter();
		third.setSimpleName("C");

		// act
		// add the type parameters out-of-order but in the correct positions
		constructor.addFormalCtTypeParameterAt(0, second);
		constructor.addFormalCtTypeParameterAt(0, first);
		constructor.addFormalCtTypeParameterAt(2, third);

		assertThat(constructor.getFormalCtTypeParameters(), equalTo(Arrays.asList(first, second, third)));
	}

	@Test
	public void test_addFormalCtTypeParameterAt_throwsOutOfBoundsException_whenPositionIsOutOfBounds() {
		// contract: addFormalCtTypeParameterAt should throw an out ouf bounds exception when the
		// specified position is out of bounds
		Factory factory = new Launcher().getFactory();
		CtConstructor<?> constructor = factory.createConstructor();
		CtTypeParameter typeParam = factory.createTypeParameter();

		assertThrows(IndexOutOfBoundsException.class,
				() -> constructor.addFormalCtTypeParameterAt(1, typeParam));
	}
}
