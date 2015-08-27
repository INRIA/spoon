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

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.visitor.SignaturePrinter;

public abstract class CtReferenceImpl implements CtReference, Serializable, Comparable<CtReference> {

	private static final long serialVersionUID = 1L;

	String simplename;

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

	abstract protected AnnotatedElement getActualAnnotatedElement();

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
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}
}
