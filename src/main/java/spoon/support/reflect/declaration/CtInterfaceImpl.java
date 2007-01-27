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

import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtInterfaceImpl<T> extends CtTypeImpl<T> implements CtInterface<T> {
	private static final long serialVersionUID = 1L;

	// @Override
	// public List<CtField<?>> getAllFields() {
	// List<CtField<?>> ret = new ArrayList<CtField<?>>();
	// ret.addAll(getFields());
	// for (CtTypeReference<?> ref : getSuperInterfaces()) {
	// if (ref.getDeclaration() != null) {
	// ret.addAll(ref.getDeclaration().getAllFields());
	// }
	// }
	// return ret;
	// }

	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> ret = new TreeSet<CtMethod<?>>();
		ret.addAll(getMethods());

		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.getDeclaration() != null) {
				CtType<?> t = (CtType<?>) ref.getDeclaration();
				ret.addAll(t.getAllMethods());
			}
		}
		return ret;
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtInterface(this);
	}

	public boolean isSubtypeOf(CtTypeReference<?> type) {
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type))
				return true;
		}
		return false;
	}
}
