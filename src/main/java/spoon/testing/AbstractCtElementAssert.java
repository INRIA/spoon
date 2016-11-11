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

		if (!actual.equals(expected)) {
			throw new AssertionError();
		}
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
