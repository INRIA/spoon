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

package spoon.support.reflect.reference;

import spoon.Launcher;
import spoon.processing.FactoryAccessor;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.reflect.declaration.CtUncomparableException;
import spoon.support.util.RtHelper;
import spoon.support.visitor.SignaturePrinter;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public abstract class CtReferenceImpl implements CtReference, Serializable, Comparable<CtReference> {

	private static final long serialVersionUID = 1L;

	String simplename;

	Object parent;

	transient Factory factory;

	public CtReferenceImpl() {
		super();
	}

	@Override
	public int compareTo(CtReference o) {
		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		String current = pr.getSignature();
		pr.reset();
		pr.scan(o);
		return current.compareTo(pr.getSignature());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof CtReference && compareTo((CtReference) object) == 0;
	}

	protected abstract AnnotatedElement getActualAnnotatedElement();

	@Override
	public String getSimpleName() {
		return simplename;
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		if (simplename.contains("?")) {
			throw new RuntimeException("argl");
		}
		Factory factory = getFactory();
		if (factory instanceof FactoryImpl) {
			simplename = ((FactoryImpl) factory).dedup(simplename);
		}
		this.simplename = simplename;
		return (T) this;
	}

	@Override
	public Object getParent() throws ParentNotInitializedException {
		if (parent == null) {
			throw new ParentNotInitializedException("parent not initialized for " + getSimpleName() + "(" + this.getClass() + ")");
		}
		return parent;
	}

	@Override
	public <E extends CtReference> E setParent(Object parent) {
		this.parent = parent;
		return (E) this;
	}

	@Override
	public String toString() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(
				getFactory().getEnvironment());
		printer.scan(this);
		return printer.toString();
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	protected void replace(CtReference reference) {
		try {
			replaceIn(this, reference, getParent());
		} catch (CtUncomparableException e1) {
			// do nothing
		} catch (Exception e1) {
			Launcher.LOGGER.error(e1.getMessage(), e1);
		}
	}

	private <T extends FactoryAccessor> void replaceIn(Object toReplace, T replacement, Object parent) throws IllegalArgumentException, IllegalAccessException {

		for (Field f : RtHelper.getAllFields(parent.getClass())) {
			f.setAccessible(true);
			Object tmp = f.get(parent);

			if (tmp != null) {
				if (tmp instanceof List) {
					@SuppressWarnings("unchecked") List<T> lst = (List<T>) tmp;
					for (int i = 0; i < lst.size(); i++) {
						if (lst.get(i) != null && compare(lst.get(i), toReplace)) {
							lst.remove(i);
							if (replacement != null) {
								lst.add(i, getReplacement(replacement, parent));
							}
						}
					}
				} else if (tmp instanceof Collection) {
					@SuppressWarnings("unchecked") Collection<T> collect = (Collection<T>) tmp;
					Object[] array = collect.toArray();
					for (Object obj : array) {
						if (compare(obj, toReplace)) {
							collect.remove(obj);
							collect.add(getReplacement(replacement, parent));
						}
					}
				} else if (compare(tmp, toReplace)) {
					f.set(parent, getReplacement(replacement, parent));
				}
			}
		}
	}

	private <T extends FactoryAccessor> T getReplacement(T replacement, Object parent) {
		// T ret = replacement.getFactory().Core().clone(replacement);
		if (replacement instanceof CtReference && parent instanceof CtReference) {
			((CtReference) replacement).setParent(parent);
		}
		return replacement;
	}

	private boolean compare(Object o1, Object o2) {
		return o1 == o2;
	}
}
