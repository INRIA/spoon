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

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.Check.assertNotNull;
import static spoon.testing.utils.ProcessorUtils.process;

public abstract class AbstractCtPackageAssert<T extends AbstractCtPackageAssert<T>> extends AbstractAssert<T, CtPackage> {
	protected AbstractCtPackageAssert(CtPackage actual, Class<?> selfType) {
		super(actual, selfType);
	}

	/**
	 * Verifies that the actual value is equal to the given one.
	 *
	 * @param expected
	 * 		The expected package.
	 * @return {@code this} assertion object.
	 */
	public T isEqualTo(CtPackage expected) {
		assertNotNull(expected);

		if (!actual.getSimpleName().equals(expected.getSimpleName())) {
			throw new AssertionError(String.format("The actual package named %1$s isn't equals to the expected package named %2$s", actual.getSimpleName(), expected.getSimpleName()));
		}

		if (processors != null && !processors.isEmpty()) {
			process(actual.getFactory(), processors);
		}

		class TypeComparator implements Comparator<CtType<?>> {
			@Override
			public int compare(CtType<?> o1, CtType<?> o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		}

		final List<CtType<?>> actualTypes = new ArrayList<>(actual.getTypes());
		Collections.sort(actualTypes, new TypeComparator());
		final List<CtType<?>> expectedTypes = new ArrayList<>(expected.getTypes());
		Collections.sort(expectedTypes, new TypeComparator());
		for (int i = 0; i < actual.getTypes().size(); i++) {
			final CtType<?> actualType = actualTypes.get(i);
			final CtType<?> expectedType = expectedTypes.get(i);
			if (!actualType.toString().equals(expectedType.toString())) {
				throw new AssertionError(String.format("%1$s and %2$s aren't equals.", actualType.getShortRepresentation(), expectedType.getShortRepresentation()));
			}
		}

		class PackageComparator implements Comparator<CtPackage> {
			@Override
			public int compare(CtPackage o1, CtPackage o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		}

		final List<CtPackage> actualPackages = new ArrayList<>(actual.getPackages());
		Collections.sort(actualPackages, new PackageComparator());
		final List<CtPackage> expectedPackages = new ArrayList<>(expected.getPackages());
		Collections.sort(expectedPackages, new PackageComparator());

		for (int i = 0; i < actualPackages.size(); i++) {
			final CtPackage actualPackage = actualPackages.get(i);
			final CtPackage expectedPackage = expectedPackages.get(i);
			assertThat(actualPackage).isEqualTo(expectedPackage);
		}

		return this.myself;
	}
}
