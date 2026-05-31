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

import org.assertj.core.api.Assertions;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.testing.processors.FooToBarProcessor;
import spoon.testing.utils.ByClass;
import spoon.testing.utils.ModelTest;
import spoon.testing.utils.ProcessorUtils;

import java.io.File;
import java.util.List;

import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static spoon.testing.utils.ModelUtils.build;

public class AbstractAssertTest {

	@ModelTest("./src/test/java/spoon/testing/testclasses/" + "Foo.java")
	public void testTransformationWithProcessorInstantiated(Factory actual) {
		ProcessorUtils.process(actual, List.of(new FooToBarProcessor()));
		List<CtType<?>> actualTypes = actual.Type().getAll();
		List<CtType<?>> expectedTypes = build(new File("./src/test/java/spoon/testing/testclasses/" + "Bar.java")).Type().getAll();
		Assertions.assertThat(actualTypes).hasSameSizeAs(expectedTypes);
		for (int i = 0; i < actualTypes.size(); i++) {
			assertThat(actualTypes.get(i)).isEqualTo(expectedTypes.get(i));
		}
	}

	@ModelTest("./src/test/java/spoon/testing/testclasses/" + "Foo.java")
	public void testTransformationWithProcessorClass(Factory actual) {
		ProcessorUtils.process(actual, List.of(new FooToBarProcessor()));
		List<CtType<?>> actualTypes = actual.Type().getAll();
		List<CtType<?>> expectedTypes = build(new File("./src/test/java/spoon/testing/testclasses/" + "Bar.java")).Type().getAll();
		Assertions.assertThat(actualTypes).hasSameSizeAs(expectedTypes);
		for (int i = 0; i < actualTypes.size(); i++) {
			assertThat(actualTypes.get(i)).isEqualTo(expectedTypes.get(i));
		}
	}

	@ModelTest("./src/test/java/spoon/testing/testclasses/" + "Foo.java")
	public void testTransformationWithProcessorName(Factory actual) {
		ProcessorUtils.process(actual, List.of(new FooToBarProcessor()));
		List<CtType<?>> actualTypes = actual.Type().getAll();
		List<CtType<?>> expectedTypes = build(new File("./src/test/java/spoon/testing/testclasses/" + "Bar.java")).Type().getAll();
		Assertions.assertThat(actualTypes).hasSameSizeAs(expectedTypes);
		for (int i = 0; i < actualTypes.size(); i++) {
			assertThat(actualTypes.get(i)).isEqualTo(expectedTypes.get(i));
		}
	}

	@ModelTest("./src/test/java/spoon/testing/CtElementAssertTest.java")
	public void testTransformationFromCtElementWithProcessor(
		@ByClass(CtElementAssertTest.class) CtType<CtElementAssertTest> type, Factory factory) throws Exception {
		class MyProcessor extends AbstractProcessor<CtField<?>> {
			@Override
			public void process(CtField<?> element) {
				element.setSimpleName("j");
			}
		}
		CtField<?> field = type.getField("i");
		ProcessorUtils.process(factory, List.of(new MyProcessor()));
		Assertions.assertThat(field.toString()).isEqualTo("public int j;");
	}
}
