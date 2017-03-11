/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;

/**
 * An implementation for {@link CtLocalVariableReference}.
 */
public class CtLocalVariableReferenceImpl<T>
		extends CtVariableReferenceImpl<T> implements CtLocalVariableReference<T> {

	/**
	 * Id for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public CtLocalVariableReferenceImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLocalVariableReference(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CtLocalVariable<T> getDeclaration() {
		// without a factory, we are not able to filter for local variables
		final Factory factory = getFactory();
		if (factory == null) {
			return null;
		}

		final String simpleName = getSimpleName();

		//handle the CtLocalVariableReference which were created by CtLocalVariable#getReference() and which are not yet part of model, so we cannot found them using standard rules
		if (parent instanceof CtLocalVariable) {
			CtLocalVariable<T> var = (CtLocalVariable<T>) parent;
			if (simpleName.equals(var.getSimpleName())) {
				return var;
			}
		}
		try {
			// successively iterate through all parents of this reference and
			// return first result (which must be the closest declaration
			// respecting visible scope)
			CtVariable<?> var = map(new PotentialVariableDeclarationFunction(simpleName)).first();
			if (var instanceof CtLocalVariable) {
				return (CtLocalVariable<T>) var;
			}
			if (var != null) {
				//we have found another variable declaration with same simple name, which hides declaration of this local variable reference
				//handle it as not found
				return null;
			}
		} catch (ParentNotInitializedException e) {
			// handle this case as 'not found'
		}
		return null;
	}

	@Override
	public CtLocalVariableReference<T> clone() {
		return (CtLocalVariableReference<T>) super.clone();
	}
}
