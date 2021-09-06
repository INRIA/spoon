/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CtInterfaceImpl<T> extends CtTypeImpl<T> implements CtInterface<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtInterface(this);
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getReference().isSubtypeOf(type);
	}

	@Override
	public boolean isInterface() {
		return true;
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		Set<CtTypeReference<?>> superInterfaces = getSuperInterfaces();
		if (superInterfaces.isEmpty()) {
			return super.getDeclaredExecutables();
		}
		List<CtExecutableReference<?>> l = new ArrayList<>(super.getDeclaredExecutables());
		for (CtTypeReference<?> sup : superInterfaces) {
			l.addAll(sup.getAllExecutables());
		}
		return Collections.unmodifiableList(l);
	}

	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate((R) this);
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatement statement) throws ParentNotInitializedException {
		CtStatementImpl.insertAfter(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatementList statements) throws ParentNotInitializedException {
		CtStatementImpl.insertAfter(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatement statement) throws ParentNotInitializedException {
		CtStatementImpl.insertBefore(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatementList statements) throws ParentNotInitializedException {
		CtStatementImpl.insertBefore(this, statements);
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtStatement> C setLabel(String label) {
		return (C) this;
	}

	@Override
	public CtInterface<T> clone() {
		return (CtInterface<T>) super.clone();
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		// unsettable property
		return (C) this;
	}

	@Override
	public String getLabel() {
		return null;
	}
}
