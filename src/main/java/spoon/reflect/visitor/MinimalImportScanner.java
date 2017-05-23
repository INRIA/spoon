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

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * A scanner dedicated to import only the necessary packages, @see spoon.test.variable.AccessFullyQualifiedTest
 *
 */
public class MinimalImportScanner extends ImportScannerImpl implements ImportScanner {

	/**
	 * This method use @link{ImportScannerImpl#isTypeInCollision} to import a ref only if there is a collision
	 * @param ref: the type we are testing, it can be a CtTypeReference, a CtFieldReference or a CtExecutableReference
	 *
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtReference ref) {
		// we import the targetType by default to simplify and avoid conclict in inner classes
		if (ref.equals(targetType)) {
			return true;
		}

		return isTypeInCollision(ref, true);
	}

	@Override
	protected boolean addClassImport(CtTypeReference<?> ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			return super.addClassImport(ref);
		} else {
			return false;
		}
	}

	@Override
	protected boolean addFieldImport(CtFieldReference ref) {
		if (ref.getDeclaringType() != null) {
			if (isImportedInClassImports(ref.getDeclaringType())) {
				return false;
			}
		}

		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			if (this.fieldImports.containsKey(ref.getSimpleName())) {
				return isImportedInFieldImports(ref);
			}

			fieldImports.put(ref.getSimpleName(), ref);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean addMethodImport(CtExecutableReference ref) {
		if (ref.getDeclaringType() != null) {
			if (isImportedInClassImports(ref.getDeclaringType())) {
				return false;
			}
		}
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			if (this.methodImports.containsKey(ref.getSimpleName())) {
				return isImportedInMethodImports(ref);
			}

			methodImports.put(ref.getSimpleName(), ref);

			if (ref.getDeclaringType() != null) {
				if (ref.getDeclaringType().getPackage() != null) {
					if (ref.getDeclaringType().getPackage().equals(this.targetType.getPackage())) {
						addClassImport(ref.getDeclaringType());
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isImportedInClassImports(CtTypeReference<?> ref) {
		if (!(ref.isImplicit()) && classImports.containsKey(ref.getSimpleName())) {
			CtTypeReference<?> exist = classImports.get(ref.getSimpleName());
			if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}
}
