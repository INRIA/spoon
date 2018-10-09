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

import static spoon.testing.Assert.assertThat;

public class FileAssertTest {
	public static final String PATH = "./src/test/java/spoon/testing/testclasses/";

	@Test
	public void testEqualsBetweenTwoSameFile() {
		final String actual = PATH + "Foo.java";
		assertThat(actual).isEqualTo(actual);
	}

	@Test(expected = AssertionError.class)
	public void testEqualsBetweenTwoDifferentFile() {
		assertThat(PATH + "Foo.java").isEqualTo(PATH + "Bar.java");
	}
}
