package spoon.testing;

import org.junit.Test;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.testing.processors.FooToBarProcessor;

import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;

public class AbstractAssertTest {
	public static final String PATH = "./src/test/java/spoon/testing/testclasses/";

	@Test
	public void testTransformationWithProcessorInstantiated() throws Exception {
		assertThat(PATH + "Foo.java").withProcessor(new FooToBarProcessor()).isEqualTo(PATH + "Bar.java");
	}

	@Test
	public void testTransformationWithProcessorClass() throws Exception {
		assertThat(PATH + "Foo.java").withProcessor(FooToBarProcessor.class).isEqualTo(PATH + "Bar.java");
	}

	@Test
	public void testTransformationWithProcessorName() throws Exception {
		assertThat(PATH + "Foo.java").withProcessor(FooToBarProcessor.class.getName()).isEqualTo(PATH + "Bar.java");
	}

	@Test
	public void testTransformationFromCtElementWithProcessor() throws Exception {
		class MyProcessor extends AbstractProcessor<CtField<?>> {
			@Override
			public void process(CtField<?> element) {
				element.setSimpleName("j");
			}
		}
		final CtType<CtElementAssertTest> type = buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class);
		assertThat(type.getField("i")).withProcessor(new MyProcessor()).isEqualTo("public int j;");
	}
}
