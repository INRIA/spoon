package spoon.test.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class AnnotationTest {

	@Test
	public void testModelBuildingAnnotationBound() throws Exception {
		CtSimpleType<?> type = build("spoon.test.annotation", "Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());
	}	@Test
	
	public void testWritingAnnotParamArray() throws Exception {
		CtSimpleType<?> type = build("spoon.test.annotation", "AnnotParam");
		assertEquals("@java.lang.SuppressWarnings(value = { \"unused\" , \"rawtypes\" })" + DefaultJavaPrettyPrinter.LINE_SEPARATOR, type.getElements(new TypeFilter<>(CtAnnotation.class)).get(0).toString());
	}

	@Test
	public void testModelBuildingAnnotationBoundUsage() throws Exception {
		// we can not use TestUtils.build because we need to compile two classes
		// at the same time
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/annotation/AnnotParamTypes.java",
						"./src/test/java/spoon/test/annotation/Bound.java",
						"./src/test/java/spoon/test/annotation/Main.java"))
				.build();

		CtSimpleType<?> type = factory.Package().get("spoon.test.annotation")
				.getType("Main");

		assertEquals("Main", type.getSimpleName());
		CtParameter<?> param = type.getElements(
				new TypeFilter<CtParameter<?>>(CtParameter.class)).get(0);
		assertEquals("a", param.getSimpleName());
		List<CtAnnotation<? extends Annotation>> annotations = param
				.getAnnotations();
		CtAnnotation<?> a = annotations.toArray(new CtAnnotation[0])[0];
		assertEquals(1, annotations.size());
		Bound actualAnnotation = (Bound) a.getActualAnnotation();
		assertEquals(8, actualAnnotation.max());
	}

	@Test
	public void testPersistenceProperty() throws Exception {
		CtSimpleType<?> type = build("spoon.test.annotation",
				"PersistenceProperty");
		assertEquals("PersistenceProperty", type.getSimpleName());
		assertEquals(2, type.getAnnotations().size());
		CtAnnotation<Target> a1 = type.getAnnotation(type.getFactory().Type()
				.createReference(Target.class));
		assertNotNull(a1);
		CtAnnotation<Retention> a2 = type.getAnnotation(type.getFactory()
				.Type().createReference(Retention.class));
		assertNotNull(a2);
		assertTrue(a1.getElementValues().containsKey("value"));
		assertTrue(a2.getElementValues().containsKey("value"));
	}

	
	@Test
	public void testAnnotationParameterTypes() throws Exception {
		// we can not use TestUtils.build because we need to compile two classes
		// at the same time
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/annotation/AnnotParamTypes.java",
						"./src/test/java/spoon/test/annotation/Bound.java",
						"./src/test/java/spoon/test/annotation/Main.java"))
				.build();

		CtSimpleType<?> type = factory.Package().get("spoon.test.annotation")
				.getType("Main");
		
		CtMethod<?> m1 = type.getElements(
				new NameFilter<CtMethod<?>>("m1")).get(0);
		List<CtAnnotation<? extends Annotation>> annotations = m1
				.getAnnotations();
		CtAnnotation<?> a = annotations.toArray(new CtAnnotation[0])[0];
		AnnotParamTypes annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42,annot.integer());
		assertEquals(1,annot.integers().length);
		assertEquals("Hello World!",annot.string());
		assertEquals(2,annot.strings().length);
		assertEquals(Integer.class,annot.clazz());
		assertEquals(2,annot.classes().length);
		assertEquals(true,annot.b());
		assertEquals('c',annot.c());
		assertEquals(42,annot.byt());
		assertEquals((short)42,annot.s());
		assertEquals(42,annot.l());
		assertEquals(3.14f,annot.f(),0f);
		assertEquals(3.14159,annot.d(),0);
		
		CtMethod<?> m2 = type.getElements(
				new NameFilter<CtMethod<?>>("m2")).get(0);
		annotations = m2.getAnnotations();
		a = annotations.toArray(new CtAnnotation[0])[0];
		annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42,annot.integer());
		assertEquals(1,annot.integers().length);
		assertEquals("Hello World!",annot.string());
		assertEquals(2,annot.strings().length);
		assertEquals(false,annot.b());
		assertEquals(42,annot.byt());
		assertEquals((short)42,annot.s());
		assertEquals(42,annot.l());
		assertEquals(3.14f,annot.f(),0f);
		assertEquals(3.14159,annot.d(),0);
	}
}
