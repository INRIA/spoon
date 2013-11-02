package spoon.test.annotation;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.lang.annotation.Annotation;
import java.util.Set;

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
		Spoon.createCompiler().build(
				factory,
				SpoonResourceHelper.files(
						"./src/test/java/spoon/test/annotation/Bound.java",
						"./src/test/java/spoon/test/annotation/Main.java"));

		CtSimpleType<?> type = factory.Package().get("spoon.test.annotation")
				.getType("Main");

		assertEquals("Main", type.getSimpleName());
		CtParameter<?> param = (CtParameter<?>) type.getElements(
				new TypeFilter<CtParameter<?>>(CtParameter.class)).get(0);
		assertEquals("a", param.getSimpleName());
		Set<CtAnnotation<? extends Annotation>> annotations = param
				.getAnnotations();
		CtAnnotation<?> a = annotations.toArray(new CtAnnotation[0])[0];
		assertEquals(1, annotations.size());
		Bound actualAnnotation = (Bound) a.getActualAnnotation();
		assertEquals(8, actualAnnotation.max());
	}

}
