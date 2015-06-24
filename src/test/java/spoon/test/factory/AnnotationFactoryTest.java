package spoon.test.factory;

import static spoon.test.TestUtils.build;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.AnnotationFactory;

public class AnnotationFactoryTest {

	@Test
	public void testAnnotate() throws Exception {
				
		CtClass<?> type = build("spoon.test", "SampleClass");

		AnnotationFactory af = type.getFactory().Annotation();
		af.annotate(type,SampleAnnotation.class,"names",new String[]{"foo","bar"});

		SampleAnnotation annot = type.getAnnotation(SampleAnnotation.class);
		Assert.assertEquals(2,annot.names().length);
		Assert.assertEquals("foo",annot.names()[0]);
		Assert.assertEquals("bar",annot.names()[1]);
	}	
}

@interface SampleAnnotation {
	String[] names();
}
