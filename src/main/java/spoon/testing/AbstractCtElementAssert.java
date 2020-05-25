/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing;

import spoon.reflect.declaration.CtElement;

import static spoon.testing.utils.Check.assertCtElementEquals;
import static spoon.testing.utils.Check.assertNotNull;
import static spoon.testing.utils.Check.assertIsSame;
import static spoon.testing.utils.ProcessorUtils.process;

public abstract class AbstractCtElementAssert<T extends AbstractCtElementAssert<T>> extends AbstractAssert<T, CtElement> {
	protected AbstractCtElementAssert(CtElement actual, Class<?> selfType) {
		super(actual, selfType);
	}

	/**
	 * Verifies that the actual value is equal to the given one.
	 *
	 * @param expected
	 * 		The expected element.
	 * @return {@code this} assertion object.
	 */
	public T isEqualTo(CtElement expected) {
		assertNotNull(expected);
		assertIsSame(actual, expected);

		process(actual.getFactory(), processors);

		// using the new method to have a nice error message
		assertCtElementEquals(expected, actual);

		return this.myself;
	}

	/**
	 * Verifies that the actual value is equal to the given one.
	 *
	 * @param expected
	 * 		The expected render of the element.
	 * @return {@code this} assertion object.
	 */
	public T isEqualTo(String expected) {
		assertNotNull(expected);

		process(actual.getFactory(), processors);

		if (!actual.toString().equals(expected)) {
			throw new AssertionError(String.format("%1$s and %2$s aren't equals.", actual.getShortRepresentation(), expected));
		}
		return this.myself;
	}
}
