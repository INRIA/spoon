/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;

import java.io.File;

import static spoon.testing.utils.Check.assertExists;
import static spoon.testing.utils.Check.assertNotNull;

/**
 * Entry point for assertion methods for different data types.
 * Each method in this class is a static factory for the type-specific
 * assertion objects. The purpose of this class is to make test code
 * more readable.
 */
public class Assert {
	/**
	 * Create a new instance of <code>{@link FileAssert}</code>.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractFileAssert<?> assertThat(String actual) {
		return assertThat(new File(actual));
	}

	/**
	 * Create a new instance of <code>{@link FileAssert}</code>.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractFileAssert<?> assertThat(File actual) {
		assertNotNull(actual);
		assertExists(actual);
		return new FileAssert(actual);
	}

	/**
	 * Create a new instance of <code>{@link CtElementAssert}</code>.
	 * Note that a package typed by CtElement will call this method and
	 * not {@link Assert#assertThat(CtPackage)}.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractCtElementAssert<?> assertThat(CtElement actual) {
		assertNotNull(actual);
		return new CtElementAssert(actual);
	}

	/**
	 * Create a new instance of <code>{@link CtPackageAssert}</code>.
	 * Note that this assert will be make a deep equals with its content
	 * (all types).
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractCtPackageAssert<?> assertThat(CtPackage actual) {
		assertNotNull(actual);
		return new CtPackageAssert(actual);
	}
}
