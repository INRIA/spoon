/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.experimental;

import spoon.SpoonException;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtImportVisitor;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtUnresolvedImport extends CtElementImpl implements CtImport {

	public CtUnresolvedImport() {
	}

	private boolean isStatic;

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isStatic() {
		return isStatic;
	}

	private String unresolvedReference;

	public void setUnresolvedReference(String reference) {
		this.unresolvedReference = reference;
	}

	public String getUnresolvedReference() {
		return unresolvedReference;
	}

	@Override
	public CtImportKind getImportKind() {
		return CtImportKind.UNRESOLVED;
	}

	@Override
	public CtReference getReference() {
		return null;
	}

	@Override
	public <T extends CtImport> T setReference(CtReference reference) {
		throw new SpoonException("UnrseolvedImport reference cannot be set. Use CtImportImpl instead");
	}

	@Override
	public void accept(CtImportVisitor visitor) {
		visitor.visitUnresolvedImport(this);
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImport(this);
	}

	@Override
	public CtUnresolvedImport clone() {
		Factory factory = getFactory();
		return (CtUnresolvedImport) factory.createUnresolvedImport(unresolvedReference, isStatic);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof CtUnresolvedImport)) {
			return false;
		}
		CtUnresolvedImport other = (CtUnresolvedImport) o;
		return other.isStatic() == isStatic && other.getUnresolvedReference().equals(unresolvedReference);
	}

	@Override
	public int hashCode() {
		return unresolvedReference.hashCode() + (isStatic ? 1 : 0);
	}
}
