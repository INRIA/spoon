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
package spoon.support.reflect.code;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;

public class CtLiteralImpl<T extends Object> extends CtExpressionImpl<T> implements CtLiteral<T> {
	private static final long serialVersionUID = 1L;

	T value;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLiteral(this);
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public <C extends CtLiteral<T>> C setValue(T value) {
		this.value = value;
		if (this.value instanceof CtElement) {
			((CtElement) this.value).setParent(this);
		}
		return (C) this;
	}

	@Override
	public CtLiteral<T> clone() {
		return (CtLiteral<T>) super.clone();
	}
}
