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
import spoon.support.reflect.reference.CtWildcardStaticTypeMemberReferenceImpl;

public class CtImportImpl extends CtElementImpl implements CtImport {
	@MetamodelPropertyField(role = CtRole.IMPORT_REFERENCE)
	private CtReference localReference;

	public CtImportImpl() {
	}

	@Override
	public CtImportKind getImportKind() {
		if (this.localReference == null) {
			return null;
		}

		if (this.localReference instanceof CtFieldReference) {
			return CtImportKind.FIELD;
		} else if (this.localReference instanceof CtExecutableReference) {
			return CtImportKind.METHOD;
		} else if (this.localReference instanceof CtPackageReference) {
			return CtImportKind.ALL_TYPES;
		} else if (this.localReference instanceof CtWildcardStaticTypeMemberReferenceImpl) {
			return CtImportKind.ALL_STATIC_MEMBERS;
		} else if (this.localReference instanceof CtTypeReference) {
			return CtImportKind.TYPE;
		} else {
			throw new SpoonException("Only CtFieldReference, CtExecutableReference, CtPackageReference and CtTypeReference are accepted reference types.");
		}
	}

	@Override
	public <T extends CtImport> T setReference(CtReference reference) {
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

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImport(this);
	}

	@Override
	public CtImport clone() {
		return (CtImport) super.clone();
	}
}
