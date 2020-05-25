/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import spoon.reflect.declaration.CtPackage;
import spoon.support.DerivedProperty;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtPackage}.
 */
public interface CtPackageReference extends CtReference {
	/**
	 * Gets the package element when available in the source code.
	 */
	@Override
	@DerivedProperty
	CtPackage getDeclaration();

	/**
	 * Gets the package element when available in the class path.
	 */
	Package getActualPackage();

	/**
	 * Returns {@code true} if this is a reference to an <i>unnamed</i>
	 * Java package. See JLS §7.4.2. Unnamed Packages.
	 */
	boolean isUnnamedPackage();

	@Override
	CtPackageReference clone();

	/**
	 * The simple name of a CtPackageReference is always the fully qualified name of its referenced package. (see {@link spoon.reflect.factory.PackageFactory})
	 * @return The fully qualified name of its referenced package
	 */
	@Override
	String getSimpleName();

	/**
	 * The qualified name of a CtPackageReference is directly given by its simple name (see {@link CtPackageReference#getSimpleName})
	 * @return the fully qualified name of its referenced package
	 */
	String getQualifiedName();
}
