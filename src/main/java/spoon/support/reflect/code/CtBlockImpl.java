/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.util.EmptyIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.STATEMENT;

public class CtBlockImpl<R> extends CtStatementImpl implements CtBlock<R> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.STATEMENT)
	private List<CtStatement> statements = emptyList();

	public void accept(CtVisitor visitor) {
		visitor.visitCtBlock(this);
	}

	@Override
	public List<CtStatement> getStatements() {
		ensureModifiableStatementsList();
		return this.statements;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtStatement> T getStatement(int i) {
		return (T) statements.get(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CtStatement> T getLastStatement() {
		return (T) statements.get(statements.size() - 1);
	}

	private boolean shouldInsertAfterSuper() {
		try {
			if (getParent() != null && getParent() instanceof CtConstructor && getStatements().size() > 0) {
				CtStatement first = getStatements().get(0);
				if (first instanceof CtInvocation && ((CtInvocation<?>) first).getExecutable().isConstructor()) {
					return true;
				}
			}
		} catch (ParentNotInitializedException ignore) {
			// CtBlock hasn't a parent. So, it isn't in a constructor.
		}
		return false;
	}

	@Override
	public <T extends CtStatementList> T insertBegin(CtStatementList statements) {
		if (this.shouldInsertAfterSuper()) {
			getStatements().get(0).insertAfter(statements);
			return (T) this;
		}
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
		if (this.shouldInsertAfterSuper()) {
			getStatements().get(0).insertAfter(statement);
			return (T) this;
		}
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
		for (CtStatement s : statements.getStatements()) {
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
	public <T extends CtStatementList> T setStatements(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.statements = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, STATEMENT, this.statements, new ArrayList<>(this.statements));
		this.statements.clear();
		for (CtStatement s : statements) {
			addStatement(s);
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
		ensureModifiableStatementsList();
		statement.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, STATEMENT, this.statements, index, statement);
		this.statements.add(index, statement);
		if (isImplicit() && this.statements.size() > 1) {
			setImplicit(false);
		}
		return (T) this;
	}

	private void ensureModifiableStatementsList() {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (this.statements != CtElementImpl.<CtStatement>emptyList()) {
			boolean hasBeenRemoved = false;
			// we cannot use a remove(statement) as it uses the equals
			// and a block can have twice exactly the same statement.
			for (int i = 0; i < this.statements.size(); i++) {
				if (this.statements.get(i) == statement) {
					getFactory().getEnvironment().getModelChangeListener().onListDelete(this, STATEMENT, statements, i, statement);
					this.statements.remove(i);
					hasBeenRemoved = true;
					break;
				}
			}

			// in case we use it with a statement manually built
			if (!hasBeenRemoved) {
				getFactory().getEnvironment().getModelChangeListener().onListDelete(this, STATEMENT, statements, statements.indexOf(statement), statement);
				this.statements.remove(statement);
			}

			if (isImplicit() && statements.size() == 0) {
				setImplicit(false);
			}
		}
	}

	@Override
	public Iterator<CtStatement> iterator() {
		if (getStatements().isEmpty()) {
			return EmptyIterator.instance();
		}
		// we have to both create a defensive object and an unmodifiable list
		// with only Collections.unmodifiableList you can modify the defensive object
		// with only new ArrayList it breaks the encapsulation
		return Collections.unmodifiableList(new ArrayList<>(getStatements())).iterator();
	}

	@Override
	public R S() {
		return null;
	}

	@Override
	public CtBlock<R> clone() {
		return (CtBlock<R>) super.clone();
	}
}
