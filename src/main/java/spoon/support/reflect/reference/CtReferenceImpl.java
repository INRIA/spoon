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
package spoon.support.reflect.reference;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;

public abstract class CtReferenceImpl extends CtElementImpl implements CtReference, Serializable {

	private static final long serialVersionUID = 1L;

	String simplename;

	public CtReferenceImpl() {
		super();
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
	public String toString() {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(
				getFactory().getEnvironment());
		printer.scan(this);
		return printer.toString();
	}

	@Override
	public void accept(CtVisitor visitor) {
		throw new UnsupportedOperationException("Must be implemented in subclasses.");
	}
}
