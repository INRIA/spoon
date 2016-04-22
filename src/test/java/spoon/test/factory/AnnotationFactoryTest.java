package spoon.test.factory;

import org.junit.Test;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.AnnotationFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class AnnotationFactoryTest {

	@Test
	public void testAnnotate() throws Exception {

		CtClass<?> type = build("spoon.test", "SampleClass");

		AnnotationFactory af = type.getFactory().Annotation();
		af.annotate(type,SampleAnnotation.class,"names",new String[]{"foo","bar"});

		final CtAnnotation<SampleAnnotation> annotation = type.getAnnotation(type.getFactory().Annotation().createReference(SampleAnnotation.class));
		assertTrue(annotation.getValue("names") instanceof CtNewArray);
		final CtNewArray names = annotation.getValue("names");
		assertEquals(2, names.getElements().size());
		assertEquals("foo", ((CtLiteral) names.getElements().get(0)).getValue());
		assertEquals("bar", ((CtLiteral) names.getElements().get(1)).getValue());
	}
}

@interface SampleAnnotation {
	String[] names();
}
