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
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.util.ModelList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.STATEMENT;

public class CtBlockImpl<R> extends CtStatementImpl implements CtBlock<R> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.STATEMENT)
	private final ModelList<CtStatement> statements = new ModelList<CtStatement>() {
		private static final long serialVersionUID = 1L;
		@Override
		protected CtElement getOwner() {
			return CtBlockImpl.this;
		}
		@Override
		protected CtRole getRole() {
			return STATEMENT;
		}
		@Override
		protected int getDefaultCapacity() {
			return BLOCK_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
		}
		@Override
		protected void onSizeChanged(int newSize) {
			if (isImplicit() && (newSize > 1 || newSize == 0)) {
				setImplicit(false);
			}
		}
	};

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtBlock(this);
	}

	@Override
	public List<CtStatement> getStatements() {
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
			if (getParent() != null && getParent() instanceof CtConstructor && !getStatements().isEmpty()) {
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
		List<CtStatement> copy = new ArrayList<>(statements.getStatements());
		statements.setStatements(null);
		this.statements.addAll(0, copy);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertBegin(CtStatement statement) {
		if (this.shouldInsertAfterSuper()) {
			getStatements().get(0).insertAfter(statement);
			return (T) this;
		}
		this.statements.add(0, statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertEnd(CtStatement statement) {
		addStatement(statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T insertEnd(CtStatementList statements) {
		List<CtStatement> tobeInserted = new ArrayList<>(statements.getStatements());
		//remove statements from the `statementsToBeInserted` before they are added to spoon model
		//note: one element MUST NOT be part of two models.
		statements.setStatements(null);
		this.statements.addAll(this.statements.size(), tobeInserted);
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
		this.statements.set(statements);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(CtStatement statement) {
		this.statements.add(statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(int index, CtStatement statement) {
		this.statements.add(index, statement);
		return (T) this;
	}


	@Override
	public void removeStatement(CtStatement statement) {
		this.statements.remove(statement);
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return this.statements.iterator();
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
