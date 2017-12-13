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
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;


/**
 * The default scanner in Spoon: it uses fully qualified name everywhere possible,
 * except when there is a name collision which can not be resolved in other way except doing an import.
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
		super();
	}

	/**
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtReference ref) {
		if (ref instanceof CtTypeReference) {
			CtTypeReference ctTypeReference = (CtTypeReference) ref;
			String fqn = ctTypeReference.getQualifiedName();
			return this.fqnCollideWithTypeMembers(fqn);
		} else if (ref instanceof CtFieldReference) {
			CtFieldReference fieldReference = (CtFieldReference) ref;
			String fqn = fieldReference.getQualifiedName();
			return this.fqnCollideWithTypeMembers(fqn);
		} else if (ref instanceof CtExecutableReference) {
			CtExecutableReference executableReference = (CtExecutableReference) ref;
			if (executableReference.getDeclaringType() != null) {
				String fqn = executableReference.getDeclaringType().getQualifiedName();
				return this.fqnCollideWithTypeMembers(fqn);
			}
		}

		return false;
	}

	private boolean fqnCollideWithTypeMembers(String fqn) {
		String[] splitFQN = fqn.split("\\.");

		// we cannot print in FQN if there is a collision with the first package name
		// BUT we cannot print without FQN if the last name collide with a variable
		return targetTypeNames.contains(splitFQN[0]) && !targetTypeNames.contains(splitFQN[splitFQN.length - 1]);
	}

	@Override
	public void addImport(CtReference reference) {
		if (!reference.equals(targetType) && this.shouldTypeBeImported(reference)) {
			this.addImport(this.getFactory().Type().createImport(reference));
		}
	}

	@Override
	public boolean printQualifiedName(CtReference reference) {
		if (this.isEffectivelyImported(reference)) {
			return false;
		}

		if (this.isTypeInCollision(reference)) {
			return false;
		}

		/*if (reference instanceof CtFieldReference) {
			CtFieldReference fieldReference = (CtFieldReference) reference;
			String fqn = fieldReference.getQualifiedName();
			return !this.fqnCollideWithTypeMembers(fqn);
		}
		if (reference instanceof CtExecutableReference) {
			CtExecutableReference executableReference = (CtExecutableReference) reference;
			if (executableReference.getDeclaringType() != null) {
				String fqn = executableReference.getDeclaringType().getQualifiedName();
				return !this.fqnCollideWithTypeMembers(fqn);
			}
		}*/
		return true;
	}
}
