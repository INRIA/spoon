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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonException;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.CtModel;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import spoon.test.annotation.testclasses.AnnotArray;
import spoon.test.annotation.testclasses.AnnotParamTypeEnum;
import spoon.test.annotation.testclasses.AnnotParamTypes;
import spoon.test.annotation.testclasses.AnnotationDefaultAnnotation;
import spoon.test.annotation.testclasses.AnnotationIntrospection;
import spoon.test.annotation.testclasses.AnnotationRepeated;
import spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass;
import spoon.test.annotation.testclasses.AnnotationsRepeated;
import spoon.test.annotation.testclasses.Bound;
import spoon.test.annotation.testclasses.Foo;
import spoon.test.annotation.testclasses.Foo.InnerAnnotation;
import spoon.test.annotation.testclasses.Foo.MiddleAnnotation;
import spoon.test.annotation.testclasses.Foo.OuterAnnotation;
import spoon.test.annotation.testclasses.GlobalAnnotation;
import spoon.test.annotation.testclasses.InnerAnnot;
import spoon.test.annotation.testclasses.Main;
import spoon.test.annotation.testclasses.PortRange;
import spoon.test.annotation.testclasses.SuperAnnotation;
import spoon.test.annotation.testclasses.TestInterface;
import spoon.test.annotation.testclasses.TypeAnnotation;
import spoon.test.annotation.testclasses.notrepeatable.StringAnnot;
import spoon.test.annotation.testclasses.repeatable.Repeated;
import spoon.test.annotation.testclasses.repeatable.Tag;
import spoon.test.annotation.testclasses.repeatandarrays.RepeatedArrays;
import spoon.test.annotation.testclasses.repeatandarrays.TagArrays;
import spoon.test.annotation.testclasses.shadow.DumbKlass;
import spoon.test.annotation.testclasses.spring.AliasFor;
import spoon.test.annotation.testclasses.typeandfield.SimpleClass;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

public class AnnotationTest {

	@Test
	public void testAnnotationValueReflection() {
		Factory factory = new Launcher().getFactory();

		CtTypeReference reference = factory.createCtTypeReference(PropertyGetter.class);
		CtAnnotation annotation = factory.Interface().get(CtNamedElement.class).getMethod("getSimpleName").getAnnotation(reference);

		assertEquals("The annotation must have a value", 1, annotation.getValues().size());
		assertEquals("NAME", ((CtFieldRead) annotation.getValue("role")).getVariable().getSimpleName());
	}

	@Test
	public void testModelBuildingAnnotationBound() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Bound.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<spoon.test.annotation.testclasses.Bound> type = factory.Type().get("spoon.test.annotation.testclasses.Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());

		// contract one can build an annotation from the annotation type
		CtAnnotation<?> annot = launcher.getFactory().createAnnotation(type.getReference());
		assertEquals("@spoon.test.annotation.testclasses.Bound", annot.toString());
	}

	@Test
	public void testWritingAnnotParamArray() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotParam.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.AnnotParam");
		assertEquals("@java.lang.SuppressWarnings({ \"unused\", \"rawtypes\" })",
				type.getElements(new TypeFilter<>(CtAnnotation.class)).get(0).toString());
	}

	@Test
	public void testModelBuildingAnnotationBoundUsage() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Main.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.Main");
		assertEquals("Main", type.getSimpleName());

		CtParameter<?> param = type.getElements(new TypeFilter<CtParameter<?>>(CtParameter.class)).get(0);
		assertEquals("a", param.getSimpleName());

		List<CtAnnotation<? extends Annotation>> annotations = param.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> a = annotations.get(0);
		Bound actualAnnotation = (Bound) a.getActualAnnotation();
		assertEquals(8, actualAnnotation.max());

		CtParameter<?> param2 = type.getMethodsByName("nn").get(0).getParameters().get(0);
		assertEquals("param2", param2.getSimpleName());

		List<CtAnnotation<? extends Annotation>> annotations2 = param2.getAnnotations();
		assertEquals(1, annotations2.size());

		CtAnnotation<?> annot = annotations2.get(0);
		assertEquals("10", annot.getValue("max").toString());

		Bound actualAnnotation2 = (Bound) annot.getActualAnnotation();
		assertEquals(10, actualAnnotation2.max());

		// contract: getAllvalues
		// only direct value, no default ones in getValues()
		assertEquals(1, a.getValues().size());
		assertEquals(0, annot.getValues().size());

		// direct values and default ones in getValues()
		assertEquals(1, a.getAllValues().size());
		assertEquals(1, annot.getAllValues().size());

		// the good value is selected, not the default value
		assertEquals("8", a.getAllValues().get("max").toString());
	}

	@Test
	public void testPersistenceProperty() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/PersistenceProperty.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.PersistenceProperty");
		assertEquals("PersistenceProperty", type.getSimpleName());
		assertEquals(2, type.getAnnotations().size());

		CtAnnotation<Target> a1 = type.getAnnotation(type.getFactory().Type().createReference(Target.class));
		assertNotNull(a1);

		CtAnnotation<Retention> a2 = type.getAnnotation(type.getFactory().Type().createReference(Retention.class));
		assertNotNull(a2);

		assertTrue(a1.getValues().containsKey("value"));
		assertTrue(a2.getValues().containsKey("value"));
	}

	@Test
	public void testAnnotationParameterTypes() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Main.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.Main");

		CtMethod<?> m1 = type.getElements(new NamedElementFilter<>(CtMethod.class, "m1")).get(0);

		List<CtAnnotation<? extends Annotation>> annotations = m1.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> a = annotations.get(0);

		assertEquals(15, a.getAllValues().size());

		AnnotParamTypes annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42, a.getValueAsInt("integer"));
		assertEquals(42, annot.integer());
		assertEquals(1, annot.integers().length);
		assertEquals(42, annot.integers()[0]);
		assertEquals("Hello World!", a.getValueAsString("string"));
		assertEquals("Hello World!", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Hello", annot.strings()[0]);
		assertEquals("World", annot.strings()[1]);
		assertEquals(Integer.class, a.getValueAsObject("clazz"));
		assertSame(Integer.class, annot.clazz());
		assertEquals(2, annot.classes().length);
		assertSame(Integer.class, annot.classes()[0]);
		assertSame(String.class, annot.classes()[1]);
		assertTrue(annot.b());
		assertEquals('c', annot.c());
		assertEquals(42, annot.byt());
		assertEquals((short) 42, annot.s());
		assertEquals(42, annot.l());
		assertEquals(3.14f, annot.f(), 0f);
		assertEquals(3.14159, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dd", annot.ia().value());

		CtMethod<?> m2 = type.getElements(new NamedElementFilter<>(CtMethod.class, "m2")).get(0);

		annotations = m2.getAnnotations();
		assertEquals(1, annotations.size());

		a = annotations.get(0);
		annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42, annot.integer());
		assertEquals(1, annot.integers().length);
		assertEquals(42, annot.integers()[0]);
		assertEquals("Hello World!", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Hello", annot.strings()[0]);
		assertEquals("world", annot.strings()[1]);
		assertFalse(annot.b());
		assertEquals(42, annot.byt());
		assertEquals((short) 42, annot.s());
		assertEquals(42, annot.l());
		assertEquals(3.14f, annot.f(), 0f);
		assertEquals(3.14159, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dd", annot.ia().value());

		// tests binary expressions
		CtMethod<?> m3 = type.getElements(new NamedElementFilter<>(CtMethod.class, "m3")).get(0);

		annotations = m3.getAnnotations();
		assertEquals(1, annotations.size());

		a = annotations.get(0);
		annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(45, annot.integer());
		assertEquals(2, annot.integers().length);
		assertEquals(40, annot.integers()[0]);
		assertEquals(42 * 3, annot.integers()[1]);
		assertEquals("Hello World!concatenated", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Helloconcatenated", annot.strings()[0]);
		assertEquals("worldconcatenated", annot.strings()[1]);
		assertTrue(annot.b());
		assertEquals(42 ^ 1, annot.byt());
		assertEquals((short) 42 / 2, annot.s());
		assertEquals(43, annot.l());
		assertEquals(3.14f * 2f, annot.f(), 0f);
		assertEquals(3.14159d / 3d, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dddd", annot.ia().value());
	}

	@Test
	public void testAnnotatedElementTypes() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		// load package of the test classes
		CtPackage pkg = factory.Package().get("spoon.test.annotation.testclasses");

		// check annotated element type of the package annotation
		List<CtAnnotation<?>> annotations = pkg.getAnnotations();
		assertEquals(2, annotations.size());
		assertEquals(pkg, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.PACKAGE, annotations.get(0).getAnnotatedElementType());

		// load class Main from package and check annotated element type of the class annotation
		CtClass<?> clazz = pkg.getType("Main");
		assertSame(Main.class, clazz.getActualClass());

		annotations = clazz.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(clazz, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.TYPE, clazz.getAnnotations().get(0).getAnnotatedElementType());

		// load method toString() from class and check annotated element type of the annotation
		List<CtMethod<?>> methods = clazz.getMethodsByName("toString");
		assertEquals(1, methods.size());

		CtMethod<?> method = methods.get(0);
		assertEquals("toString", method.getSimpleName());

		annotations = method.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(method, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.METHOD, annotations.get(0).getAnnotatedElementType());

		// load parameter of method m(int) and check annotated element type of the parameter annotation
		methods = clazz.getMethodsByName("m");
		assertEquals(1, methods.size());

		method = methods.get(0);
		assertEquals("m", method.getSimpleName());

		List<CtParameter<?>> parameters = method.getParameters();
		assertEquals(1, parameters.size());

		CtParameter<?> parameter = parameters.get(0);
		annotations = parameter.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(parameter, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.PARAMETER, annotations.get(0).getAnnotatedElementType());

		// load constructor of the clazz and check annotated element type of the constructor annotation
		Set<? extends CtConstructor<?>> constructors = clazz.getConstructors();
		assertEquals(1, constructors.size());

		CtConstructor<?> constructor = constructors.iterator().next();
		annotations = constructor.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(constructor, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.CONSTRUCTOR, annotations.get(0).getAnnotatedElementType());

		// load value ia of the m1() method annotation, which is also an annotation
		// and check the annotated element type of the inner annotation.
		methods = clazz.getMethodsByName("m1");
		assertEquals(1, methods.size());

		method = methods.get(0);
		annotations = method.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> annotation = annotations.get(0);
		assertEquals(method, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.METHOD, annotations.get(0).getAnnotatedElementType());

		Object element = annotation.getValues().get("ia");
		assertNotNull(element);
		assertTrue(element instanceof CtAnnotation);
		assertEquals(annotation, ((CtAnnotation<?>) element).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.ANNOTATION_TYPE, ((CtAnnotation<?>) element).getAnnotatedElementType());

		// load enum AnnotParamTypeEnum and check the annotated element type of the annotation of the enum and of the fields
		CtEnum<?> enumeration = pkg.getType("AnnotParamTypeEnum");
		assertSame(AnnotParamTypeEnum.class, enumeration.getActualClass());

		annotations = enumeration.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(enumeration, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.TYPE, annotations.get(0).getAnnotatedElementType());

		List<CtEnumValue<?>> fields = enumeration.getEnumValues();
		assertEquals(3, fields.size());

		annotations = fields.get(0).getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(fields.get(0), annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.FIELD, annotations.get(0).getAnnotatedElementType());

		// load interface type TestInterface and check the annotated element type of the annotation
		CtInterface<?> ctInterface = pkg.getType("TestInterface");
		assertSame(TestInterface.class, ctInterface.getActualClass());

		annotations = ctInterface.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(ctInterface, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.TYPE, annotations.get(0).getAnnotatedElementType());

		// load annotation type Bound and check the annotated element type of the annotations
		CtAnnotationType<?> annotationType = pkg.getType("Bound");
		assertSame(Bound.class, annotationType.getActualClass());
		assertNull(annotationType.getSuperclass());
		assertEquals(1, annotationType.getMethods().size());
		assertEquals(0, annotationType.getSuperInterfaces().size());

		annotations = annotationType.getAnnotations();
		assertEquals(1, annotations.size());
		assertEquals(annotationType, annotations.get(0).getAnnotatedElement());
		assertEquals(CtAnnotatedElementType.ANNOTATION_TYPE, annotations.get(0).getAnnotatedElementType());
	}

	@Test
	public void testAnnotationWithDefaultArrayValue() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotArrayInnerClass.java");
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotArray.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final String res = "java.lang.Class<?>[] value() default {  };";

		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.AnnotArrayInnerClass");
		CtType<?> annotationInnerClass = type.getNestedType("Annotation");
		assertEquals("Annotation", annotationInnerClass.getSimpleName());
		assertEquals(1, annotationInnerClass.getAnnotations().size());
		assertEquals(res, annotationInnerClass.getMethod("value").toString());

		CtType<?> annotation = factory.Type().get("spoon.test.annotation.testclasses.AnnotArray");
		assertEquals("AnnotArray", annotation.getSimpleName());
		assertEquals(1, annotation.getAnnotations().size());
		assertEquals(res, annotation.getMethod("value").toString());
	}

	@Test
	public void testInnerAnnotationsWithArray() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Foo.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.Foo");
		final CtMethod<?> testMethod = ctClass.getMethodsByName("test").get(0);
		final List<CtAnnotation<? extends Annotation>> testMethodAnnotations = testMethod.getAnnotations();
		assertEquals(1, testMethodAnnotations.size());

		final CtAnnotation<? extends Annotation> firstAnnotation = testMethodAnnotations.get(0);
		assertSame(OuterAnnotation.class, getActualClassFromAnnotation(firstAnnotation));

		final CtNewArray<?> arrayAnnotations = (CtNewArray<?>) firstAnnotation.getValues().get("value");
		assertEquals(2, arrayAnnotations.getElements().size());

		final CtAnnotation<?> firstAnnotationInArray = getMiddleAnnotation(arrayAnnotations, 0);
		assertSame(MiddleAnnotation.class, getActualClassFromAnnotation(firstAnnotationInArray));

		final CtAnnotation<?> secondAnnotationInArray = getMiddleAnnotation(arrayAnnotations, 1);
		assertSame(MiddleAnnotation.class, getActualClassFromAnnotation(secondAnnotationInArray));

		final CtAnnotation<?> innerAnnotationInFirstMiddleAnnotation = getInnerAnnotation(firstAnnotationInArray);
		assertSame(InnerAnnotation.class, getActualClassFromAnnotation(innerAnnotationInFirstMiddleAnnotation));
		assertEquals("hello", getLiteralValueInAnnotation(innerAnnotationInFirstMiddleAnnotation).getValue());

		final CtAnnotation<?> innerAnnotationInSecondMiddleAnnotation = getInnerAnnotation(secondAnnotationInArray);
		assertSame(InnerAnnotation.class, getActualClassFromAnnotation(innerAnnotationInSecondMiddleAnnotation));
		assertEquals("hello again", getLiteralValueInAnnotation(innerAnnotationInSecondMiddleAnnotation).getValue());
	}

	@Test
	public void testAccessAnnotationValue() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Main.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.Main");
		CtMethod<?> testMethod = ctClass.getMethodsByName("testValueWithArray").get(0);
		Class<?>[] value = testMethod.getAnnotation(AnnotArray.class).value();
		assertArrayEquals(new Class[] { RuntimeException.class }, value);

		testMethod = ctClass.getMethodsByName("testValueWithoutArray").get(0);
		value = testMethod.getAnnotation(AnnotArray.class).value();
		assertArrayEquals(new Class[] { RuntimeException.class }, value);
	}

	@Test
	public void testUsageOfTypeAnnotationInNewInstance() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtConstructorCall<?> ctConstructorCall = ctClass.getElements(new AbstractFilter<CtConstructorCall<?>>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall<?> element) {
				return "String".equals(element.getType().getSimpleName());
			}
		}).get(0);
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = ctConstructorCall.getType().getAnnotations();

		assertEquals("Type of the new class must use an annotation", 1, typeAnnotations.size());
		assertSame("Type of the new class is typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("New class with an type annotation must be well printed", "new java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "String()", ctConstructorCall.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInCast() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtReturn<?> returns = ctClass.getElements(new AbstractFilter<CtReturn<?>>(CtReturn.class) {
			@Override
			public boolean matches(CtReturn<?> element) {
				return !element.getReturnedExpression().getTypeCasts().isEmpty();
			}
		}).get(0);
		final CtExpression<?> returnedExpression = returns.getReturnedExpression();
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = returnedExpression.getTypeCasts().get(0).getAnnotations();

		assertEquals("Cast with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertSame("Type annotation in the cast must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Cast with an type annotation must be well printed", "((java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "String) (s))", returnedExpression.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationBeforeExceptionInSignatureOfMethod() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m").get(0);
		final CtTypeReference<?> thrownReference = method.getThrownTypes().toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = thrownReference.getAnnotations();

		assertEquals("Thrown type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertSame("Type annotation with the thrown type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Thrown type with an type annotation must be well printed", "public void m() throws java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "Exception {"
						+ System.lineSeparator() + "}", method.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInReturnTypeInMethod() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m3").get(0);
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = method.getType().getAnnotations();

		assertEquals("Return type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertSame("Type annotation with the return type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Return type with an type annotation must be well printed", "public java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "String m3() {"
						+ System.lineSeparator()
						+ "    return \"\";"
						+ System.lineSeparator() + "}", method.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationOnParameterInMethod() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsAppliedOnAnyTypeInAClass.class);

		final CtMethod<?> method = ctClass.getMethodsByName("m6").get(0);
		final CtParameter<?> ctParameter = method.getParameters().get(0);
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = ctParameter.getType().getAnnotations();

		assertEquals("Parameter type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertSame("Type annotation with the parameter type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Parameter type with an type annotation must be well printed", "java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "String param", ctParameter.toString());
	}



	@Test
	public void testUsageOfTypeAnnotationOnLocalVariableInMethod() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsAppliedOnAnyTypeInAClass.class);

		final CtMethod<?> method = ctClass.getMethodsByName("m6").get(0);
		final CtLocalVariable<?> ctLocalVariable = method.getBody().getElements(new AbstractFilter<CtLocalVariable<?>>(CtLocalVariable.class) {
			@Override
			public boolean matches(CtLocalVariable<?> element) {
				return true;
			}
		}).get(0);
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = ctLocalVariable.getType().getAnnotations();

		assertEquals("Local variable type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertSame("Type annotation with the local variable type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Local variable type with an type annotation must be well printed", "java.lang.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "String s = \"\"", ctLocalVariable.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInExtendsImplementsOfAClass() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtClass<?> innerClass = ctClass.getElements(new NamedElementFilter<>(CtClass.class, "DummyClass")).get(0);
		final CtTypeReference<?> extendsActual = innerClass.getSuperclass();
		final List<CtAnnotation<? extends Annotation>> extendsTypeAnnotations = extendsActual.getAnnotations();
		final String superClassExpected = "spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "AnnotArrayInnerClass";
		assertEquals("Extends with a type annotation must have it in its model", 1, extendsTypeAnnotations.size());
		assertSame("Type annotation on a extends must be typed by TypeAnnotation", TypeAnnotation.class, extendsTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, extendsTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Extends with an type annotation must be well printed", superClassExpected, extendsActual.toString());

		final Set<CtTypeReference<?>> superInterfaces = innerClass.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterface = superInterfaces.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> implementsTypeAnnotations = firstSuperInterface.getAnnotations();
		final String superInterfaceExpected = "spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "BasicAnnotation";
		assertEquals("Implements with a type annotation must have it in its model", 1, implementsTypeAnnotations.size());
		assertSame("Type annotation on a extends must be typed by TypeAnnotation", TypeAnnotation.class, implementsTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, implementsTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Extends with an type annotation must be well printed", superInterfaceExpected, firstSuperInterface.toString());

		final CtEnum<?> enumActual = ctClass.getElements(new NamedElementFilter<>(CtEnum.class, "DummyEnum")).get(0);
		final Set<CtTypeReference<?>> superInterfacesOfEnum = enumActual.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterfaceOfEnum = superInterfacesOfEnum.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> enumTypeAnnotations = firstSuperInterfaceOfEnum.getAnnotations();
		final String enumExpected = "public enum DummyEnum implements spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "BasicAnnotation {" + System.lineSeparator() + "    ;" + System.lineSeparator() + "}";
		assertEquals("Implements in a enum with a type annotation must have it in its model", 1, enumTypeAnnotations.size());
		assertSame("Type annotation on a implements in a enum must be typed by TypeAnnotation", TypeAnnotation.class, enumTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, enumTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Implements in a enum with an type annotation must be well printed", enumExpected, enumActual.toString());

		final CtInterface<?> interfaceActual = ctClass.getElements(new NamedElementFilter<>(CtInterface.class, "DummyInterface")).get(0);
		final Set<CtTypeReference<?>> superInterfacesOfInterface = interfaceActual.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterfaceOfInterface = superInterfacesOfInterface.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> interfaceTypeAnnotations = firstSuperInterfaceOfInterface.getAnnotations();
		final String interfaceExpected = "public interface DummyInterface extends spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "BasicAnnotation {}";
		assertEquals("Implements in a interface with a type annotation must have it in its model", 1, interfaceTypeAnnotations.size());
		assertSame("Type annotation on a implements in a enum must be typed by TypeAnnotation", TypeAnnotation.class, interfaceTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, interfaceTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Implements in a interface with an type annotation must be well printed", interfaceExpected, interfaceActual.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationWithGenericTypesInClassDeclaration() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtClass<?> genericClass = ctClass.getElements(new NamedElementFilter<>(CtClass.class, "DummyGenericClass")).get(0);

		// New type parameter declaration.
		final List<CtTypeParameter> typeParameters = genericClass.getFormalCtTypeParameters();
		assertEquals("Generic class has 2 generics parameters.", 2, typeParameters.size());
		assertEquals("First generic type must have type annotation", "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "T", typeParameters.get(0).toString());
		assertEquals("Second generic type must have type annotation", "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "K", typeParameters.get(1).toString());

		final CtTypeReference<?> superInterface = genericClass.getSuperInterfaces().toArray(new CtTypeReference<?>[0])[0];
		final String expected = "spoon.test.annotation.testclasses.BasicAnnotation<@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "T>";
		assertEquals("Super interface has a generic type with type annotation", expected, superInterface.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationWithGenericTypesInStatements() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m4").get(0);

		// New type parameter declaration.
		final List<CtTypeParameter> typeParameters = method.getFormalCtTypeParameters();
		assertEquals("Method has 1 generic parameter", 1, typeParameters.size());
		assertEquals("Method with an type annotation must be well printed", "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "T", typeParameters.get(0).toString());

		final CtBlock<?> body = method.getBody();
		final String expectedFirstStatement =
				"java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation"
						+ System.lineSeparator() + "T> list = new java.util.ArrayList<>()";
		final CtStatement firstStatement = body.getStatement(0);
		assertEquals("Type annotation on generic parameter declared in the method",
					expectedFirstStatement, firstStatement.toString());
		final CtConstructorCall firstConstructorCall =
				firstStatement.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		final CtTypeReference<?> firstTypeReference = firstConstructorCall.getType()
																		.getActualTypeArguments()
																		.get(0);
		assertTrue(firstTypeReference.isImplicit());
		assertEquals("T", firstTypeReference.getSimpleName());

		final String expectedSecondStatement =
				"java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation"
						+ System.lineSeparator() + "?> list2 = new java.util.ArrayList<>()";
		final CtStatement secondStatement = body.getStatement(1);
		assertEquals("Wildcard with an type annotation must be well printed",
					expectedSecondStatement, secondStatement.toString());
		final CtConstructorCall secondConstructorCall =
				secondStatement.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		final CtTypeReference<?> secondTypeReference = secondConstructorCall.getType()
																			.getActualTypeArguments()
																			.get(0);
		assertTrue(secondTypeReference.isImplicit());
		assertEquals("Object", secondTypeReference.getSimpleName());

		final String expectedThirdStatement = "java.util.List<spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "BasicAnnotation> list3 = new java.util.ArrayList<spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "BasicAnnotation>()";
		assertEquals("Type in generic parameter with an type annotation must be well printed", expectedThirdStatement, body.getStatement(2).toString());
	}

	@Test
	public void testUsageOfParametersInTypeAnnotation() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");
		final CtMethod<?> method = ctClass.getMethodsByName("m5").get(0);

		final String integerParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(integer = 1)" + System.lineSeparator() + "T> list";
		assertEquals("integer parameter in type annotation", integerParam, method.getBody().getStatement(0).toString());

		final String arrayIntegerParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(integers = { 1 })" + System.lineSeparator() + "T> list2";
		assertEquals("array of integers parameter in type annotation", arrayIntegerParam, method.getBody().getStatement(1).toString());

		final String stringParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(string = \"\")" + System.lineSeparator() + "T> list3";
		assertEquals("string parameter in type annotation", stringParam, method.getBody().getStatement(2).toString());

		final String arrayStringParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(strings = { \"\" })" + System.lineSeparator() + "T> list4";
		assertEquals("array of strings parameter in type annotation", arrayStringParam, method.getBody().getStatement(3).toString());

		final String classParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(clazz = java.lang.String.class)" + System.lineSeparator() + "T> list5";
		assertEquals("class parameter in type annotation", classParam, method.getBody().getStatement(4).toString());

		final String arrayClassParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(classes = { java.lang.String.class })" + System.lineSeparator() + "T> list6";
		assertEquals("array of classes parameter in type annotation", arrayClassParam, method.getBody().getStatement(5).toString());

		final String primitiveParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(b = true)" + System.lineSeparator() + "T> list7";
		assertEquals("primitive parameter in type annotation", primitiveParam, method.getBody().getStatement(6).toString());

		final String enumParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(e = spoon.test.annotation.testclasses.AnnotParamTypeEnum.R)" + System.lineSeparator() + "T> list8";
		assertEquals("enum parameter in type annotation", enumParam, method.getBody().getStatement(7).toString());

		final String annotationParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(ia = @spoon.test.annotation.testclasses.InnerAnnot(\"\"))" + System.lineSeparator() + "T> list9";
		assertEquals("annotation parameter in type annotation", annotationParam, method.getBody().getStatement(8).toString());

		final String arrayAnnotationParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(ias = { @spoon.test.annotation.testclasses.InnerAnnot(\"\") })" + System.lineSeparator() + "T> list10";
		assertEquals("array of annotations parameter in type annotation", arrayAnnotationParam, method.getBody().getStatement(9).toString());

		final String complexArrayParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(inceptions = { @spoon.test.annotation.testclasses.Inception(value = @spoon.test.annotation.testclasses.InnerAnnot(\"\"), values = { @spoon.test.annotation.testclasses.InnerAnnot(\"\") }) })" + System.lineSeparator() + "T> list11";
		assertEquals("array of complexes parameters in type annotation", complexArrayParam, method.getBody().getStatement(10).toString());
	}

	@Test
	public void testOutputGeneratedByTypeAnnotation() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		launcher.buildModel();
		// we only write to disk here
		launcher.setSourceOutputDirectory(new File("./target/spooned-annotation-output/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);

		canBeBuilt(new File("./target/spooned-annotation-output/spoon/test/annotation/testclasses/"), 8);
	}

	@Test
	public void testRepeatSameAnnotationOnClass() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);

		final List<CtAnnotation<? extends Annotation>> annotations = ctClass.getAnnotations();
		assertEquals("Class must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"First\"", "First", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Second\"", "Second", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnField() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);
		final CtField<?> field = ctClass.getField("field");

		final List<CtAnnotation<? extends Annotation>> annotations = field.getAnnotations();
		assertEquals("Field must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Field 1\"", "Field 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Field 2\"", "Field 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnMethod() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("method").get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = method.getAnnotations();
		assertEquals("Method must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Method 1\"", "Method 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Method 2\"", "Method 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnConstructor() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);
		final CtConstructor<?> ctConstructor = ctClass.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtAnnotation<? extends Annotation>> annotations = ctConstructor.getAnnotations();
		assertEquals("Constructor must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Constructor 1\"", "Constructor 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Constructor 2\"", "Constructor 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnParameter() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("methodWithParameter").get(0);
		final CtParameter<?> ctParameter = method.getParameters().get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = ctParameter.getAnnotations();
		assertEquals("Parameter must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Param 1\"", "Param 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Param 2\"", "Param 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnLocalVariable() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("methodWithLocalVariable").get(0);
		final CtLocalVariable<?> ctLocalVariable = method.getBody().getElements(new AbstractFilter<CtLocalVariable<?>>(CtLocalVariable.class) {
			@Override
			public boolean matches(CtLocalVariable<?> element) {
				return true;
			}
		}).get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = ctLocalVariable.getAnnotations();
		assertEquals("Local variable must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Local 1\"", "Local 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Local 2\"", "Local 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testRepeatSameAnnotationOnPackage() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsRepeated.java");
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/package-info.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtPackage pkg = factory.Package().get("spoon.test.annotation.testclasses");

		final List<CtAnnotation<? extends Annotation>> annotations = pkg.getAnnotations();
		assertEquals("Local variable must to have multi annotation of the same type", 2, annotations.size());
		assertSame("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertSame("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Package 1\"", "Package 1", ((CtLiteral) annotations.get(0).getValue("value")).getValue());
		assertEquals("Argument of the second annotation is \"Package 2\"", "Package 2", ((CtLiteral) annotations.get(1).getValue("value")).getValue());
	}

	@Test
	public void testDefaultValueInAnnotationsForAnnotationFields() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationDefaultAnnotation.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtType<?> annotation = factory.Type().get(AnnotationDefaultAnnotation.class);

		final CtAnnotationMethod<?> ctAnnotations = annotation.getMethods().toArray(new CtAnnotationMethod<?>[0])[0];
		assertSame("Field is typed by an annotation.", InnerAnnot.class, ctAnnotations.getType().getActualClass());
		assertSame("Default value of a field typed by an annotation must be an annotation",
				InnerAnnot.class, ctAnnotations.getDefaultExpression().getType().getActualClass());
	}

	@Test
	public void testGetAnnotationOuter() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Foo.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtClass<?> ctClass = (CtClass<?>) factory.Type().get("spoon.test.annotation.testclasses.Foo");
		final CtMethod<?> testMethod = ctClass.getMethodsByName("test").get(0);
		Foo.OuterAnnotation annot = testMethod.getAnnotation(Foo.OuterAnnotation.class);
		assertNotNull(annot);
		assertEquals(2, annot.value().length);
	}

	@Test
	public void testAbstractAllAnnotationProcessor() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(false);
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/BasicAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TypeAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotParamTypeEnum.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/InnerAnnot.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Inception.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TestAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotArrayInnerClass.java");
		Factory factory = spoon.getFactory();
		spoon.buildModel();

		// create the processor
		final ProcessingManager p = new QueueProcessingManager(factory);
		final TypeAnnotationProcessor processor = new TypeAnnotationProcessor();
		p.addProcessor(processor);
		p.process(factory.Class().getAll());

		assertEquals(29, processor.elements.size());
	}

	@Test
	public void testAbstractAllAnnotationProcessorWithGlobalAnnotation() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/ClassProcessed.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TypeAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotParamTypeEnum.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/InnerAnnot.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Inception.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/GlobalAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TestAnnotation.java");
		Factory factory = spoon.getFactory();
		spoon.buildModel();

		// create the processor
		final ProcessingManager p = new QueueProcessingManager(factory);
		final GlobalProcessor processor = new GlobalProcessor();
		p.addProcessor(processor);
		final TypeAnnotationMethodProcessor methodProcessor = new TypeAnnotationMethodProcessor();
		p.addProcessor(methodProcessor);
		p.process(factory.Class().getAll());

		assertEquals(7, processor.elements.size()); // GlobalAnnotation is also attached to the type
		assertEquals(2, methodProcessor.elements.size());
	}

	@Test
	public void testAnnotationIntrospection() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationIntrospection.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtClass<Object> aClass = factory.Class().get(AnnotationIntrospection.class);
		CtMethod<?> mMethod = aClass.getMethod("m");
		CtStatement statement = mMethod.getBody().getStatement(1);
		assertEquals("annotation.equals(null)", statement.toString());
	}

	@Test
	public void testFieldAndMethodInAnnotation() throws Exception {
		final CtType<SuperAnnotation> aTypeAnnotation = buildClass(SuperAnnotation.class);
		final CtField<?> fieldValue = aTypeAnnotation.getField("value");
		assertNotNull(fieldValue);
		assertEquals("public static final java.lang.String value = \"\";", fieldValue.toString());
		final CtMethod<Object> methodValue = aTypeAnnotation.getMethod("value");
		assertTrue(methodValue instanceof CtAnnotationMethod);
		assertEquals("java.lang.String value() default spoon.test.annotation.testclasses.SuperAnnotation.value;", methodValue.toString());
		final CtMethod<Object> methodNoDefault = aTypeAnnotation.getMethod("value1");
		assertTrue(methodNoDefault instanceof CtAnnotationMethod);
		assertEquals("java.lang.String value1();", methodNoDefault.toString());

		assertEquals(2, aTypeAnnotation.getMethods().size());
		aTypeAnnotation.addMethod(methodValue.clone());
		assertEquals(2, aTypeAnnotation.getMethods().size());
	}

	@Test
	public void testAnnotationInterfacePreserveMethods() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/PortRange.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		final CtAnnotationType<?> ctAnnotationType = (CtAnnotationType) factory.Type().get(PortRange.class);
		List<CtMethod<?>> ctMethodMin = ctAnnotationType.getMethodsByName("min");
		assertEquals("Method min is preserved after transformation", 1, ctMethodMin.size());

		List<CtMethod<?>> ctMethodMax = ctAnnotationType.getMethodsByName("max");
		assertEquals("Method max is preserved after transformation", 1, ctMethodMax.size());

		List<CtMethod<?>> ctMethodMessage = ctAnnotationType.getMethodsByName("message");
		assertEquals("Method message is preserved after transformation", 1, ctMethodMessage.size());

		List<CtMethod<?>> ctMethodGroups = ctAnnotationType.getMethodsByName("groups");
		assertEquals("Method groups is preserved after transformation", 1, ctMethodGroups.size());

		List<CtMethod<?>> ctMethodPayload = ctAnnotationType.getMethodsByName("payload");
		assertEquals("Method payload is preserved after transformation", 1, ctMethodPayload.size());
	}

	abstract class AbstractElementsProcessor<A extends Annotation, E extends CtElement>
			extends AbstractAnnotationProcessor<A, E> {
		final List<CtElement> elements = new ArrayList<>();
		@Override
		public void process(A annotation, E element) {
			elements.add(element);
		}
	}

	class GlobalProcessor extends AbstractElementsProcessor<GlobalAnnotation, CtElement> {
		@Override
		public void process(GlobalAnnotation annotation, CtElement element) {
			super.process(annotation, element);
		}
	}

	class TypeAnnotationProcessor extends AbstractElementsProcessor<TypeAnnotation, CtElement> {
		@Override
		public void process(TypeAnnotation annotation, CtElement element) {
			super.process(annotation, element);
		}
	}

	class TypeAnnotationMethodProcessor extends AbstractElementsProcessor<TypeAnnotation, CtTypeReference<?>> {
		@Override
		public void process(TypeAnnotation annotation, CtTypeReference<?> element) {
			if (element.getParent() instanceof CtMethod) {
				super.process(annotation, element);
			}
		}
	}

	public static Class<? extends Annotation> getActualClassFromAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return annotation.getAnnotationType().getActualClass();
	}

	private CtLiteral<?> getLiteralValueInAnnotation(CtAnnotation<?> annotation) {
		return (CtLiteral<?>) annotation.getValues().get("value");
	}

	private CtAnnotation<?> getInnerAnnotation(CtAnnotation<?> firstAnnotationInArray) {
		return (CtAnnotation<?>) firstAnnotationInArray.getValues().get("value");
	}

	private CtAnnotation<?> getMiddleAnnotation(CtNewArray<?> arrayAnnotations, int index) {
		return (CtAnnotation<?>) arrayAnnotations.getElements().get(index);
	}

	@Test
	public void testSpoonSpoonResult() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/dropwizard/GraphiteReporterFactory.java");
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-firstspoon/";
		spoon.setSourceOutputDirectory(output);
		Factory factory = spoon.getFactory();
		spoon.run();

		Launcher spoon2 = new Launcher();
		spoon2.addInputResource(output + "/spoon/test/annotation/testclasses/dropwizard/GraphiteReporterFactory.java");
		spoon2.buildModel();

		List<CtField<?>> fields = spoon2.getModel().getElements(new NamedElementFilter(CtField.class, "port"));

		assertEquals("Number of fields port should be 1", 1, fields.size());

		CtField<?> getport = fields.get(0);
		CtTypeReference returnType = getport.getType();

		List<CtAnnotation<?>> annotations = returnType.getAnnotations();

		assertEquals("Number of annotation for return type of method getPort should be 1", 1, annotations.size());

		CtAnnotation annotation = annotations.get(0);

		assertEquals("Annotation should be @spoon.test.annotation.testclasses.PortRange", "spoon.test.annotation.testclasses.PortRange", annotation.getAnnotationType().getQualifiedName());
	}

	@Test
	public void testGetAnnotationFromParameter() {
		// contract: Java 8 receiver parameters are handled
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/resources/noclasspath/Initializer.java");
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-firstspoon/";
		spoon.setSourceOutputDirectory(output);
		spoon.getEnvironment().setNoClasspath(true);
		Factory factory = spoon.getFactory();
		spoon.buildModel();

		List<CtMethod> methods = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class, "setField"));
		assertThat(methods.size(), is(1));

		CtMethod methodSet = methods.get(0);
		assertThat(methodSet.getSimpleName(), is("setField"));

		List<CtParameter> parameters = methodSet.getParameters();

		assertThat(parameters.size(), is(1));

		CtParameter thisParameter = parameters.get(0);
		assertThat(thisParameter.getSimpleName(), is("this"));


		CtTypeReference thisParamType = thisParameter.getType();
		assertThat(thisParamType.getSimpleName(), is("Initializer"));

		List<CtAnnotation<?>> annotations = thisParameter.getType().getAnnotations();
		assertThat(annotations.size(), is(2));

		CtAnnotation unknownInit = annotations.get(0);
		CtAnnotation raw = annotations.get(1);

		assertThat(unknownInit.getAnnotationType().getSimpleName(), is("UnknownInitialization"));
		assertThat(raw.getAnnotationType().getSimpleName(), is("Raw"));
	}

	@Test
	public void annotationAddValue() {
		Launcher spoon = new Launcher();

		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Bar.java");
		spoon.buildModel();

		Factory factory = spoon.getFactory();

		List<CtMethod> methods = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class, "bidule"));

		assertThat(methods.size(), is(1));

		CtAnnotation anno1 = factory.Annotation().annotate(methods.get(0), TypeAnnotation.class).addValue("params", new String[] { "test"});
		assertThat(anno1.getValue("params").getType(), is(factory.Type().createReference(String[].class)));

		CtAnnotation anno = factory.Annotation().annotate(methods.get(0), TypeAnnotation.class).addValue("params", new String[0]);
		assertThat(anno.getValue("params").getType(), is(factory.Type().createReference(String[].class)));
	}

	@Test
	public void annotationOverrideFQNIsOK() {
		Launcher spoon = new Launcher();
		Factory factory = spoon.getFactory();
		factory.getEnvironment().setNoClasspath(true);
		spoon.addInputResource("./src/test/resources/noclasspath/annotation/issue1307/SpecIterator.java");
		spoon.buildModel();

		List<CtAnnotation> overrideAnnotations = factory.getModel().getElements(new TypeFilter<>(CtAnnotation.class));

		for (CtAnnotation annotation : overrideAnnotations) {
			CtTypeReference typeRef = annotation.getAnnotationType();
			if ("Override".equals(typeRef.getSimpleName())) {
				assertThat(typeRef.getQualifiedName(), is("java.lang.Override"));
			}
		}
	}

	@Test
	public void testCreateAnnotation() {
		final Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Annotation().create("spoon.test.annotation.testclasses.NewAnnot");
		assertTrue(type.isAnnotationType());
		assertSame(type, type.getReference().getDeclaration());
	}

	@Test
	public void testReplaceAnnotationValue() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Main.java");
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType<?> type = factory.Type().get("spoon.test.annotation.testclasses.Main");

		CtMethod<?> m1 = type.getElements(new NamedElementFilter<>(CtMethod.class, "m1")).get(0);

		List<CtAnnotation<? extends Annotation>> annotations = m1.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> a = annotations.get(0);
		AnnotParamTypes annot = (AnnotParamTypes) a.getActualAnnotation();

		//contract: test replace of single value
		CtExpression integerValue = a.getValue("integer");
		assertEquals(42, ((CtLiteral<Integer>) integerValue).getValue().intValue());
		assertEquals(42, annot.integer());
		integerValue.replace(factory.createLiteral(17));
		CtExpression newIntegerValue = a.getValue("integer");
		assertEquals(17, ((CtLiteral<Integer>) newIntegerValue).getValue().intValue());
		assertEquals(17, annot.integer());

		//contract: replacing of single value of map by multiple values must fail
		//even if second value is null
		try {
			a.getValue("integer").replace(Arrays.asList(factory.createLiteral(18), null));
			fail();
		} catch (SpoonException e)  {
			//OK
		}

		//contract: replacing of single value by no value
		a.getValue("integer").delete();
		assertNull(a.getValue("integer"));
		try {
			annot.integer();
			fail();
		} catch (NullPointerException e) {
			//OK - fails because int cannot be null
		}
		//contract: replace with null value means remove
		a.getValue("string").replace((CtElement) null);
		assertNull(a.getValue("string"));
		//contract: check that null value can be returned
		assertNull(annot.string());

		//contract: replace with null value in collection means remove
		a.getValue("clazz").replace(Collections.singletonList(null));
		assertNull(a.getValue("clazz"));
		//contract: check that null value can be returned
		assertNull(annot.clazz());

		//contract: test replace of item in collection
		assertEquals(1, annot.integers().length);
		assertEquals(42, annot.integers()[0]);
		CtNewArray<?> integersNewArray = (CtNewArray) a.getValue("integers");
		integersNewArray.getElements().get(0).replace(Arrays.asList(null, factory.createLiteral(101), null, factory.createLiteral(102)));
		assertEquals(2, annot.integers().length);
		assertEquals(101, annot.integers()[0]);
		assertEquals(102, annot.integers()[1]);
	}

	@Test
	public void testSpoonManageRecursivelyDefinedAnnotation() {
		// contract: Spoon manage to process recursively defined annotation in shadow classes
		// annotation fields are encoded as CtAnnotationMethod
		Launcher spoon = new Launcher();
		CtType type = spoon.getFactory().Type().get(AliasFor.class);
		assertEquals(3, type.getTypeMembers().size());
		assertTrue(type.getTypeMembers().get(0) instanceof CtAnnotationMethod);
	}

	@Test
	public void testRepeatableAnnotationAreManaged() {
		// contract: when two identical repeatable annotation are used, they should be displayed in two different annotations and not factorized
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/repeatable");
		spoon.buildModel();

		CtType type = spoon.getFactory().Type().get(Repeated.class);
		CtMethod firstMethod = (CtMethod) type.getMethodsByName("method").get(0);
		List<CtAnnotation<?>> annotations = firstMethod.getAnnotations();

		assertEquals(2, annotations.size());

		for (CtAnnotation a : annotations) {
			assertEquals("Tag", a.getAnnotationType().getSimpleName());
		}

		String classContent = type.toString();
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatable.Tag(\"machin\")"));
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatable.Tag(\"truc\")"));
	}

	@Test
	public void testCreateRepeatableAnnotation() {
		// contract: when creating two repeatable annotations, two annotations should be created

		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/repeatable");
		spoon.buildModel();

		CtType type = spoon.getFactory().Type().get(Repeated.class);
		CtMethod firstMethod = (CtMethod) type.getMethodsByName("withoutAnnotation").get(0);
		List<CtAnnotation<?>> annotations = firstMethod.getAnnotations();

		assertTrue(annotations.isEmpty());

		spoon.getFactory().Annotation().annotate(firstMethod, Tag.class, "value", "foo");
		assertEquals(1, firstMethod.getAnnotations().size());

		spoon.getFactory().Annotation().annotate(firstMethod, Tag.class, "value", "bar");

		annotations = firstMethod.getAnnotations();
		assertEquals(2, annotations.size());

		for (CtAnnotation a : annotations) {
			assertEquals("Tag", a.getAnnotationType().getSimpleName());
		}

		String classContent = type.toString();
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatable.Tag(\"foo\")"));
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatable.Tag(\"bar\")"));
	}

	@Test
	public void testRepeatableAnnotationAreManagedWithArrays() {
		// contract: when two identical repeatable annotation with arrays are used, they should be displayed in two different annotations and not factorized
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/repeatandarrays");
		spoon.buildModel();

		CtType type = spoon.getFactory().Type().get(RepeatedArrays.class);
		CtMethod firstMethod = (CtMethod) type.getMethodsByName("method").get(0);
		List<CtAnnotation<?>> annotations = firstMethod.getAnnotations();

		assertEquals(2, annotations.size());

		for (CtAnnotation a : annotations) {
			assertEquals("TagArrays", a.getAnnotationType().getSimpleName());
		}

		String classContent = type.toString();
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatandarrays.TagArrays({ \"machin\", \"truc\" })"));
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatandarrays.TagArrays({ \"truc\", \"bidule\" })"));
	}

	@Test
	public void testCreateRepeatableAnnotationWithArrays() {
		// contract: when using annotate with a repeatable annotation, it will create a new annotation, even if an annotation with an array already exists
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/repeatandarrays");
		spoon.buildModel();

		CtType type = spoon.getFactory().Type().get(Repeated.class);
		CtMethod firstMethod = (CtMethod) type.getMethodsByName("withoutAnnotation").get(0);
		List<CtAnnotation<?>> annotations = firstMethod.getAnnotations();

		assertTrue(annotations.isEmpty());

		spoon.getFactory().Annotation().annotate(firstMethod, TagArrays.class, "value", "foo");
		assertEquals(1, firstMethod.getAnnotations().size());

		spoon.getFactory().Annotation().annotate(firstMethod, TagArrays.class, "value", "bar");
		annotations = firstMethod.getAnnotations();
		assertEquals(2, annotations.size());

		for (CtAnnotation a : annotations) {
			assertEquals("TagArrays", a.getAnnotationType().getSimpleName());
		}

		String classContent = type.toString();
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatandarrays.TagArrays(\"foo\")"));
		assertTrue("Content of the file: " + classContent, classContent.contains("@spoon.test.annotation.testclasses.repeatandarrays.TagArrays(\"bar\")"));
	}

	@Test
	public void testAnnotationNotRepeatableNotArrayAnnotation() {
		// contract: when trying to annotate multiple times with same not repeatable, not array annotation, it should throw an exception
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/notrepeatable/StringAnnot.java");
		spoon.buildModel();

		CtMethod aMethod = spoon.getFactory().createMethod().setSimpleName(("mamethod"));

		spoon.getFactory().Annotation().annotate(aMethod, StringAnnot.class, "value", "foo");
		assertEquals(1, aMethod.getAnnotations().size());

		String methodContent = aMethod.toString();
		assertTrue("Content: " + methodContent, methodContent.contains("@spoon.test.annotation.testclasses.notrepeatable.StringAnnot(\"foo\")"));

		try {
			spoon.getFactory().Annotation().annotate(aMethod, StringAnnot.class, "value", "bar");
			methodContent = aMethod.toString();
			fail("You should not be able to add two values to StringAnnot annotation: " + methodContent);
		} catch (SpoonException e) {
			assertEquals("cannot assign an array to a non-array annotation element", e.getMessage());
		}
	}

	@Test
	public void testAnnotationTypeAndFieldOnField() throws IOException {
		// contract: annotation on field with an annotation type which supports type and field, should be attached both on type and field
		// see: https://docs.oracle.com/javase/specs/jls/se9/html/jls-9.html#jls-9.7.3
		// in this case, we want to print it only once before the type
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/typeandfield");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.setSourceOutputDirectory("./target/spooned-typeandfield");
		launcher.run();

		CtType type = launcher.getFactory().Type().get(SimpleClass.class);

		CtField field = type.getField("mandatoryField");
		assertEquals(1, field.getAnnotations().size());
		CtAnnotation annotation = field.getAnnotations().get(0);
		assertEquals("spoon.test.annotation.testclasses.typeandfield.AnnotTypeAndField", annotation.getAnnotationType().getQualifiedName());

		CtTypeReference fieldType = field.getType();
		assertEquals(1, fieldType.getAnnotations().size());
		CtAnnotation anotherAnnotation = fieldType.getAnnotations().get(0);
		assertEquals(annotation, anotherAnnotation);

		assertEquals("java.lang.String", field.getType().getQualifiedName());
		assertEquals(1, field.getType().getAnnotations().size());

		List<String> lines = Files.readAllLines(new File("./target/spooned-typeandfield/spoon/test/annotation/testclasses/typeandfield/SimpleClass.java").toPath());
		String fileContent = StringUtils.join(lines, "\n");

		assertTrue("Content :" + fileContent, fileContent.contains("@spoon.test.annotation.testclasses.typeandfield.AnnotTypeAndField"));
		assertTrue("Content :" + fileContent, fileContent.contains("public java.lang.String mandatoryField;"));
	}

	@Test
	public void testAnnotationAndShadowDefaultRetentionPolicy() {
		// contract: When the default retention policy is used in an annotation, it's lost in shadow classes
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/shadow");
		CtModel model = launcher.buildModel();
		CtClass<?> dumbKlass = model.getElements(new NamedElementFilter<>(CtClass.class, "DumbKlass")).get(0);
		CtMethod<?> fooMethod = dumbKlass.getMethodsByName("foo").get(0);

		final Factory shadowFactory = createFactory();
		CtType<?> shadowDumbKlass = shadowFactory.Type().get(DumbKlass.class);
		CtMethod<?> shadowFooMethod = shadowDumbKlass.getMethodsByName("foo").get(0);

		assertEquals(0, shadowFooMethod.getAnnotations().size());
	}

	@Test
	public void testAnnotationAndShadowClassRetentionPolicy() {
		// contract: When the Class retention policy is used in an annotation, it's lost in shadow classes
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/shadow");
		CtModel model = launcher.buildModel();
		CtClass<?> dumbKlass = model.getElements(new NamedElementFilter<>(CtClass.class, "DumbKlass")).get(0);
		CtMethod<?> fooMethod = dumbKlass.getMethodsByName("fooClass").get(0);

		final Factory shadowFactory = createFactory();
		CtType<?> shadowDumbKlass = shadowFactory.Type().get(DumbKlass.class);
		CtMethod<?> shadowFooMethod = shadowDumbKlass.getMethodsByName("fooClass").get(0);

		assertEquals(0, shadowFooMethod.getAnnotations().size());
	}

	@Test
	public void testAnnotationAndShadowRuntimeRetentionPolicy() {
		// contract: When the runtime retention policy is used in an annotation, it's available through shadow classes
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/shadow");
		CtModel model = launcher.buildModel();
		CtClass<?> dumbKlass = model.getElements(new NamedElementFilter<>(CtClass.class, "DumbKlass")).get(0);
		CtMethod<?> fooMethod = dumbKlass.getMethodsByName("barOneValue").get(0);

		final Factory shadowFactory = createFactory();
		CtType<?> shadowDumbKlass = shadowFactory.Type().get(DumbKlass.class);
		CtMethod<?> shadowFooMethod = shadowDumbKlass.getMethodsByName("barOneValue").get(0);

		assertEquals(fooMethod.getAnnotations().size(), shadowFooMethod.getAnnotations().size());
	}

	@Test
	public void testAnnotationArray() throws Exception {
		// contract: getValue should return a value as close as possible from the sourcecode:
		// i.e. even if the annotation should return an Array, it should return a single element
		// if the value is given without the braces. The same behaviour should be used both for
		// spooned source code and shadow classes.

		Method barOneValueMethod = DumbKlass.class.getMethod("barOneValue");
		Method barMultipleValueMethod = DumbKlass.class.getMethod("barMultipleValues");

		Annotation annotationOneValue = barOneValueMethod.getAnnotations()[0];
		Annotation annotationMultiple = barMultipleValueMethod.getAnnotations()[0];

		Object oneValue = annotationOneValue.getClass().getMethod("role").invoke(annotationOneValue);
		Object multipleValue = annotationMultiple.getClass().getMethod("role").invoke(annotationMultiple);

		// in Java both values are String arrays with same values
		assertTrue("[Java] annotation are not arrays type", oneValue instanceof String[] && multipleValue instanceof String[]);
		assertEquals("[Java] annotation string values are not the same", ((String[]) oneValue)[0], ((String[]) multipleValue)[0]);

		// in shadow classes, same behaviour: both annotation have the same values
		final Factory shadowFactory = createFactory();
		CtType<?> shadowDumbKlass = shadowFactory.Type().get(DumbKlass.class);
		CtMethod<?> shadowBarOne = shadowDumbKlass.getMethodsByName("barOneValue").get(0);
		CtAnnotation shadowAnnotationOne = shadowBarOne.getAnnotations().get(0);

		CtMethod<?> shadowMultiple = shadowDumbKlass.getMethodsByName("barMultipleValues").get(0);
		CtAnnotation shadowAnnotationMultiple = shadowMultiple.getAnnotations().get(0);

		assertEquals("[Shadow] Annotation one and multiple are not of the same type", shadowAnnotationOne.getAnnotationType(), shadowAnnotationMultiple.getAnnotationType());
		assertEquals("[Shadow] Annotation one and multiples values are not the same", shadowAnnotationOne.getValue("role"), shadowAnnotationMultiple.getValue("role"));

		// but with Spoon, we consider two different values
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/shadow");
		CtModel model = launcher.buildModel();
		CtClass<?> dumbKlass = model.getElements(new NamedElementFilter<>(CtClass.class, "DumbKlass")).get(0);
		CtMethod<?> barOneValue = dumbKlass.getMethodsByName("barOneValue").get(0);
		CtAnnotation annotationOne = barOneValue.getAnnotations().get(0);

		CtMethod<?> barMultipleValue = dumbKlass.getMethodsByName("barMultipleValues").get(0);
		CtAnnotation annotationMultipleVal = barMultipleValue.getAnnotations().get(0);

		assertEquals("[Spoon] Annotation one and multiple are not of the same type", annotationOne.getAnnotationType(), annotationMultipleVal.getAnnotationType());
		assertTrue(annotationOne.getValue("role") instanceof CtLiteral);
		assertTrue(annotationMultipleVal.getValue("role") instanceof CtNewArray);

		assertTrue(annotationOne.getWrappedValue("role") instanceof CtNewArray);
		assertTrue(annotationMultipleVal.getWrappedValue("role") instanceof CtNewArray);
		assertEquals(annotationMultipleVal.getWrappedValue("role"), annotationOne.getWrappedValue("role"));

		assertEquals(annotationOne.getAnnotationType(), shadowAnnotationOne.getAnnotationType());
		assertTrue(shadowAnnotationOne.getValue("role") instanceof CtLiteral); // should be CtLiteral
		assertEquals(annotationOne.getValue("role"), shadowAnnotationOne.getValue("role")); // should pass
	}

	@Test
	public void testGetValueAsObject() {
		// contract: annot.getValueAsObject now handles static values in binary classes
		CtClass<?> cl =
				Launcher.parseClass("public class C { " +
						"	@SuppressWarnings(\"int\"+Integer.SIZE) void i() {} " +
						"	@SuppressWarnings(\"str\"+java.io.File.pathSeparator) void s() {} " +
						"}");
		CtAnnotation<?> annot_i = cl.getMethodsByName("i").get(0).getAnnotations().get(0);
		CtAnnotation<?> annot_s = cl.getMethodsByName("s").get(0).getAnnotations().get(0);

		assertEquals("[int" + Integer.SIZE + "]", Arrays.toString((Object[]) annot_i.getValueAsObject("value")));
		assertEquals("[str" + File.pathSeparator + "]", Arrays.toString((Object[]) annot_s.getValueAsObject("value")));
	}

	@Test
	public void testCatchAnnotation() {
		// contract: annotations should be attached to CtCatchVariable and not to CtCatch itself.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationCatch.java");
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/CustomAnnotation.java");
		CtModel model = launcher.buildModel();
		CtClass<?> clazz = model.getElements(new NamedElementFilter<>(CtClass.class, "AnnotationCatch")).get(0);
		CtMethod<?> m1 = clazz.getMethodsByName("m1").get(0);
		CtCatch ctCatch = m1.getElements(new TypeFilter<>(CtCatch.class)).get(0);
		CtCatchVariable<?> ctCatchVariable = ctCatch.getParameter();
		assertTrue(ctCatch.getAnnotations().isEmpty());
		assertEquals(1, ctCatchVariable.getAnnotations().size());
		assertEquals("@spoon.test.annotation.testclasses.CustomAnnotation(something = \"annotation string\")", ctCatchVariable.getAnnotations().get(0).toString());
	}

	@Test
	public void testCatchExpressionAnnotation() {
		// contract: annotations should be attached to CtCatchVariable and not to CtCatch itself.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationCatchExpression.java");
		launcher.addInputResource("./src/test/java/spoon/test/annotation/testclasses/CustomAnnotation.java");
		CtModel model = launcher.buildModel();
		CtClass<?> clazz = model.getElements(new NamedElementFilter<>(CtClass.class, "AnnotationCatchExpression")).get(0);

		//Annotated CatchVariable with multiple type are properly attached
		CtMethod<?> m1 = clazz.getMethodsByName("m1").get(0);
		CtCatch ctCatch = m1.getElements(new TypeFilter<>(CtCatch.class)).get(0);
		CtCatchVariable<?> ctCatchVariable = ctCatch.getParameter();
		assertTrue(ctCatch.getAnnotations().isEmpty());
		assertEquals(1, ctCatchVariable.getAnnotations().size());
		assertEquals("@spoon.test.annotation.testclasses.CustomAnnotation(something = \"annotation string\")", ctCatchVariable.getAnnotations().get(0).toString());

		//Multiple Catch clauses are properly handled too
		CtMethod<?> m2 = clazz.getMethodsByName("m2").get(0);
		CtCatch ctCatch2 = m2.getElements(new TypeFilter<>(CtCatch.class)).get(1);
		CtCatchVariable<?> ctCatchVariable2 = ctCatch2.getParameter();
		assertTrue(ctCatch2.getAnnotations().isEmpty());
		assertEquals(1, ctCatchVariable2.getAnnotations().size());
		assertEquals("@spoon.test.annotation.testclasses.CustomAnnotation(something = \"annotation string\")", ctCatchVariable2.getAnnotations().get(0).toString());

	}
}
