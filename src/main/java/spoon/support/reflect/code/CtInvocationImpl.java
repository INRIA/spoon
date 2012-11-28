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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ChildList;

public class CtInvocationImpl<T> extends
		CtTargetedExpressionImpl<T, CtExpression<?>> implements CtInvocation<T> {
	private static final long serialVersionUID = 1L;

	List<CtExpression<?>> arguments = new ChildList<CtExpression<?>>(this);

	CtBlock<?> block;

	CtExecutableReference<T> executable;

	List<CtExpression<Integer>> indexExpressions = new ChildList<CtExpression<Integer>>(this);

	List<CtTypeReference<?>> genericTypes = new ArrayList<CtTypeReference<?>>();

	public void accept(CtVisitor visitor) {
		visitor.visitCtInvocation(this);
	}

	public void setGenericTypes(List<CtTypeReference<?>> genericTypes) {
		this.genericTypes =genericTypes;
	}
	
	public List<CtTypeReference<?>> getGenericTypes(){
		return this.genericTypes;
	}
	@Override
	public void setTarget(CtExpression<?> target) {
		super.setTarget(target);
		target.setParent(this);
	}

	public List<CtExpression<?>> getArguments() {
		return arguments;
	}

	public CtExecutableReference<T> getExecutable() {
		return executable;
	}

	public List<CtExpression<Integer>> getIndexExpressions() {
		return indexExpressions;
	}

	public void insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
	}

	public void insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
	}

	public void insertAfter(CtStatementList<?> statements) {
		CtStatementImpl.insertAfter(this, statements);
	}

	public void insertBefore(CtStatementList<?> statements) {
		CtStatementImpl.insertBefore(this, statements);
	}

	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList<?>) element);
		} else {
			super.replace(element);
		}
	}

	public boolean isArrayOperation() {
		return indexExpressions.size() > 0;
	};

	public void setArguments(List<CtExpression<?>> arguments) {
		this.arguments = new ChildList<CtExpression<?>>(arguments,this);
	}

	public void setExecutable(CtExecutableReference<T> executable) {
		this.executable = executable;
	}

	public void setIndexExpressions(List<CtExpression<Integer>> indexExpressions) {
		this.indexExpressions = new ChildList<CtExpression<Integer>>(indexExpressions,this);
	}

	String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
