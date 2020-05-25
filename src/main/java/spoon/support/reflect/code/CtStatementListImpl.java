/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public <T extends CtStatementList> T setStatements(List<CtStatement> stmts) {
		if (stmts == null || stmts.isEmpty()) {
			this.statements = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, STATEMENT, this.statements, new ArrayList<>(this.statements));
		this.statements.clear();
		for (CtStatement stmt : stmts) {
			addStatement(stmt);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(CtStatement statement) {
		return this.addStatement(this.statements.size(), statement);
	}

	@Override
	public <T extends CtStatementList> T addStatement(int index, CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, STATEMENT, this.statements, index, statement);
		this.statements.add(index, statement);
		return (T) this;
	}

	private void ensureModifiableStatementsList() {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	@Override
	public <T extends CtStatementList> T insertBegin(CtStatementList statements) {
		ensureModifiableStatementsList();
		for (CtStatement statement : statements.getStatements()) {
			statement.setParent(this);
			this.addStatement(0, statement);
		}
		if (isImplicit() && this.statements.size() > 1) {
			setImplicit(false);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertBegin(CtStatement statement) {
		ensureModifiableStatementsList();
		statement.setParent(this);
		this.addStatement(0, statement);

		if (isImplicit() && this.statements.size() > 1) {
			setImplicit(false);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertEnd(CtStatement statement) {
		ensureModifiableStatementsList();
		addStatement(statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertEnd(CtStatementList statements) {
		List<CtStatement> tobeInserted = new ArrayList<>(statements.getStatements());
		//remove statements from the `statementsToBeInserted` before they are added to spoon model
		//note: one element MUST NOT be part of two models.
		statements.setStatements(null);
		for (CtStatement s : tobeInserted) {
			insertEnd(s);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statement);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertAfter(Filter<? extends CtStatement> insertionPoints, CtStatementList statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertAfter(statements);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatement statement) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statement);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertBefore(Filter<? extends CtStatement> insertionPoints, CtStatementList statements) {
		for (CtStatement e : Query.getElements(this, insertionPoints)) {
			e.insertBefore(statements);
		}
		return (T) this;
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
	public <E extends CtElement> E setPosition(SourcePosition position) {
		for (CtStatement s : statements) {
			s.setPosition(position);
		}
		return (E) this;
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return statements.iterator();
	}

	@Override
	public CtStatementList clone() {
		return (CtStatementList) super.clone();
	}

	public CtStatementList getSubstitution(CtType<?> targetType) {
		return clone();
	}
}
