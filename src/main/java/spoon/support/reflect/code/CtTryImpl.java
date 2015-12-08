/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CATCH_CASES_CONTAINER_DEFAULT_CAPACITY;

public class CtTryImpl extends CtStatementImpl implements CtTry {
	private static final long serialVersionUID = 1L;

	CtBlock<?> body;

	List<CtCatch> catchers = emptyList();

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
		this.catchers.clear();
		for (CtCatch c : catchers) {
			addCatcher(c);
		}
		return (T) this;
	}

	@Override
	public <T extends CtTry> T addCatcher(CtCatch catcher) {
		if (catchers == CtElementImpl.<CtCatch>emptyList()) {
			catchers = new ArrayList<CtCatch>(CATCH_CASES_CONTAINER_DEFAULT_CAPACITY);
		}
		catcher.setParent(this);
		catchers.add(catcher);
		return (T) this;
	}

	@Override
	public boolean removeCatcher(CtCatch catcher) {
		return catchers != CtElementImpl.<CtCatch>emptyList() && catchers.remove(catcher);
	}

	@Override
	public CtBlock<?> getFinalizer() {
		return finalizer;
	}

	@Override
	public <T extends CtTry> T setFinalizer(CtBlock<?> finalizer) {
		finalizer.setParent(this);
		this.finalizer = finalizer;
		return (T) this;
	}

	@Override
	public CtBlock<?> getBody() {
		return body;
	}

	@Override
	public <T extends CtTry> T setBody(CtBlock<?> body) {
		body.setParent(this);
		this.body = body;
		return (T) this;
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return getFactory().Core().clone(this);
	}

}
