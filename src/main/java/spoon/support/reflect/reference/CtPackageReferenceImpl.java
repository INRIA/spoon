/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.reflect.AnnotatedElement;

public class CtPackageReferenceImpl extends CtReferenceImpl implements CtPackageReference {
	private static final long serialVersionUID = 1L;

	public CtPackageReferenceImpl() {
	}

	@Override
	public CtPackage getDeclaration() {
		return getFactory().Package().get(getSimpleName());
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtPackageReference(this);
	}

	@Override
	public Package getActualPackage() {
		return Package.getPackage(getSimpleName());
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return getActualPackage();
	}

	@Override
	public CtPackageReference clone() {
		return (CtPackageReference) super.clone();
	}

	@Override
	public String getQualifiedName() {
		return this.getSimpleName();
	}

	@Override
	public boolean isUnnamedPackage() {
		return getSimpleName().isEmpty();
	}
}
