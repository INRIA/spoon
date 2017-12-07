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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * The default scanner in Spoon: it uses fully qualified name everywhere possible,
 * except when there is a name collision which can not be resolved in other way except doing an import.
 */
public class MinimalImportScanner extends ImportScannerImpl implements ImportScanner {

	private Set<String> scopedNames = CtElementImpl.emptySet();

	public MinimalImportScanner(Factory factory) {
		super(factory);
	}

	/**
	 * @deprecated Use constructor with parameter factory instead
	 */
	@Deprecated
	public MinimalImportScanner() {

	}

	@Override
	public void enter(CtElement element) {
		if (element instanceof CtNamedElement) {
			if (this.scopedNames == CtElementImpl.<String>emptySet()) {
				this.scopedNames = new HashSet<>();
			}
			this.scopedNames.add(((CtNamedElement) element).getSimpleName());
		}
	}

	@Override
	public void exit(CtElement element) {
		if (element instanceof CtNamedElement) {
			this.scopedNames.remove(((CtNamedElement) element).getSimpleName());
		}
	}

	/**
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtReference ref) {
		if (ref instanceof CtTypeReference) {
			CtTypeReference ctTypeReference = (CtTypeReference) ref;
			String[] splitName = ctTypeReference.getQualifiedName().split("\\.");
			if (this.scopedNames.contains(splitName[0])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void addImport(CtReference reference) {
		if (!reference.equals(targetType) && this.shouldTypeBeImported(reference)) {
			this.addImport(this.getFactory().Type().createImport(reference));
		}
	}

	@Override
	public boolean printQualifiedName(CtReference reference) {
		return !this.isEffectivelyImported(reference);
	}
}
