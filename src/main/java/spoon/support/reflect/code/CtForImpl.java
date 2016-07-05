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

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.FOR_INIT_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.FOR_UPDATE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;

public class CtForImpl extends CtLoopImpl implements CtFor {
	private static final long serialVersionUID = 1L;

	CtExpression<Boolean> expression;

	List<CtStatement> forInit = emptyList();

	List<CtStatement> forUpdate = emptyList();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtFor(this);
	}

	@Override
	public CtExpression<Boolean> getExpression() {
		return expression;
	}

	@Override
	public <T extends CtFor> T setExpression(CtExpression<Boolean> expression) {
		if (expression != null) {
			expression.setParent(this);
		}
		this.expression = expression;
		return (T) this;
	}

	@Override
	public List<CtStatement> getForInit() {
		return forInit;
	}

	@Override
	public <T extends CtFor> T addForInit(CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		if (forInit == CtElementImpl.<CtStatement>emptyList()) {
			forInit = new ArrayList<>(FOR_INIT_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		forInit.add(statement);
		return (T) this;
	}

	@Override
	public <T extends CtFor> T setForInit(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.forInit = CtElementImpl.emptyList();
			return (T) this;
		}
		this.forInit.clear();
		for (CtStatement stmt : statements) {
			addForInit(stmt);
		}
		return (T) this;
	}

	@Override
	public boolean removeForInit(CtStatement statement) {
		return forInit != CtElementImpl.<CtStatement>emptyList() && forInit.remove(statement);
	}

	@Override
	public List<CtStatement> getForUpdate() {
		return forUpdate;
	}

	@Override
	public <T extends CtFor> T addForUpdate(CtStatement statement) {
		if (statement == null) {
			return (T) this;
		}
		if (forUpdate == CtElementImpl.<CtStatement>emptyList()) {
			forUpdate = new ArrayList<>(FOR_UPDATE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY);
		}
		statement.setParent(this);
		forUpdate.add(statement);
		return (T) this;
	}

	@Override
	public <T extends CtFor> T setForUpdate(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.forUpdate = CtElementImpl.emptyList();
			return (T) this;
		}
		this.forUpdate.clear();
		for (CtStatement stmt : statements) {
			addForUpdate(stmt);
		}
		return (T) this;
	}

	@Override
	public boolean removeForUpdate(CtStatement statement) {
		return forUpdate != CtElementImpl.<CtStatement>emptyList() && forUpdate.remove(statement);
	}

	@Override
	public CtFor clone() {
		return (CtFor) super.clone();
	}
}
