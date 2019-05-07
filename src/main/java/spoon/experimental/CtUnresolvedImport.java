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
package spoon.experimental;

import spoon.SpoonException;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
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

	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImport(this);
	}

	@Override
	public CtUnresolvedImport clone() {
		return (CtUnresolvedImport) super.clone();
	}
}
