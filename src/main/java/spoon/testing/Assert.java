/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	private Assert() { }
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
