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
package spoon.support.reflect.reference;

import spoon.reflect.reference.CtImport;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.ImportKind;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtImportImpl extends CtElementImpl implements CtImport {
	private ImportKind importKind;
	private CtReference localReference;

	public CtImportImpl() {
		super();
	}

	@Override
	public <T extends CtImport> T setKindImport(ImportKind importKind) {
		this.importKind = importKind;
		return (T) this;
	}

	@Override
	public ImportKind getKindImport() {
		return this.importKind;
	}

	@Override
	public <T extends CtImport> T setReference(CtReference reference) {
		this.localReference = reference;
		return (T) this;
	}

	@Override
	public CtReference getReference() {
		return this.localReference;
	}

	@Override
	public String getSimpleName() {
		if (this.localReference == null) {
			return null;
		}

		return this.localReference.getSimpleName();
	}

	@Override
	public void accept(CtVisitor visitor) {

	}
}
