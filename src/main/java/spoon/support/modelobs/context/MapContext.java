/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.modelobs.context;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

import java.util.Map;

/**
 * defines the map context
 * @param <K>
 * @param <V>
 */
public class MapContext<K, V> extends Context {
	private final Map<K, V> map;
	private  K key;

	public MapContext(CtElement element, CtRole role, Map<K, V> map) {
		super(element, role);
		this.map = map;
	}

	public MapContext(CtElement element, CtRole role, Map<K, V> map, K key) {
		this(element, role, map);
		this.key = key;
	}

	/**
	 * get the changed key
	 * @return the changed key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * the changed map
	 * @return the changed map
	 */
	public Map<K, V> getMap() {
		return map;
	}
}
