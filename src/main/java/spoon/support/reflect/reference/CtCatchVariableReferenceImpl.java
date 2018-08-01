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
package spoon.support.reflect.reference;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtCatchVariableReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtCatchVariableReference<T> {
	private static final long serialVersionUID = 1L;

	public CtCatchVariableReferenceImpl() {
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariableReference(this);
	}

	@Override
	public CtCatchVariable<T> getDeclaration() {
		CtElement element = this;
		String name = getSimpleName();
		CtCatchVariable var;
		try {
			do {
				CtCatch catchBlock = element.getParent(CtCatch.class);
				if (catchBlock == null) {
					return null;
				}
				var = catchBlock.getParameter();
				element = catchBlock;
			} while (!name.equals(var.getSimpleName()));
		} catch (ParentNotInitializedException e) {
			return null;
		}
		return var;
	}

	@Override
	public CtCatchVariableReference<T> clone() {
		return (CtCatchVariableReference<T>) super.clone();
	}
}
