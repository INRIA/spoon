/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

public class CtTryImpl extends CtStatementImpl implements CtTry {
	private static final long serialVersionUID = 1L;

	List<CtCatch> catchers = EMPTY_LIST();

	public List<CtCatch> getCatchers() {
		return catchers;
	}

	public void setCatchers(List<CtCatch> catchers) {
		this.catchers = catchers;
	}

	@Override
	public boolean addCatcher(CtCatch catcher) {
		if (catchers == CtElementImpl.<CtCatch> EMPTY_LIST()) {
			catchers = new ArrayList<CtCatch>();
		}
		return catchers.add(catcher);
	}

	@Override
	public boolean removeCatcher(CtCatch catcher) {
		if (catchers == CtElementImpl.<CtCatch> EMPTY_LIST()) {
			catchers = new ArrayList<CtCatch>();
		}
		return catchers.remove(catcher);
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtTry(this);
	}

	CtBlock<?> finalizer;

	public CtBlock<?> getFinalizer() {
		return finalizer;
	}

	public void setFinalizer(CtBlock<?> finalizer) {
		this.finalizer = finalizer;
	}

	CtBlock<?> body;

	public CtBlock<?> getBody() {
		return body;
	}

	public void setBody(CtBlock<?> body) {
		this.body = body;
	}

	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return getFactory().Core().clone(this);
	}

}
