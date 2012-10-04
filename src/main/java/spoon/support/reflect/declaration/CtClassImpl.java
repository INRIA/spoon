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

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtClass}.
 * 
 * @author Renaud Pawlak
 */
public class CtClassImpl<T extends Object> extends CtTypeImpl<T> implements
		CtClass<T> {
	private static final long serialVersionUID = 1L;

	List<CtAnonymousExecutable> anonymousExecutable = new ArrayList<CtAnonymousExecutable>();

	Set<CtConstructor<T>> constructors = new TreeSet<CtConstructor<T>>();

	CtTypeReference<?> superClass;

	public void accept(CtVisitor v) {
		v.visitCtClass(this);
	}

	// @Override
	// public List<CtField<?>> getAllFields() {
	// if (getSuperclass() != null && getSuperclass().getDeclaration() != null)
	// {
	// List<CtField<?>> fields = new ArrayList<CtField<?>>();
	// fields.addAll(getSuperclass().getDeclaration().getAllFields());
	// fields.addAll(getFields());
	// return fields;
	// }
	// return super.getAllFields();
	// }

	public Set<CtMethod<?>> getAllMethods() {
		Set<CtMethod<?>> ret = new TreeSet<CtMethod<?>>();
		ret.addAll(getMethods());

		if ((getSuperclass() != null)
				&& (getSuperclass().getDeclaration() != null)) {
			CtType<?> t = (CtType<?>) getSuperclass().getDeclaration();
			ret.addAll(t.getMethods());
		}
		return ret;
	}

	public List<CtAnonymousExecutable> getAnonymousExecutables() {
		return anonymousExecutable;
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

	// TODO : remove useless
	// @Override
	// @SuppressWarnings("unchecked")
	// public List<CtField<?>> getFields() {
	// return super.getFields();
	// }

	public CtTypeReference<?> getSuperclass() {
		return superClass;
	}

	public void setAnonymousExecutables(List<CtAnonymousExecutable> e) {
		anonymousExecutable.clear();
		anonymousExecutable.addAll(e);
	}

	public void setConstructors(Set<CtConstructor<T>> constructors) {
		this.constructors = constructors;
	}

	public void setSuperclass(CtTypeReference<?> superClass) {
		this.superClass = superClass;
	}

	@Override
	public Set<CtAnnotation<? extends Annotation>> getAnnotations() {
		Set<CtAnnotation<? extends Annotation>> annot = super.getAnnotations();

		if (getSuperclass() != null) {
			CtSimpleType<?> sup = getSuperclass().getDeclaration();
			if (sup != null) {
				for (CtAnnotation<? extends Annotation> a : sup
						.getAnnotations()) {
					if (a.getAnnotationType().getAnnotation(Inherited.class) != null) {
						annot.add(a);
					}
				}
			}
		}
		return annot;
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

	public void insertAfter(CtStatementList<?> statements) {
		spoon.support.reflect.code.CtStatementImpl
				.insertAfter(this, statements);
	}

	public void insertBefore(CtStatement statement) {
		spoon.support.reflect.code.CtStatementImpl
				.insertBefore(this, statement);
	}

	public void insertBefore(CtStatementList<?> statements) {
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
}
