/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.reflect.AnnotatedElement;

public class CtPackageReferenceImpl extends CtReferenceImpl
		implements CtPackageReference {
	private static final long serialVersionUID = 1L;

	public CtPackageReferenceImpl() {
		super();
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
	public void replace(CtPackageReference packageReference) {
		super.replace(packageReference);
	}

	@Override
	protected AnnotatedElement getActualAnnotatedElement() {
		return getActualPackage();
	}

}
