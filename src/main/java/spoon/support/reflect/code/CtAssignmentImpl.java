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

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtAssignmentImpl<T, A extends T> extends CtStatementImpl implements
		CtAssignment<T, A> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> assigned;

	CtExpression<A> assignment;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> typeCasts = new ArrayList<CtTypeReference<?>>();

	public void accept(CtVisitor visitor) {
		visitor.visitCtAssignment(this);
	}

	public CtExpression<T> getAssigned() {
		return assigned;
	}

	public CtExpression<A> getAssignment() {
		return assignment;
	}

	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

	public CtTypeReference<T> getType() {
		return type;
	}

	public List<CtTypeReference<?>> getTypeCasts() {
		return typeCasts;
	}

	public T S() {
		return null;
	}

	public void setAssigned(CtExpression<T> assigned) {
		this.assigned = assigned;
		assigned.setParent(this);
	}

	public void setAssignment(CtExpression<A> assignment) {
		this.assignment = assignment;
		assignment.setParent(this);
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	public void setTypeCasts(List<CtTypeReference<?>> casts) {
		this.typeCasts = casts;
	}

}
