/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CATCH_CASES_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.CATCH;
import static spoon.reflect.path.CtRole.FINALIZER;

public class CtTryImpl extends CtStatementImpl implements CtTry {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = BODY)
	CtBlock<?> body;

	@MetamodelPropertyField(role = CATCH)
	List<CtCatch> catchers = emptyList();

	@MetamodelPropertyField(role = FINALIZER)
	CtBlock<?> finalizer;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTry(this);
	}

	@Override
	public List<CtCatch> getCatchers() {
		return catchers;
	}

	@Override
	public <T extends CtTry> T setCatchers(List<CtCatch> catchers) {
		if (catchers == null || catchers.isEmpty()) {
			this.catchers = CtElementImpl.emptyList();
			return (T) this;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CATCH, this.catchers, new ArrayList<>(this.catchers));
		this.catchers.clear();
		for (CtCatch c : catchers) {
			addCatcher(c);
		}
		return (T) this;
	}

	@Override
	public <T extends CtTry> T addCatcher(CtCatch catcher) {
		if (catcher == null) {
			return (T) this;
		}
		if (catchers == CtElementImpl.<CtCatch>emptyList()) {
			catchers = new ArrayList<>(CATCH_CASES_CONTAINER_DEFAULT_CAPACITY);
		}
		catcher.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CATCH, this.catchers, catcher);
		catchers.add(catcher);
		return (T) this;
	}

	@Override
	public boolean removeCatcher(CtCatch catcher) {
		if (catchers == CtElementImpl.<CtCatch>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CATCH, catchers, catchers.indexOf(catcher), catcher);
		return catchers.remove(catcher);
	}

	@Override
	public CtBlock<?> getFinalizer() {
		return finalizer;
	}

	@Override
	public <T extends CtTry> T setFinalizer(CtBlock<?> finalizer) {
		if (finalizer != null) {
			finalizer.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, FINALIZER, finalizer, this.finalizer);
		this.finalizer = finalizer;
		return (T) this;
	}

	@Override
	public CtBlock<?> getBody() {
		return body;
	}

	@Override
	public <T extends CtBodyHolder> T setBody(CtStatement statement) {
		if (statement != null) {
			CtBlock<?> body = getFactory().Code().getOrCreateCtBlock(statement);
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BODY, body, this.body);
			if (body != null) {
				body.setParent(this);
			}
			this.body = body;
		} else {
			getFactory().getEnvironment().getModelChangeListener().onObjectDelete(this, BODY, this.body);
			this.body = null;
		}

		return (T) this;
	}

	@Override
	public CtTry clone() {
		return (CtTry) super.clone();
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return clone();
	}

}
