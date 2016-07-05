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
package spoon.testing.utils;

import spoon.reflect.declaration.CtElement;

import java.io.File;

public final class Check {
	private Check() {
		throw new AssertionError();
	}

	public static <T> T assertNotNull(T reference) {
		if (reference == null) {
			throw new AssertionError("Your parameter can't be null.");
		}
		return reference;
	}

	public static <T extends File> T assertExists(T file) {
		if (!file.exists()) {
			throw new AssertionError("You should specify an existing file.");
		}
		return file;
	}

	public static <A extends CtElement, E extends CtElement> A assertIsSame(A actual, E expected) {
		assertNotNull(actual);
		assertNotNull(expected);
		if (!actual.getClass().equals(expected.getClass())) {
			throw new AssertionError(String.format("Actual value is typed by %1$s and expected is typed by %2$s, these objects should be the same type.", actual.getClass().getName(), expected.getClass().getName()));
		}
		return actual;
	}
}
