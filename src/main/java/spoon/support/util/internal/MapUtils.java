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
