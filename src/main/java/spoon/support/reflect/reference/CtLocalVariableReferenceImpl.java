/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
