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
