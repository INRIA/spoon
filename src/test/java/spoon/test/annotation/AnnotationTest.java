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

import spoon.Spoon;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.filter.TypeFilter;

public class AnnotationTest {

	@Test
	public void testModelBuildingAnnotationBound() throws Exception {
		CtSimpleType<?> type = build("spoon.test.annotation", "Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());
	}

	@Test
	public void testModelBuildingAnnotationBoundUsage() throws Exception {
		// we can not use TestUtils.build because we need to compile two classes
		// at the same time
		Factory factory = Spoon.createFactory();
		Spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/annotation/Bound.java",
						"./src/test/java/spoon/test/annotation/Main.java"))
				.build();

		CtSimpleType<?> type = factory.Package().get("spoon.test.annotation")
				.getType("Main");

		assertEquals("Main", type.getSimpleName());
		CtParameter<?> param = (CtParameter<?>) type.getElements(
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

}
