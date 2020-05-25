/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * implementation of {@link RoleHandler}, which handles attributes of type List&lt;V&gt;
 * @param <T> the type of node whose attribute has to be manipulated
 * @param <V> the type of item value of the attribute
 */
abstract class ListHandler<T, V> extends AbstractRoleHandler<T, List<V>, V> {

	protected ListHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
		super(role, targetType, valueClass);
	}

	@Override
	public ContainerKind getContainerKind() {
		return ContainerKind.LIST;
	}

	@Override
	protected List<V> castValue(Object value) {
		List<V> list = super.castValue(value);
		//check that each item has expected class
		checkItemsClass(list);
		return list;
	}

	@Override
	public <W, X> java.util.Collection<X> asCollection(W element) {
		return asList(element);
	}

	@Override
	public <W, X> java.util.List<X> asList(W e) {
		return new AbstractList<X>() {
			T element = castTarget(e);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Iterator<X> iterator() {
				return (Iterator) ListHandler.this.iterator(element);
			}

			@Override
			public int size() {
				return ListHandler.this.size(element);
			}

			@SuppressWarnings("unchecked")
			@Override
			public X get(int index) {
				return (X) ListHandler.this.get(element, index);
			}

			@Override
			public X set(int index, X value) {
				return (X) ListHandler.this.set(element, index, castItemValue(value));
			}

			@Override
			public X remove(int index) {
				return (X) ListHandler.this.remove(element, index);
			}

			@Override
			public boolean add(X value) {
				return ListHandler.this.add(element, castItemValue(value));
			}

			@Override
			public boolean remove(Object value) {
				return ListHandler.this.remove(element, value);
			}
		};
	}

	protected boolean remove(T element, Object value) {
		List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
		boolean ret = values.remove(value);
		if (ret) {
			setValue(element, values);
		}
		return ret;
	}

	protected V remove(T element, int index) {
		List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
		V ret = values.remove(index);
		setValue(element, values);
		return ret;
	}

	protected boolean add(T element, V value) {
		List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
		boolean ret = values.add(value);
		setValue(element, values);
		return ret;
	}

	protected V get(T element, int index) {
		return this.<T, List<V>>getValue(element).get(index);
	}

	protected V set(T element, int index, V value) {
		List<V> values = new ArrayList<>(this.<T, List<V>>getValue(element));
		V ret = values.set(index, value);
		setValue(element, values);
		return ret;
	}

	protected int size(T element) {
		return this.<T, List<V>>getValue(element).size();
	}

	protected Iterator<V> iterator(T element) {
		return this.<T, List<V>>getValue(element).iterator();
	}
}
