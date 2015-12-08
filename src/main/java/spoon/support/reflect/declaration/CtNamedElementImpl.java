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
package spoon.support.reflect.declaration;

import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;

public abstract class CtNamedElementImpl extends CtElementImpl implements CtNamedElement {

	private static final long serialVersionUID = 1L;

	String simpleName;

	@Override
	public CtReference getReference() {
		return null;
	}

	@Override
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		Factory factory = getFactory();
		if (factory instanceof FactoryImpl) {
			simpleName = ((FactoryImpl) factory).dedup(simpleName);
		}
		this.simpleName = simpleName;
		return (T) this;
	}
}
