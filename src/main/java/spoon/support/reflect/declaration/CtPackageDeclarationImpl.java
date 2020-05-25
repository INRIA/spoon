/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.visitor.CtVisitor;

public class CtPackageDeclarationImpl extends CtElementImpl implements CtPackageDeclaration {
	private static final long serialVersionUID = 1L;
	@MetamodelPropertyField(role = CtRole.PACKAGE_REF)
	private CtPackageReference reference;

	@Override
	public CtPackageDeclarationImpl setReference(CtPackageReference reference) {
		if (reference != null) {
			reference.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.PACKAGE_REF, reference, this.reference);
		this.reference = reference;
		return this;
	}

	@Override
	public CtPackageReference getReference() {
		return this.reference;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtPackageDeclaration(this);
	}

	@Override
	public CtPackageDeclarationImpl clone() {
		return (CtPackageDeclarationImpl) super.clone();
	}
}
