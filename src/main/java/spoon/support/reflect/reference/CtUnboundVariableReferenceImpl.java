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

import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.visitor.CtVisitor;

/** represents a reference to an unbound field (used when no full classpath is available */
public class CtUnboundVariableReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtUnboundVariableReference<T> {
	private static final long serialVersionUID = -932423216089690817L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtUnboundVariableReference(this);
	}

	@Override
	public CtUnboundVariableReference<T> clone() {
		return (CtUnboundVariableReference<T>) super.clone();
	}
}
