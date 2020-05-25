/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.FOR_INIT_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.ModelElementContainerDefaultCapacities.FOR_UPDATE_STATEMENTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.EXPRESSION;
import static spoon.reflect.path.CtRole.FOR_INIT;
import static spoon.reflect.path.CtRole.FOR_UPDATE;

public class CtForImpl extends CtLoopImpl implements CtFor {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = EXPRESSION)
	CtExpression<Boolean> expression;

	@MetamodelPropertyField(role = FOR_INIT)
	List<CtStatement> forInit = emptyList();

	@MetamodelPropertyField(role = FOR_UPDATE)
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, EXPRESSION, expression, this.expression);
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
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, FOR_INIT, this.forInit, statement);
		forInit.add(statement);
		return (T) this;
	}

	@Override
	public <T extends CtFor> T setForInit(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.forInit = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, FOR_INIT, this.forInit, new ArrayList<>(this.forInit));
		this.forInit.clear();
		for (CtStatement stmt : statements) {
			addForInit(stmt);
		}
		return (T) this;
	}

	@Override
	public boolean removeForInit(CtStatement statement) {
		if (forInit == CtElementImpl.<CtStatement>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, FOR_INIT, forInit, forInit.indexOf(statement), statement);
		return forInit.remove(statement);
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
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, FOR_UPDATE, this.forUpdate, statement);
		forUpdate.add(statement);
		return (T) this;
	}

	@Override
	public <T extends CtFor> T setForUpdate(List<CtStatement> statements) {
		if (statements == null || statements.isEmpty()) {
			this.forUpdate = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, FOR_UPDATE, this.forUpdate, new ArrayList<>(this.forUpdate));
		this.forUpdate.clear();
		for (CtStatement stmt : statements) {
			addForUpdate(stmt);
		}
		return (T) this;
	}

	@Override
	public boolean removeForUpdate(CtStatement statement) {
		if (forUpdate == CtElementImpl.<CtStatement>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, FOR_UPDATE, forUpdate, forUpdate.indexOf(statement), statement);
		return forUpdate.remove(statement);
	}

	@Override
	public CtFor clone() {
		return (CtFor) super.clone();
	}
}
