/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util;

import spoon.support.Internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Internal class only, not in the public API.
 *
 * Spoon implementation of {@link ImmutableMap}
 */
@Internal
public class ImmutableMapImpl implements ImmutableMap {

	protected final ImmutableMap parent;
	protected final Map<String, Object> map;

	public ImmutableMapImpl(Map<String, Object> map) {
		this(null, map);
	}
	private ImmutableMapImpl(ImmutableMap parent, Map<String, Object> map) {
		this.parent = parent;
		this.map = Collections.unmodifiableMap(map);
	}

	public ImmutableMapImpl(Map<String, Object> map, String parameterName, Object value) {
		this(null, map, parameterName, value);
	}

	private ImmutableMapImpl(ImmutableMap parent, Map<String, Object> map, String parameterName, Object value) {
		this.parent = null;
		Map<String, Object> copy = new HashMap<>(map.size() + 1);
		copy.putAll(map);
		copy.put(parameterName, value);
		this.map = Collections.unmodifiableMap(copy);
	}

	public ImmutableMapImpl() {
		this.parent = null;
		this.map = Collections.emptyMap();
	}

	@Override
	public ImmutableMapImpl checkpoint() {
		return new ImmutableMapImpl(this, Collections.emptyMap());
	}

	@Override
	public boolean hasValue(String parameterName) {
		if (map.containsKey(parameterName)) {
			return true;
		}
		if (parent != null) {
			return parent.hasValue(parameterName);
		}
		return false;
	}

	@Override
	public Object getValue(String parameterName) {
		Object v = map.get(parameterName);
		if (v == null && parent != null) {
			v = parent.getValue(parameterName);
		}
		return v;
	}

	@Override
	public ImmutableMap putValue(String parameterName, Object value) {
		return new ImmutableMapImpl(parent, map, parameterName, value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendMap(sb, map);
		if (parent != null) {
			sb.append("\nparent:\n");
			sb.append(parent.toString());
		}
		return sb.toString();
	}

	private static void appendMap(StringBuilder sb, Map<String, Object> map) {
		List<String> paramNames = new ArrayList<>(map.keySet());
		paramNames.sort((a, b) -> a.compareTo(b));
		for (String name : paramNames) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(name).append('=').append(map.get(name));
		}
	}

	@Override
	public Map<String, Object> asMap() {
		if (parent != null) {
			Map<String, Object> merged = new HashMap<>();
			merged.putAll(parent.asMap());
			merged.putAll(map);
			return Collections.unmodifiableMap(merged);
		}
		return map;
	}

	@Override
	public Map<String, Object> getModifiedValues() {
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImmutableMap) {
			obj = ((ImmutableMap) obj).asMap();
		}
		if (obj instanceof Map) {
			Map map = (Map) obj;
			return asMap().equals(map);
		}
		return false;
	}
}
