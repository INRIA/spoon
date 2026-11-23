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
import org.junit.jupiter.api.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.testing.utils.ByClass;
import spoon.testing.utils.ModelTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtElementAssertTest {
	public int i;

	@ModelTest("./src/test/java/spoon/testing/CtElementAssertTest.java")
	public void testEqualityBetweenTwoCtElement(@ByClass(CtElementAssertTest.class) CtType<CtElementAssertTest> type) throws Exception {
		final Factory factory = createFactory();
		final CtField<Integer> expected = factory.Core().createField();
		expected.setSimpleName("i");
		expected.setType(factory.Type().integerPrimitiveType());
		expected.addModifier(ModifierKind.PUBLIC);
		CtField<?> f = type.getField("i");
		assertThat(f).isEqualTo(expected);
	}

	@ModelTest("./src/test/java/spoon/testing/CtElementAssertTest.java")
	public void testEqualityBetweenACtElementAndAString(@ByClass(CtElementAssertTest.class) CtType<CtElementAssertTest> type) throws Exception {
		Assertions.assertThat(type.getField("i").toString()).isEqualTo("public int i;");
	}

	@Test
	public void testEqualityBetweenTwoCtElementWithTypeDifferent() {
		assertThatThrownBy(() -> assertThat(createFactory().Core().createAnnotation()).isEqualTo(createFactory().Core().createBlock()))
			.isInstanceOf(AssertionError.class);
	}

	@ModelTest("./src/test/java/spoon/testing/CtElementAssertTest.java")
	public void testEqualityBetweenTwoCtElementWithTheSameSignatureButNotTheSameContent(
		@ByClass(CtElementAssertTest.class) CtType<CtElementAssertTest> actual, Factory factory) {
		assertThatThrownBy(() -> assertThat(actual).isEqualTo(factory.Class().create(CtElementAssertTest.class.getName())))
			.isInstanceOf(AssertionError.class);
	}

	@ModelTest("./src/test/java/spoon/testing/CtElementAssertTest.java")
	public void testEqualityBetweenTwoDifferentCtElement(Factory factory) throws Exception {
		class String {
		}
		final CtFieldAccess<Class<String>> actual = factory.Code().createClassAccess(factory.Type().<String>get(String.class).getReference());
		final CtFieldAccess<Class<java.lang.String>> expected = createFactory().Code().createClassAccess(createFactory().Type().stringType());
		assertThatThrownBy(() -> assertThat(actual).isEqualTo(expected))
			.isInstanceOf(AssertionError.class);
	}
}
