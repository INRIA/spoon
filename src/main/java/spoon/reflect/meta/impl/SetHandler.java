/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * implementation of {@link RoleHandler}, which handles attributes of type Set&lt;V&gt;
 * @param <T> the type of node whose attribute has to be manipulated
 * @param <V> the type of item value of the attribute
 */
abstract class SetHandler<T, V> extends AbstractRoleHandler<T, Set<V>, V> {

	protected SetHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
		super(role, targetType, valueClass);
	}

	@Override
	public ContainerKind getContainerKind() {
		return ContainerKind.SET;
	}

	@Override
	protected Set<V> castValue(Object value) {
		Set<V> set = super.castValue(value);
		//check that each item has expected class
		checkItemsClass(set);
		return set;
	}

	@Override
	public <W, X> Collection<X> asCollection(W element) {
		return asSet(element);
	}

	@Override
	public <W, X> Set<X> asSet(W e) {
		return new AbstractSet<X>() {
			T element = castTarget(e);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Iterator<X> iterator() {
				return (Iterator) SetHandler.this.iterator(element);
			}

			@Override
			public int size() {
				return SetHandler.this.size(element);
			}

			@Override
			public boolean contains(Object o) {
				return SetHandler.this.contains(element, o);
			}

			@Override
			public boolean add(X value) {
				return SetHandler.this.add(element, castItemValue(value));
			}

			@Override
			public boolean remove(Object value) {
				return SetHandler.this.remove(element, value);
			}
		};
	}

	protected boolean remove(T element, Object value) {
		Set<V> values = new HashSet<>(this.<T, Set<V>>getValue(element));
		boolean ret = values.remove(value);
		if (ret) {
			setValue(element, values);
		}
		return false;
	}

	protected boolean add(T element, V value) {
		Set<V> values = new HashSet<>(this.<T, Set<V>>getValue(element));
		boolean ret = values.add(value);
		if (ret) {
			setValue(element, values);
		}
		return ret;
	}

	protected boolean contains(T element, Object o) {
		return this.<T, Set<V>>getValue(element).contains(o);
	}

	protected int size(T element) {
		return this.<T, Set<V>>getValue(element).size();
	}

	protected Iterator<V> iterator(T element) {
		return this.<T, Set<V>>getValue(element).iterator();
	}
}
