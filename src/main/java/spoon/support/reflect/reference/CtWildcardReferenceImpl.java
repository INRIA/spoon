/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;

public class CtWildcardReferenceImpl extends CtTypeParameterReferenceImpl implements CtWildcardReference {
	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtWildcardReference(this);
	}

	public CtWildcardReferenceImpl() {
		simplename = "?";
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		return (T) this;
	}

	@Override
	public CtWildcardReference clone() {
		return (CtWildcardReference) super.clone();
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		return getFactory().Type().get(Object.class);
	}

}
