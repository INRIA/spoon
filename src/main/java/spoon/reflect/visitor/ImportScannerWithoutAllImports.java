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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import java.util.HashSet;
import java.util.Set;

/**
 * A scanner dedicated to import only the necessary packages, @see spoon.test.variable.AccessFullyQualifiedTest
 *
 */
public class ImportScannerWithoutAllImports extends ImportScannerImpl implements ImportScanner {

	private Set<String> namedElements = new HashSet<String>();

	/**
	 * Test if the reference should be imported by looking if there is a name conflict
	 * @param ref
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtTypeReference<?> ref) {
		// we import the targetType by default to simplify and avoid conclict in inner classes
		if (ref.equals(targetType)) {
			return true;
		}

		try {
			CtElement parent = ref.getParent();

			if (parent instanceof CtNamedElement) {
				namedElements.add(((CtNamedElement) parent).getSimpleName());
			}

			while (!(parent instanceof CtPackage)) {
				if (parent instanceof CtFieldReference) {
					CtFieldReference parentType = (CtFieldReference) parent;
					String qualifiedName = parentType.getQualifiedName();

					String[] splittedName = qualifiedName.split("(\\.|#)");

					for (String token : splittedName) {
						if (namedElements.contains(token)) {
							return true;
						}
					}
				}
				parent = parent.getParent();
			}
		} catch (ParentNotInitializedException e) {
			return false;
		}

		return false;
	}

	@Override
	protected boolean addImport(CtTypeReference<?> ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			return super.addImport(ref);
		} else {
			return false;
		}
	}
}
