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
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASTS_CONTAINER_DEFAULT_CAPACITY;

public class CtAssignmentImpl<T, A extends T> extends CtStatementImpl implements CtAssignment<T, A> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> assigned;

	CtExpression<A> assignment;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> typeCasts = EMPTY_LIST();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtAssignment(this);
	}

	@Override
	public CtExpression<T> getAssigned() {
		return assigned;
	}

	@Override
	public CtExpression<A> getAssignment() {
		return assignment;
	}

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public List<CtTypeReference<?>> getTypeCasts() {
		return typeCasts;
	}

	@Override
	public <C extends CtAssignment<T, A>> C setAssigned(CtExpression<T> assigned) {
		assigned.setParent(this);
		this.assigned = assigned;
		return (C) this;
	}

	@Override
	public <C extends CtRHSReceiver<A>> C setAssignment(CtExpression<A> assignment) {
		assignment.setParent(this);
		this.assignment = assignment;
		return (C) this;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		this.type = type;
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C setTypeCasts(List<CtTypeReference<?>> casts) {
		this.typeCasts = casts;
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C addTypeCast(CtTypeReference<?> type) {
		if (typeCasts == CtElementImpl.<CtTypeReference<?>>EMPTY_LIST()) {
			typeCasts = new ArrayList<CtTypeReference<?>>(CASTS_CONTAINER_DEFAULT_CAPACITY);
		}
		typeCasts.add(type);
		return (C) this;
	}

	@Override
	public <E extends T> void replace(CtExpression<E> element) {
		replace((CtElement) element);
	}

	@Override
	public T S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return getFactory().Core().clone(this);
	}
}
