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

import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.util.ChildList;

public class CtBlockImpl<R> extends CtStatementImpl implements CtBlock<R> {
	private static final long serialVersionUID = 1L;

	List<CtStatement> statements = new ChildList<CtStatement>(this);

	public void accept(CtVisitor visitor) {
		visitor.visitCtBlock(this);
	}

	public List<CtStatement> getStatements() {
		return statements;
	}

	public void insertBegin(CtStatementList<?> statements) {
		List<CtInvocation<?>> invocations = Query.getElements(this,
				new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		if (invocations.size() > 0) {
			CtInvocation<?> invoc = invocations.get(0);
			if (invoc.getExecutable().getSimpleName().startsWith("<init>")) {
				invoc.insertAfter(statements);
				return;
			}
		}
		for (CtStatement s : statements.getStatements()) {
			s.setParent(this);
		}
		getStatements().addAll(0, statements.getStatements());
	}

	public void insertBegin(CtStatement statement) {
		List<CtInvocation<?>> invocations = Query.getElements(this,
				new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		if (invocations.size() > 0) {
			CtInvocation<?> invoc = invocations.get(0);
			if (invoc.getExecutable().getSimpleName().startsWith("<init>")) {
				invoc.insertAfter(statement);
				return;
			}
		}
		statement.setParent(this);
		getStatements().add(0, statement);
	}

	public void insertEnd(CtStatement statement) {
		getStatements().add(statement);
		statement.setParent(this);
	}

	public void insertEnd(CtStatementList<?> statements) {
		for (CtStatement s : statements.getStatements()) {
			insertEnd(s);
		}
	}

	public void insertAfter(Filter<? extends CtStatement> insertionPoints,
			CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statement);
		}
	}

	public void insertAfter(Filter<? extends CtStatement> insertionPoints,
			CtStatementList<?> statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statements);
		}
	}

	public void insertBefore(Filter<? extends CtStatement> insertionPoints,
			CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statement);
		}
	}

	public void insertBefore(Filter<? extends CtStatement> insertionPoints,
			CtStatementList<?> statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statements);
		}
	}

	public void setStatements(List<CtStatement> statements) {
		this.statements = new ChildList<CtStatement>(statements,this);
	}

	public R S() {
		return null;
	}

	public void R(R value) {

	}

	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

}
