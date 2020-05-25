/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * implementation of {@link RoleHandler}, which handles attributes of type Map&lt;String, V&gt;
 * @param <T> the type of node whose attribute has to be manipulated
 * @param <V> the type of item value of the attribute
 */
abstract class MapHandler<T, V> extends AbstractRoleHandler<T, Map<String, V>, V> {

	protected MapHandler(CtRole role, Class<T> targetType, Class<?> valueClass) {
		super(role, targetType, valueClass);
	}

	@Override
	public ContainerKind getContainerKind() {
		return ContainerKind.MAP;
	}

	@Override
	protected Map<String, V> castValue(Object value) {
		Map<String, V> map = super.castValue(value);
		//check that each item has expected class
		checkItemsClass(map.values());
		return map;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <W, X> java.util.Collection<X> asCollection(W element) {
		return (Collection) asMap(element).values();
	}

	@Override
	public <W, X> Map<String, X> asMap(W e) {
		// TODO Auto-generated method stub
		return new AbstractMap<String, X>() {
			T element = castTarget(e);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Set<Map.Entry<String, X>> entrySet() {
				return (Set) MapHandler.this.entrySet(element);
			}

			@SuppressWarnings("unchecked")
			@Override
			public X get(Object key) {
				return (X) MapHandler.this.get(element, key);
			}

			@SuppressWarnings("unchecked")
			@Override
			public X put(String key, X value) {
				return (X) MapHandler.this.put(element, key, castItemValue(value));
			}
		};
	}

	protected V get(T element, Object key) {
		return this.<T, Map<String, V>>getValue(element).get(key);
	}

	protected V put(T element, String key, V value) {
		Map<String, V> values = new LinkedHashMap<>(this.<T, Map<String, V>>getValue(element));
		V ret = values.put(key, value);
		setValue(element, values);
		return ret;
	}

	protected Set<Map.Entry<String, V>> entrySet(T element) {
		return this.<T, Map<String, V>>getValue(element).entrySet();
	}
}
