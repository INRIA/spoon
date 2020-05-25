/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing.utils;

import spoon.reflect.declaration.CtElement;
import spoon.support.visitor.equals.EqualsVisitor;

import java.io.File;

public final class Check {
	private Check() {
		throw new AssertionError();
	}

	/** if ct1 is not equals to ct2, tells the exact difference */
	public static void assertCtElementEquals(CtElement ct1, CtElement ct2) {
		EqualsVisitor ev = new EqualsVisitor();
		ev.checkEquals(ct1, ct2);
		if (!ev.isEqual()) {
			Object notEqual1 = ev.getNotEqualElement();
			Object notEqual2 = ev.getNotEqualOther();
			throw new AssertionError("elements no equal -- difference:\n"
					+ ev.getNotEqualRole() + "\n"
					+ (notEqual1 instanceof CtElement && ((CtElement) notEqual1).getPosition() != null ? ((CtElement) notEqual1).getPosition().toString() : "<unknown position>") + " \n"
					+ (notEqual1 != null ? notEqual1.toString() : "null")
					+ "\n is not \n"
					+ (notEqual2 != null ? notEqual2.toString() : "null")
			);
		}

	}
	/** throws AssertionError if "reference" is null */
	public static <T> T assertNotNull(String msg, T reference) {
		if (reference == null) {
			throw new AssertionError(msg);
		}
		return reference;
	}

	public static <T> T assertNotNull(T reference) {
		return assertNotNull("Your parameter can't be null.", reference);
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
