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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.STATEMENT;




public class CtStatementListImpl<R> extends CtCodeElementImpl implements CtStatementList {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = STATEMENT)
	List<CtStatement> statements = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtStatementList(this);
	}

	@Override
	public List<CtStatement> getStatements() {
		return statements;
	}

	@Override
	public CtStatementListImpl<R> setStatements(List<CtStatement> stmts) {
		if (stmts == null || stmts.isEmpty()) {
			this.statements = CtElementImpl.emptyList();
			return this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, STATEMENT, this.statements, new ArrayList<>(this.statements));
		this.statements.clear();
		for (CtStatement stmt : stmts) {
			addStatement(stmt);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> addStatement(CtStatement statement) {
		return ((CtStatementListImpl<R>) (this.addStatement(this.statements.size(), statement)));
	}

	@Override
	public CtStatementListImpl<R> addStatement(int index, CtStatement statement) {
		if (statement == null) {
			return this;
		}
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, STATEMENT, this.statements, index, statement);
		this.statements.add(index, statement);
		return this;
	}

	private void ensureModifiableStatementsList() {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	@Override
	public CtStatementListImpl<R> insertBegin(CtStatementList statements) {
		ensureModifiableStatementsList();
		for (CtStatement statement : statements.getStatements()) {
			statement.setParent(this);
			this.addStatement(0, statement);
		}
		if (isImplicit() && this.statements.size() > 1) {
			setImplicit(false);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertBegin(CtStatement statement) {
		ensureModifiableStatementsList();
		statement.setParent(this);
		this.addStatement(0, statement);

		if (isImplicit() && this.statements.size() > 1) {
			setImplicit(false);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertEnd(CtStatement statement) {
		ensureModifiableStatementsList();
		addStatement(statement);
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertEnd(CtStatementList statements) {
		List<CtStatement> tobeInserted = new ArrayList<>(statements.getStatements());
		//remove statements from the `statementsToBeInserted` before they are added to spoon model
		//note: one element MUST NOT be part of two models.
		statements.setStatements(null);
		for (CtStatement s : tobeInserted) {
			insertEnd(s);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statement);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatementList statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statements);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statement);
		}
		return this;
	}

	@Override
	public CtStatementListImpl<R> insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatementList statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statements);
		}
		return this;
	}

	@Override
	public <T extends CtStatement> T getStatement(int i) {
		return (T) statements.get(i);
	}

	@Override
	public <T extends CtStatement> T getLastStatement() {
		return (T) statements.get(statements.size() - 1);
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			return;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, STATEMENT, statements, statements.indexOf(statement), statement);
		statements.remove(statement);
	}

	@Override
	public CtStatementListImpl<R> setPosition(SourcePosition position) {
		for (CtStatement s : statements) {
			s.setPosition(position);
		}
		return this;
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return statements.iterator();
	}

	@Override
	public CtStatementList clone() {
		return (CtStatementList) super.clone();
	}

	public CtStatementListImpl<R> getSubstitution(CtType<?> targetType) {
		return ((CtStatementListImpl<R>) (clone()));
	}
}
