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

package spoon.support.reflect.code;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtVariableAccessImpl<T> extends CtExpressionImpl<T> implements
		CtVariableAccess<T> {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> casts = new ArrayList<CtTypeReference<?>>();

	CtTypeReference<T> type;

	CtVariableReference<T> variable;

	public void accept(CtVisitor visitor) {
		visitor.visitCtVariableAccess(this);
	}

	@Override
	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public List<CtTypeReference<?>> getTypeCasts() {
		return casts;
	}

	public CtVariableReference<T> getVariable() {
		return variable;
	}

	@Override
	public T S() {
		return null;
	}

	@Override
	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	@Override
	public void setTypeCasts(List<CtTypeReference<?>> casts) {
		this.casts = casts;
	}

	public void setVariable(CtVariableReference<T> variable) {
		this.variable = variable;
	}

}
