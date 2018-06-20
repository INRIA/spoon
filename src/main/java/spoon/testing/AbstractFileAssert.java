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

import java.io.File;
import java.util.List;

import static spoon.testing.utils.Check.assertExists;
import static spoon.testing.utils.Check.assertNotNull;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ProcessorUtils.process;

public abstract class AbstractFileAssert<T extends AbstractFileAssert<T>> extends AbstractAssert<T, File> {
	public AbstractFileAssert(File actual, Class<?> selfType) {
		super(actual, selfType);
	}

	/**
	 * Verifies that the actual value is equal to the given one.
	 *
	 * @param expected
	 * 		The expected location of source code.
	 * @return {@code this} assertion object.
	 */
	public T isEqualTo(String expected) {
		return isEqualTo(new File(expected));
	}

	/**
	 * Verifies that the actual value is equal to the given one.
	 *
	 * @param expected
	 * 		The expected location of source code.
	 * @return {@code this} assertion object.
	 */
	public T isEqualTo(File expected) {
		assertNotNull(expected);
		assertExists(expected);

		final Factory actualFactory = build(actual);
		final Factory expectedFactory = build(expected);

		process(actualFactory, processors);
		final List<CtType<?>> allActual = actualFactory.Type().getAll();
		final List<CtType<?>> allExpected = expectedFactory.Type().getAll();
		for (int i = 0; i < allActual.size(); i++) {
			final CtType<?> currentActual = allActual.get(i);
			final CtType<?> currentExpected = allExpected.get(i);
			if (!currentActual.equals(currentExpected)) {
				throw new AssertionError(String.format("%1$s and %2$s aren't equals.", currentActual.getQualifiedName(), currentExpected.getQualifiedName()));
			}
		}
		return this.myself;
	}
}
