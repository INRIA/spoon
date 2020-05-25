/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASTS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.CAST;
import static spoon.reflect.path.CtRole.TYPE;

public abstract class CtExpressionImpl<T> extends CtCodeElementImpl implements CtExpression<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = TYPE)
	CtTypeReference<T> type;

	@MetamodelPropertyField(role = CAST)
	List<CtTypeReference<?>> typeCasts = emptyList();

	@Override
	public CtTypeReference<T> getType() {
		return type;
	}

	@Override
	public List<CtTypeReference<?>> getTypeCasts() {
		return typeCasts;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C setTypeCasts(List<CtTypeReference<?>> casts) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CAST, this.typeCasts, new ArrayList<>(this.typeCasts));
		if (casts == null || casts.isEmpty()) {
			this.typeCasts = CtElementImpl.emptyList();
			return (C) this;
		}
		if (this.typeCasts == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.typeCasts = new ArrayList<>(CASTS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.typeCasts.clear();
		for (CtTypeReference<?> cast : casts) {
			addTypeCast(cast);
		}
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C addTypeCast(CtTypeReference<?> type) {
		if (type == null) {
			return (C) this;
		}
		if (typeCasts == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			typeCasts = new ArrayList<>(CASTS_CONTAINER_DEFAULT_CAPACITY);
		}
		type.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CAST, this.typeCasts, type);
		typeCasts.add(type);
		return (C) this;
	}

	@Override
	public T S() {
		return null;
	}

	@Override
	public CtExpression<T> clone() {
		return (CtExpression<T>) super.clone();
	}
}
