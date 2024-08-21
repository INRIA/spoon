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

import org.junit.jupiter.api.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtElementAssertTest {
	public int i;

	@Test
	public void testEqualityBetweenTwoCtElement() throws Exception {
		final CtType<CtElementAssertTest> type = buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class);
		final Factory factory = createFactory();
		final CtField<Integer> expected = factory.Core().createField();
		expected.setSimpleName("i");
		expected.setType(factory.Type().integerPrimitiveType());
		expected.addModifier(ModifierKind.PUBLIC);
		CtField<?> f = type.getField("i");
		assertThat(f).isEqualTo(expected);
	}

	@Test
	public void testEqualityBetweenACtElementAndAString() throws Exception {
		final CtType<CtElementAssertTest> type = buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class);
		assertThat(type.getField("i")).isEqualTo("public int i;");
	}

	@Test
	public void testEqualityBetweenTwoCtElementWithTypeDifferent() {
		assertThrows(AssertionError.class, ()-> assertThat(createFactory().Core().createAnnotation()).isEqualTo(createFactory().Core().createBlock()));
	}

	@Test
	public void testEqualityBetweenTwoCtElementWithTheSameSignatureButNotTheSameContent() throws Exception {
		assertThrows(AssertionError.class, ()-> assertThat(buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class)).isEqualTo(createFactory().Class().create(CtElementAssertTest.class.getName())));
	}

	@Test
	public void testEqualityBetweenTwoDifferentCtElement() throws Exception {
		class String {
		}
		final Factory build = buildNoClasspath(CtElementAssertTest.class);
		final CtFieldAccess<Class<String>> actual = build.Code().createClassAccess(build.Type().<String>get(String.class).getReference());
		final CtFieldAccess<Class<java.lang.String>> expected = createFactory().Code().createClassAccess(createFactory().Type().stringType());
		assertThrows(AssertionError.class, ()-> assertThat(actual).isEqualTo(expected));
	}
}
