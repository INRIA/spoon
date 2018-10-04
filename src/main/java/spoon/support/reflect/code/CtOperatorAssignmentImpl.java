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
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.OPERATOR_KIND;

public class CtOperatorAssignmentImpl<T, A extends T> extends CtAssignmentImpl<T, A> implements CtOperatorAssignment<T, A> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = OPERATOR_KIND)
	BinaryOperatorKind kind;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtOperatorAssignment(this);
	}

	@Override
	public BinaryOperatorKind getKind() {
		return kind;
	}

	@Override
	public <C extends CtOperatorAssignment<T, A>> C setKind(BinaryOperatorKind kind) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, OPERATOR_KIND, kind, this.kind);
		this.kind = kind;
		return (C) this;
	}

	@Override
	public CtOperatorAssignment<T, A> clone() {
		return (CtOperatorAssignment<T, A>) super.clone();
	}
}
