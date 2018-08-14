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
