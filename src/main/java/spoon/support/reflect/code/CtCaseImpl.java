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
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.CASE;
import static spoon.reflect.path.CtRole.STATEMENT;

public class CtCaseImpl<E> extends CtStatementImpl implements CtCase<E> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
	CtExpression<E> caseExpression;

	@MetamodelPropertyField(role = CtRole.STATEMENT)
	List<CtStatement> statements = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCase(this);
	}

	@Override
	public CtExpression<E> getCaseExpression() {
		return caseExpression;
	}

	@Override
	public List<CtStatement> getStatements() {
		return statements;
	}

	@Override
	public <T extends CtCase<E>> T setCaseExpression(CtExpression<E> caseExpression) {
		if (caseExpression != null) {
			caseExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CASE, caseExpression, this.caseExpression);
		this.caseExpression = caseExpression;
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
		for (CtStatement stmt : statements) {
			addStatement(stmt);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(CtStatement statement) {
		return this.addStatement(this.statements.size(), statement);
	}

	private void ensureModifiableStatementsList() {
		if (this.statements == CtElementImpl.<CtStatement>emptyList()) {
			this.statements = new ArrayList<>(CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	@Override
	public <T extends CtStatementList> T addStatement(int index, CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		this.ensureModifiableStatementsList();
		statement.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, STATEMENT, this.statements, index, statement);
		statements.add(index, statement);
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
	public <T extends CtStatementList> T insertBegin(CtStatementList statements) {
		this.ensureModifiableStatementsList();
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
	public <T extends CtStatement> T getStatement(int i) {
		return (T) statements.get(i);
	}

	@Override
	public <T extends CtStatement> T getLastStatement() {
		return (T) statements.get(statements.size() - 1);
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (statements == CtElementImpl.<CtStatement>emptyList()) {
			return;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, STATEMENT, statements, statements.indexOf(statement), statement);
		statements.remove(statement);
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return getStatements().iterator();
	}

	@Override
	public CtCase<E> clone() {
		return (CtCase<E>) super.clone();
	}
}
