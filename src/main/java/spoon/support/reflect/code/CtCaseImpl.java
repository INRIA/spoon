/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;

public class CtCaseImpl<E> extends CtStatementImpl implements CtCase<E> {
	private static final long serialVersionUID = 1L;

	CtExpression<E> caseExpression;

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
		this.caseExpression = caseExpression;
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T setStatements(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.statements = CtElementImpl.emptyList();
			return (T) this;
		}
		this.statements.clear();
		for (CtStatement stmt : statements) {
			addStatement(stmt);
		}
		return (T) this;
	}

	@Override
	public <T extends CtStatementList> T addStatement(CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		if (statements == CtElementImpl.<CtStatement>emptyList()) {
			statements = new ArrayList<>(CASE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		statements.add(statement);
		return (T) this;
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (statements == CtElementImpl.<CtStatement>emptyList()) {
			return;
		}
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
