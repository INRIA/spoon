/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.visitor;

import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;

/**
 * A scanner dedicated to import only the necessary packages, @see spoon.test.variable.AccessFullyQualifiedTest
 *
 */
public class MinimalImportScanner extends ImportScannerImpl implements ImportScanner {

	public MinimalImportScanner(Factory factory) {
		super(factory);
	}

	/**
	 * @deprecated Use constructor with parameter factory instead
	 */
	@Deprecated
	public MinimalImportScanner() {

	}

	/**
	 * This method use @link{ImportScannerImpl#isTypeInCollision} to import a ref only if there is a collision
	 * @param ref: the type we are testing, it can be a CtTypeReference, a CtFieldReference or a CtExecutableReference
	 *
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtReference ref) {
		if (ref.equals(targetType)) {
			return true;
		}

		return false;
	}

	@Override
	public void addImport(CtReference reference) {
		if (this.shouldTypeBeImported(reference)) {
			this.addImport(this.factory.Type().createImport(reference));
		}
	}
}
