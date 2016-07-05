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
package spoon.reflect.builder;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

public abstract class AbsBuilder<T extends CtElement, C extends AbsBuilder<T, C, P>,  P extends AbsBuilder<?, ?, ? extends AbsBuilder>> {
	private final Factory factory;
	private T element;
	private P parent;

	protected AbsBuilder(Factory factory, T element) {
		this.factory = factory;
		this.element = element;
	}

	protected T getElement() {
		return element;
	}

	protected void setElement(T element) {
		this.element = element;
	}

	protected Factory getFactory() {
		return factory;
	}

	protected P getParent() {
		return parent;
	}

	protected void setParent(P parent) {
		this.parent = parent;
	}

	public T build() {
		return element;
	}

	public P close() {
		if (getParent() != null) {
			getParent().end(this);
		}
		return getParent();
	}

	public void end(AbsBuilder child) {

	}

	@Override
	public String toString() {
		return build().toString();
	}
}
