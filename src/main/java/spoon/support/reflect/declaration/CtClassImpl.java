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

package spoon.support.reflect.declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

/**
 * The implementation for {@link spoon.reflect.declaration.CtClass}.
 * 
 * @author Renaud Pawlak
 */
public class CtClassImpl<T extends Object> extends CtTypeImpl<T> implements
		CtClass<T> {
	private static final long serialVersionUID = 1L;

	List<CtAnonymousExecutable> anonymousExecutables = EMPTY_LIST();

	Set<CtConstructor<T>> constructors = EMPTY_SET();

	CtTypeReference<?> superClass;

	public void accept(CtVisitor v) {
		v.visitCtClass(this);
	}

	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> ret = new TreeSet<CtMethod<?>>();
		ret.addAll(getMethods());

		if ((getSuperclass() != null)
				&& (getSuperclass().getDeclaration() != null)) {
			CtType<?> t = (CtType<?>) getSuperclass().getDeclaration();
			ret.addAll(t.getAllMethods());
		}
		return ret;
	}

	public List<CtAnonymousExecutable> getAnonymousExecutables() {
		return anonymousExecutables;
	}

	public CtConstructor<T> getConstructor(CtTypeReference<?>... parameterTypes) {
		for (CtConstructor<T> c : constructors) {
			boolean cont = c.getParameters().size() == parameterTypes.length;
			for (int i = 0; cont && (i < c.getParameters().size())
					&& (i < parameterTypes.length); i++) {
				if (!c.getParameters().get(i).getType().getQualifiedName()
						.equals(parameterTypes[i].getQualifiedName())) {
					cont = false;
				}
			}
			if (cont) {
				return c;
			}
		}
		return null;
	}

	public Set<CtConstructor<T>> getConstructors() {
		return constructors;
	}

	public boolean addAnonymousExecutable(CtAnonymousExecutable e) {
		if (anonymousExecutables == CtElementImpl
				.<CtAnonymousExecutable> EMPTY_LIST()) {
			anonymousExecutables = new ArrayList<CtAnonymousExecutable>();
		}
		return anonymousExecutables.add(e);
	}

	public boolean removeAnonymousExecutable(CtAnonymousExecutable e) {
		if (anonymousExecutables == CtElementImpl
				.<CtAnonymousExecutable> EMPTY_LIST()) {
			anonymousExecutables = new ArrayList<CtAnonymousExecutable>();
		}
		return anonymousExecutables.remove(e);
	}

	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	public void setAnonymousExecutables(List<CtAnonymousExecutable> e) {
		anonymousExecutables = e;
	}

	public void setConstructors(Set<CtConstructor<T>> constructors) {
		this.constructors = constructors;
	}

	@Override
	public void addConstructor(CtConstructor<T> constructor) {
		if (constructors == CtElementImpl.<CtConstructor<T>> EMPTY_SET()) {
			constructors = new TreeSet<CtConstructor<T>>();
		}
		// this needs to be done because of the set that needs the constructor's
		// signature : we should use lists!!!
		// TODO: CHANGE SETS TO LIST TO AVOID HAVING TO DO THIS
		constructor.setParent(this);
		constructors.add(constructor);
	}

	@Override
	public void removeConstructor(CtConstructor<T> constructor) {
		if (constructors == CtElementImpl.<CtConstructor<T>> EMPTY_SET()) {
			constructors = new TreeSet<CtConstructor<T>>();
		}
		constructors.remove(constructor);
	}

	public void setSuperclass(CtTypeReference<?> superClass) {
		this.superClass = superClass;
	}

	public boolean isSubtypeOf(CtTypeReference<?> type) {
		if ((getSuperclass() != null) && getSuperclass().isSubtypeOf(type)) {
			return true;
		}
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return false;
	}

	public void insertAfter(CtStatement statement) {
		spoon.support.reflect.code.CtStatementImpl.insertAfter(this, statement);
	}

	public void insertAfter(CtStatementList statements) {
		spoon.support.reflect.code.CtStatementImpl
				.insertAfter(this, statements);
	}

	public void insertBefore(CtStatement statement) {
		spoon.support.reflect.code.CtStatementImpl
				.insertBefore(this, statement);
	}

	public void insertBefore(CtStatementList statements) {
		spoon.support.reflect.code.CtStatementImpl.insertBefore(this,
				statements);
	}

	public String getLabel() {
		return null;
	}

	public void setLabel(String label) {
		throw new UnsupportedOperationException(
				"cannot set a label on a class declaration");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate(getParent(), (R) this);
	}
}
