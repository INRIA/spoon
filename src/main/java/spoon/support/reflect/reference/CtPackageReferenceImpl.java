/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

public class CtPackageReferenceImpl extends CtReferenceImpl implements
		CtPackageReference {
	private static final long serialVersionUID = 1L;

	public CtPackageReferenceImpl() {
		super();
	}

	public CtPackage getDeclaration() {
		return getFactory().Package().get(getSimpleName());
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtPackageReference(this);
	}

	Factory factory;

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

}
