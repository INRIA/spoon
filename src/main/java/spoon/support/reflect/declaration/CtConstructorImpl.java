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

package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtConstructorImpl<T> extends CtExecutableImpl<T> implements
		CtConstructor<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void setSimpleName(String simpleName) {
		throw new RuntimeException("Operation not allowed");
	}

	@Override
	public String getSimpleName() {
		return "<init>";
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtConstructor(this);
	}

	// public CtType<T> getDeclaringType() {
	// return super.getDeclaringType();
	// }

	@SuppressWarnings("unchecked")
	public CtType<T> getDeclaringType() {
		return (CtType<T>) parent;
	}

	public CtTypeReference<T> getType() {
		if (getDeclaringType() == null)
			return null;
		return getDeclaringType().getReference();
	}

	public void setType(CtTypeReference<T> type) {
	}

}
