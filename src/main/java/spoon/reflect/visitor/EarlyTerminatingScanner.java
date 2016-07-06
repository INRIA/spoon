/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;

import java.util.Collection;

public class EarlyTerminatingScanner<T> extends CtScanner {

	private boolean terminate = false;
	private T result;

	protected void terminate() {
		terminate = true;
	}

	protected void setResult(T result) {
		this.result = result;
	}

	public T getResult() {
		return result;
	}

	@Override
	public void scan(Collection<? extends CtElement> elements) {
		if (terminate) {
			return;
		}
		super.scan(elements);
	}

	@Override
	public void scan(CtElement element) {
		if (terminate) {
			return;
		}
		super.scan(element);
	}

	@Override
	public void scan(Object o) {
		if (terminate) {
			return;
		}
		super.scan(o);
	}
}
