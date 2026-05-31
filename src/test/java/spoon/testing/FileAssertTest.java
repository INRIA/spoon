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

import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.testing.utils.ModelTest;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static spoon.testing.assertions.SpoonAssertions.assertThat;
import static spoon.testing.utils.ModelUtils.build;

public class FileAssertTest {
	public static final String PATH = "./src/test/java/spoon/testing/testclasses/";

	@ModelTest(PATH + "Foo.java")
	public void testEqualsBetweenTwoSameFile(Factory factory) {
		for (CtType<?> type : factory.Type().getAll()) {
			assertThat(type).isEqualTo(type);
		}
	}

	@ModelTest(PATH + "Foo.java")
	public void testEqualsBetweenTwoDifferentFile(Factory fooFactory) {
		Factory barFactory = build(new File(PATH + "Bar.java"));
		assertThatThrownBy(() -> {
			for (CtType<?> fooType : fooFactory.Type().getAll()) {
				for (CtType<?> barType : barFactory.Type().getAll()) {
					assertThat(fooType).isEqualTo(barType);
				}
			}
		}).isInstanceOf(AssertionError.class);
	}
}
