/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
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
	public CtTypeAccess<A> clone() {
		return (CtTypeAccess<A>) super.clone();
	}
}
