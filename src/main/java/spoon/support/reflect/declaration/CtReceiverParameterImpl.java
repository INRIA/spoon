/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtReceiverParameterImpl extends CtElementImpl implements CtReceiverParameter   {

	@MetamodelPropertyField(role = CtRole.TYPE)
	private CtTypeReference<Object> type;
	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	private boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <C extends CtShadowable> C setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (C) this;
	}
	@Override
	public CtTypeReference<Object> getType() {
		return type;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtReceiverParameter(this);
	}

	@Override
	public CtReceiverParameter clone() {
		return (CtReceiverParameter) super.clone();
	}
}
