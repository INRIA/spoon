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
	public void testTransformationWithProcessorInstantiated() {
		assertThat(PATH + "Foo.java").withProcessor(new FooToBarProcessor()).isEqualTo(PATH + "Bar.java");
	}

	@Test
	public void testTransformationWithProcessorClass() {
		assertThat(PATH + "Foo.java").withProcessor(FooToBarProcessor.class).isEqualTo(PATH + "Bar.java");
	}

	@Test
	public void testTransformationWithProcessorName() {
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
