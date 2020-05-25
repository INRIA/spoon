/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.ACCESSED_TYPE;

public class CtTypeAccessImpl<A> extends CtExpressionImpl<Void> implements CtTypeAccess<A> {

	@MetamodelPropertyField(role = ACCESSED_TYPE)
	private CtTypeReference<A> type;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeAccess(this);
	}

	@Override
	public CtTypeReference<A> getAccessedType() {
		return type;
	}

	@Override
	public <C extends CtTypeAccess<A>> C setAccessedType(CtTypeReference<A> accessedType) {
		if (accessedType != null) {
			accessedType.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, ACCESSED_TYPE, accessedType, this.type);
		type = accessedType;
		return (C) this;
	}

	@Override
	public CtTypeReference<Void> getType() {
		return (CtTypeReference<Void>) getFactory().Type().VOID_PRIMITIVE.clone().<CtTypeAccess>setParent(this);
	}

	@Override
	@UnsettableProperty
	public <C extends CtTypedElement> C setType(CtTypeReference<Void> type) {
		// type is used in setAccessedType now.
		return (C) this;
	}

	@Override
	@DerivedProperty
	public boolean isImplicit() {
		if (type != null) {
			return type.isImplicit();
		}
		return false;
	}

	@Override
	@DerivedProperty
	public <E extends CtElement> E setImplicit(boolean implicit) {
		if (type != null) {
			type.setImplicit(implicit);
		}
		return (E) this;
	}

	@Override
	public CtTypeAccess<A> clone() {
		return (CtTypeAccess<A>) super.clone();
	}
}
