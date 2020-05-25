/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;

import static spoon.reflect.path.CtRole.NAME;

public abstract class CtNamedElementImpl extends CtElementImpl implements CtNamedElement {

	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = NAME)
	String simpleName = "";

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
		if (factory == null) {
			this.simpleName = simpleName;
			return (T) this;
		}
		if (factory instanceof FactoryImpl) {
			simpleName = ((FactoryImpl) factory).dedup(simpleName);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simpleName, this.simpleName);
		this.simpleName = simpleName;
		return (T) this;
	}

	@Override
	public CtNamedElement clone() {
		return (CtNamedElement) super.clone();
	}
}
