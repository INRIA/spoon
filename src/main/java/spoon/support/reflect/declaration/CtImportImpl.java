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
package spoon.support.reflect.declaration;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtImportImpl extends CtNamedElementImpl implements CtImport {
	@MetamodelPropertyField(role = CtRole.IMPORT_KIND)
	private CtImportKind importKind;

	@MetamodelPropertyField(role = CtRole.IMPORT_REFERENCE)
	private CtReference localReference;

	public CtImportImpl() {
		super();
	}

	@Override
	public <T extends CtImport> T setImportKind(CtImportKind importKind) {
		assertCompatibilityImportKindReference(importKind, this.localReference);
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IMPORT_KIND, importKind, this.importKind);
		this.importKind = importKind;
		return (T) this;
	}

	@Override
	public CtImportKind getImportKind() {
		return this.importKind;
	}

	@Override
	public <T extends CtImport> T setReference(CtReference reference) {
		assertCompatibilityImportKindReference(this.importKind, reference);
		if (reference != null) {
			reference.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IMPORT_REFERENCE, reference, this.localReference);
		this.localReference = reference;
		return (T) this;
	}

	@Override
	public CtReference getReference() {
		return this.localReference;
	}

	private void assertCompatibilityImportKindReference(CtImportKind importKind, CtReference reference) {
		if (importKind != null && reference != null) {
			if (reference instanceof CtExecutableReference && importKind != CtImportKind.METHOD) {
				throw new SpoonException("ImportError: You can only have an executable reference for a static method import.");
			} else if (reference instanceof CtFieldReference && importKind != CtImportKind.FIELD) {
				throw new SpoonException("ImportError: You can only have a field reference for a static field import.");
			} else if (reference instanceof CtPackageReference && importKind != CtImportKind.ALL_TYPES) {
				throw new SpoonException("ImportError: You can only have a package reference for a package.* import.");
			} else if (reference instanceof CtTypeReference && importKind != CtImportKind.TYPE && importKind != CtImportKind.ALL_STATIC_MEMBERS) {
				throw new SpoonException("ImportError: You can only have a type reference for a type import or a static type.* import.");
			}
		}
	}

	@Override
	public String getSimpleName() {
		if (this.localReference == null || this.importKind == null) {
			return null;
		}

		String s = this.localReference.getSimpleName();

		if (importKind == CtImportKind.ALL_STATIC_MEMBERS || importKind == CtImportKind.ALL_TYPES) {
			return s + ".*";
		} else {
			return s;
		}
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImport(this);
	}

	@Override
	public CtImport clone() {
		return (CtImport) super.clone();
	}
}
