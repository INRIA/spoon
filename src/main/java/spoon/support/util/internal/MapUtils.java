/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MapUtils {

	private MapUtils() {
	}

	/**
	 * @return existing value of `key` from `map`. If value doesn't exist yet for `key` yet,
	 * then `valueCreator` is used to create new value, which is then assigned to `key` and returned
	 */
	public static <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> valueCreator) {
		return getOrCreate(map, key, valueCreator, null);
	}
	/**
	 * @param initializer is called immediately after the value is added to the map
	 * @return existing value of `key` from `map`. If value doesn't exist yet for `key` yet,
	 * then `valueCreator` is used to create new value, which is then assigned to `key` and returned
	 */
	public static <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> valueCreator, Consumer<V> initializer) {
		V value = map.get(key);
		if (value == null) {
			value = valueCreator.get();
			map.put(key, value);
			if (initializer != null) {
				initializer.accept(value);
			}
		}
		return value;
	}
}
