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
package spoon.support.reflect.code;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASTS_CONTAINER_DEFAULT_CAPACITY;

public abstract class CtExpressionImpl<T> extends CtCodeElementImpl implements
		CtExpression<T> {
	private static final long serialVersionUID = 1L;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> typeCasts = emptyList();

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
		this.type = type;
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C setTypeCasts(List<CtTypeReference<?>> casts) {
		if (this.typeCasts == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			this.typeCasts = new ArrayList<CtTypeReference<?>>(
					CASTS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.typeCasts.clear();
		for (CtTypeReference<?> cast : casts) {
			addTypeCast(cast);
		}
		return (C) this;
	}

	@Override
	public <C extends CtExpression<T>> C addTypeCast(CtTypeReference<?> type) {
		if (typeCasts == CtElementImpl.<CtTypeReference<?>>emptyList()) {
			typeCasts = new ArrayList<CtTypeReference<?>>(
					CASTS_CONTAINER_DEFAULT_CAPACITY);
		}
		type.setParent(this);
		typeCasts.add(type);
		return (C) this;
	}

	@Override
	public <E extends T> void replace(CtExpression<E> element) {
		super.replace(element);
	}

	@Override
	public T S() {
		return null;
	}
}
