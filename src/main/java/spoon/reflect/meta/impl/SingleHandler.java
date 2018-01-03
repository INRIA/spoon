/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.meta.impl;

import java.util.AbstractList;
import java.util.Collections;

import spoon.SpoonException;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;

abstract class SingleHandler<T, U> extends AbstractRoleHandler<T, U, U> {

	protected SingleHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
		super(role, targetType, valueClass);
	}

	@Override
	public ContainerKind getContainerKind() {
		return ContainerKind.SINGLE;
	}

	public <W, X> java.util.Collection<X> asCollection(W element) {
		return asList(element);
	};

	public <W, X> java.util.List<X> asList(W e) {
//		return Collections.<X>singletonList(getValue(element));
		return new AbstractList<X>() {
			T element = castTarget(e);

			@Override
			public int size() {
				return SingleHandler.this.getValue(element) == null ? 0 : 1;
			}

			@SuppressWarnings("unchecked")
			@Override
			public X get(int index) {
				if (index != 0) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
				}
				return (X) SingleHandler.this.getValue(element);
			}

			@Override
			public X set(int index, X value) {
				if (index != 0) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
				}
				X oldValue = get(0);
				SingleHandler.this.setValue(element, value);
				return (X) oldValue;
			}
			@Override
			public boolean add(X value) {
				if (value == null) {
					return false;
				}
				X oldValue = get(0);
				if (oldValue != null) {
					if (oldValue.equals(value)) {
						return false;
					}
					//single value cannot have more then one value
					throw new SpoonException("Single value attribute cannot have more then one value");
				}
				SingleHandler.this.setValue(element, value);
				return true;
			}

			@Override
			public boolean remove(Object value) {
				if (value == null) {
					return false;
				}
				X oldValue = get(0);
				if (value.equals(oldValue)) {
					SingleHandler.this.setValue(element, null);
					return true;
				}
				return false;
			}
		};
	};

	public <W, X> java.util.Set<X> asSet(W element) {
		return Collections.<X>singleton(getValue(element));
	};
}
