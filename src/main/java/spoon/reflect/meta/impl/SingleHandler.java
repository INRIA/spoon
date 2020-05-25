/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;

import java.util.AbstractList;
import java.util.Collections;

import spoon.SpoonException;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * implementation of {@link RoleHandler}
 * @param <T> the type of node whose attribute has to be manipulated
 * @param <U> the type of value of the attribute
 */
abstract class SingleHandler<T, U> extends AbstractRoleHandler<T, U, U> {

	protected SingleHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
		super(role, targetType, valueClass);
	}

	@Override
	public ContainerKind getContainerKind() {
		return ContainerKind.SINGLE;
	}

	@Override
	public <W, X> java.util.Collection<X> asCollection(W element) {
		return asList(element);
	}

	@Override
	public <W, X> java.util.List<X> asList(W e) {
		return new AbstractList<X>() {
			T element = castTarget(e);
			boolean hasValue = SingleHandler.this.getValue(element) != null;

			@Override
			public int size() {
				return hasValue ? 1 : 0;
			}

			@SuppressWarnings("unchecked")
			@Override
			public X get(int index) {
				if (index < 0 || index >= size()) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
				}
				return (X) SingleHandler.this.getValue(element);
			}

			@Override
			public X set(int index, X value) {
				if (index < 0 || index >= size()) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
				}
				X oldValue = get(0);
				SingleHandler.this.setValue(element, value);
				return oldValue;
			}

			@Override
			public boolean add(X value) {
				if (hasValue) {
					//single value cannot have more then one value
					throw new SpoonException("Single value attribute cannot have more then one value");
				}
				SingleHandler.this.setValue(element, value);
				hasValue = true;
				return true;
			}

			@Override
			public X remove(int index) {
				if (index < 0 || index >= size()) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
				}
				X oldValue = get(0);
				if (oldValue != null) {
					SingleHandler.this.setValue(element, null);
				}
				hasValue = false;
				return oldValue;
			}

			@Override
			public boolean remove(Object value) {
				if (hasValue == false) {
					return false;
				}
				X oldValue = get(0);
				if (equals(oldValue, value)) {
					if (oldValue != null) {
						SingleHandler.this.setValue(element, null);
					}
					hasValue = false;
					return true;
				}
				return false;
			}

			private boolean equals(Object v1, Object v2) {
				if (v1 == v2) {
					return true;
				}
				if (v1 == null) {
					return false;
				}
				return v1.equals(v2);
			}
		};
	}

	@Override
	public <W, X> java.util.Set<X> asSet(W element) {
		return Collections.<X>singleton(getValue(element));
	}
}
