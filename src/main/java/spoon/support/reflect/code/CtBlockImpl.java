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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtBlockImpl<R> extends CtStatementImpl implements CtBlock<R> {
	private static final long serialVersionUID = 1L;

	private List<CtStatement> statements = EMPTY_LIST();

	public void accept(CtVisitor visitor) {
		visitor.visitCtBlock(this);
	}

	public List<CtStatement> getStatements() {
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		return this.statements;
	}

	public CtStatementList toStatementList() {
		return getFactory().Code().createStatementList(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends CtStatement> T getStatement(int i) {
		return (T) statements.get(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtStatement> T getLastStatement() {
		return (T) statements.get(statements.size() - 1);
	}

	public void insertBegin(CtStatementList statements) {
		if (getParentNoExceptions() != null
				&& getParentNoExceptions() instanceof CtConstructor
				&& getStatements().size() > 0) {
			CtStatement first = getStatements().get(0);
			if (first instanceof CtInvocation
					&& ((CtInvocation<?>) first).getExecutable()
							.getSimpleName().startsWith("<init>")) {
				first.insertAfter(statements);
				return;
			}
		}
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		this.statements.addAll(0, statements.getStatements());
	}

	public void insertBegin(CtStatement statement) {
		if (getParentNoExceptions() != null
				&& getParentNoExceptions() instanceof CtConstructor
				&& getStatements().size() > 0) {
			CtStatement first = getStatements().get(0);
			if (first instanceof CtInvocation
					&& ((CtInvocation<?>) first).getExecutable()
							.getSimpleName().startsWith("<init>")) {
				first.insertAfter(statement);
				return;
			}
		}
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		this.statements.add(0, statement);
	}

	public void insertEnd(CtStatement statement) {
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		addStatement(statement);
	}

	public void insertEnd(CtStatementList statements) {
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
			CtStatementList statements) {
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
			CtStatementList statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statements);
		}
	}

	public void setStatements(List<CtStatement> statements) {
		this.statements = statements;
	}

	public R S() {
		return null;
	}

	public void R(R value) {

	}

	public CtCodeElement getSubstitution(CtSimpleType<?> targetType) {
		return getFactory().Core().clone(this);
	}

	@Override
	public void addStatement(CtStatement statement) {
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		this.statements.add(statement);
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (this.statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			this.statements = new ArrayList<CtStatement>();
		}
		this.statements.remove(statement);
	}

	@Override
	public Iterator<CtStatement> iterator() {
		// we have to both create a defensive object and an unmodifiable list
		// with only Collections.unmodifiableList you can modify the defensive object
		// with only new ArrayList it breaks the encapsulation
		return Collections.unmodifiableList(
				new ArrayList<CtStatement>(getStatements())).iterator();
	}

}
