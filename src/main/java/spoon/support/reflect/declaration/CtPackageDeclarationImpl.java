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
