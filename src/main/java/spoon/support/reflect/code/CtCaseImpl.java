/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.ModelElementContainerDefaultCapacities;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CaseKind;
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

public class CtCaseImpl<E> extends CtStatementImpl implements CtCase<E> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
	CtExpression<E> caseExpression;

	@MetamodelPropertyField(role = CtRole.EXPRESSION)
	List<CtExpression<E>> caseExpressions = emptyList();

	@MetamodelPropertyField(role = CtRole.STATEMENT)
	List<CtStatement> statements = emptyList();

	@MetamodelPropertyField(role = CtRole.CASE_KIND)
	CaseKind caseKind = CaseKind.COLON;

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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.CASE, caseExpression, this.caseExpression);
		this.caseExpression = caseExpression;
		return (T) this;
	}

	@Override
	public List<CtExpression<E>> getCaseExpressions() {
		return caseExpressions;
	}

	@Override
	public <T extends CtCase<E>> T setCaseExpressions(List<CtExpression<E>> caseExpressions) {
		if (caseExpressions == null || caseExpressions.isEmpty()) {
			this.caseExpressions = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.CASE, caseExpressions, this.caseExpressions);
		this.caseExpressions.clear();
		for (CtExpression expr : caseExpressions) {
			addCaseExpression(expr);
		}
		return (T) this;
	}

	@Override
	public <T extends CtCase<E>> T addCaseExpression(CtExpression<E> caseExpression) {
		if (caseExpression == null) {
			return (T) this;
		}
		this.ensureModifiableCaseExpressionsList();
		if (getCaseExpression() == null) {
			setCaseExpression(caseExpression);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.CASE, caseExpressions, this.caseExpressions);
		this.caseExpressions.add(caseExpression);
		return (T) this;
	}

	@Override
	public CaseKind getCaseKind() {
		return caseKind;
	}

	@Override
	public <T extends CtCase<E>> T setCaseKind(CaseKind kind) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.CASE_KIND, kind, this.caseKind);
		caseKind = kind;
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T setStatements(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.statements = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.STATEMENT, this.statements, new ArrayList<>(this.statements));
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
			this.statements = new ArrayList<>(ModelElementContainerDefaultCapacities.CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	private void ensureModifiableCaseExpressionsList() {
		if (this.caseExpressions == CtElementImpl.<CtExpression<E>>emptyList()) {
			this.caseExpressions = new ArrayList<>(ModelElementContainerDefaultCapacities.CASE_EXPRESSIONS_CONTAINER_DEFAULT_CAPACITY);
		}
	}

	@Override
	public <T extends CtStatementList> T addStatement(int index, CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		this.ensureModifiableStatementsList();
		statement.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.STATEMENT, this.statements, index, statement);
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
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.STATEMENT, statements, statements.indexOf(statement), statement);
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
