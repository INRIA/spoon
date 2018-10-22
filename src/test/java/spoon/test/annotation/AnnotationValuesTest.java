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
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.annotation.testclasses.AnnotationValues;
import spoon.test.annotation.testclasses.BoundNumber;

import java.lang.annotation.Annotation;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.annotation.AnnotationValuesTest.Request.on;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

public class AnnotationValuesTest {
	static int i;

	@Test
	public void testValuesOnJava7Annotation() throws Exception {
		CtType<AnnotationValues> aClass = buildClass(AnnotationValues.class);

		CtAnnotation<?> ctAnnotation = on(aClass).giveMeAnnotation(AnnotationValues.Annotation.class);
		on(ctAnnotation).giveMeAnnotationValue("integer").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("integers").isTypedBy(CtNewArray.class);
		on(ctAnnotation).giveMeAnnotationValue("string").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("strings").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("clazz").isTypedBy(CtFieldAccess.class);
		on(ctAnnotation).giveMeAnnotationValue("classes").isTypedBy(CtNewArray.class);
		on(ctAnnotation).giveMeAnnotationValue("b").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("e").isTypedBy(CtFieldAccess.class);
		on(ctAnnotation).giveMeAnnotationValue("ia").isTypedBy(CtAnnotation.class);
		on(ctAnnotation).giveMeAnnotationValue("ias").isTypedBy(CtNewArray.class);
	}

	@Test
	public void testValuesOnJava8Annotation() throws Exception {
		CtType<AnnotationValues> aClass = buildClass(AnnotationValues.class);
		CtConstructorCall aConstructorCall = aClass.getMethod("method").getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);

		CtAnnotation<?> ctAnnotation = on(aConstructorCall.getType()).giveMeAnnotation(AnnotationValues.Annotation.class);
		on(ctAnnotation).giveMeAnnotationValue("integer").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("integers").isTypedBy(CtNewArray.class);
		on(ctAnnotation).giveMeAnnotationValue("string").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("strings").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("clazz").isTypedBy(CtFieldAccess.class);
		on(ctAnnotation).giveMeAnnotationValue("classes").isTypedBy(CtNewArray.class);
		on(ctAnnotation).giveMeAnnotationValue("b").isTypedBy(CtLiteral.class);
		on(ctAnnotation).giveMeAnnotationValue("e").isTypedBy(CtFieldAccess.class);
		on(ctAnnotation).giveMeAnnotationValue("ia").isTypedBy(CtAnnotation.class);
		on(ctAnnotation).giveMeAnnotationValue("ias").isTypedBy(CtNewArray.class);
	}

	@Test
	public void testCtAnnotationAPI() throws Exception {
		Factory factory = createFactory();
		CtAnnotation<Annotation> annotation = factory.Core().createAnnotation();
		annotation.addValue("integers", 7);

		on(annotation).giveMeAnnotationValue("integers").isTypedBy(CtLiteral.class);

		annotation.addValue("integers", 42);

		on(annotation).giveMeAnnotationValue("integers").isTypedBy(CtNewArray.class);

		annotation.addValue("classes", String.class);

		on(annotation).giveMeAnnotationValue("classes").isTypedBy(CtFieldAccess.class);

		annotation.addValue("classes", Integer.class);

		on(annotation).giveMeAnnotationValue("classes").isTypedBy(CtNewArray.class);

		annotation.addValue("field", AnnotationValuesTest.class.getDeclaredField("i"));

		on(annotation).giveMeAnnotationValue("field").isTypedBy(CtFieldAccess.class);
	}

	@Test
	public void testAnnotationFactory() {
		final Factory factory = createFactory();
		final CtClass<Object> target = factory.Class().create("org.example.Tacos");

		on(target).isNotAnnotated();
		CtAnnotation<SuppressWarnings> annotation = factory.Annotation().annotate(target, SuppressWarnings.class, "value", "test");
		on(target).giveMeAnnotation(SuppressWarnings.class);
		on(annotation).giveMeAnnotationValue("value").isTypedBy(CtLiteral.class);

		annotation = factory.Annotation().annotate(target, SuppressWarnings.class, "value", "test2");
		on(annotation).giveMeAnnotationValue("value").isTypedBy(CtNewArray.class);
	}

	@Test
	public void testAnnotateWithEnum() {
		final Factory factory = createFactory();
		final CtClass<Object> target = factory.Class().create("org.example.Tacos");
		final CtField<String> field = factory.Field().create(target, new HashSet<>(), factory.Type().STRING, "field");
		target.addField(field);

		final CtAnnotation<BoundNumber> byteOrder = factory.Annotation().annotate(field, BoundNumber.class, "byteOrder", BoundNumber.ByteOrder.LittleEndian);
		assertEquals(byteOrder, on(field).giveMeAnnotation(BoundNumber.class));
		assertTrue(on(byteOrder).giveMeAnnotationValue("byteOrder").element instanceof CtFieldRead);
	}

	@Test
	public void testAnnotationPrintAnnotation() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/printer-test/spoon/test/AnnotationSpecTest.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(false); // avoid getting the comment for the equals
		launcher.buildModel();

		assertEquals(strCtClassOracle,
				launcher.getFactory().Class().getAll().get(0).getElements(new TypeFilter<>(CtClass.class)).get(2).toString());
	}

	private static final String nl = System.lineSeparator();

	private static final String strCtClassOracle = "@com.squareup.javapoet.AnnotationSpecTest.HasDefaultsAnnotation(o = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES, p = 1701, f = 11.1, m = { 9, 8, 1 }, l = java.lang.Override.class, j = @com.squareup.javapoet.AnnotationSpecTest.AnnotationA"
			+ ", q = @com.squareup.javapoet.AnnotationSpecTest.AnnotationC(\"bar\")"
			+ ", r = { java.lang.Float.class, java.lang.Double.class })"
			+ nl
			+ "public class IsAnnotated {}";

	static class Request {
		private static Request myself = new Request();
		private static CtElement element;

		public static Request on(CtElement ctElement) {
			assertNotNull(ctElement);
			element = ctElement;
			return myself;
		}

		public <A extends Annotation> CtAnnotation<? extends Annotation> giveMeAnnotation(Class<A> annotation) {
			for (CtAnnotation<? extends Annotation> ctAnnotation : element.getAnnotations()) {
				if (ctAnnotation.getActualAnnotation().annotationType().equals(annotation)) {
					return ctAnnotation;
				}
			}
			fail("Annotation isn't present on the current element.");
			return null;
		}

		public Request giveMeAnnotationValue(String key) {
			assertTrue("Element given in the method on should be an CtAnnotation.", element instanceof CtAnnotation);
			CtAnnotation<?> ctAnnotation = (CtAnnotation<?>) element;
			CtExpression value = null;
			try {
				value = ctAnnotation.getValue(key);
			} catch (ClassCastException e) {
				fail("Value of the given key can't be cast to an expression.");
			}
			assertNotNull(value);
			element = value;
			return myself;
		}

		public <T extends CtElement> Request isTypedBy(Class<T> expectedType) {
			try {
				expectedType.cast(element);
			} catch (ClassCastException e) {
				fail("The given element can't be cast by the given type.");
			}
			return myself;
		}

		public Request isNotAnnotated() {
			assertEquals(0, element.getAnnotations().size());
			return myself;
		}
	}
}
