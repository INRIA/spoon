/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtPackage}.
 */
public interface CtPackageReference extends CtReference {
	/**
	 * Gets the package element when available in the source code.
	 */
	CtPackage getDeclaration();

	/**
	 * Gets the package element when available in the class path.
	 */
	Package getActualPackage();

	/**
	 * Replace a package reference by another one.
	 */
	void replace(CtPackageReference packageReference);
}
