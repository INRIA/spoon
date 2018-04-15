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
package spoon.pattern.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides value of parameter
 */
public class UnmodifiableParameterValueProvider implements ParameterValueProvider {

	public static class Factory implements ParameterValueProviderFactory {
		public static final Factory INSTANCE = new Factory();
		@Override
		public ParameterValueProvider createParameterValueProvider() {
			return new UnmodifiableParameterValueProvider();
		}
	}

	protected final ParameterValueProvider parent;
	protected final Map<String, Object> map;

	public UnmodifiableParameterValueProvider(Map<String, Object> map) {
		this(null, map);
	}
	private UnmodifiableParameterValueProvider(ParameterValueProvider parent, Map<String, Object> map) {
		this.parent = parent;
		this.map = Collections.unmodifiableMap(map);
	}

	public UnmodifiableParameterValueProvider(Map<String, Object> map, String parameterName, Object value) {
		this(null, map, parameterName, value);
	}

	private UnmodifiableParameterValueProvider(ParameterValueProvider parent, Map<String, Object> map, String parameterName, Object value) {
		this.parent = null;
		Map<String, Object> copy = new HashMap<>(map.size() + 1);
		copy.putAll(map);
		copy.put(parameterName, value);
		this.map = Collections.unmodifiableMap(copy);
	}

	public UnmodifiableParameterValueProvider() {
		this.parent = null;
		this.map = Collections.emptyMap();
	}

	@Override
	public UnmodifiableParameterValueProvider createLocalParameterValueProvider() {
		return new UnmodifiableParameterValueProvider(this, Collections.emptyMap());
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
	public ParameterValueProvider putValueToCopy(String parameterName, Object value) {
		return new UnmodifiableParameterValueProvider(parent, map, parameterName, value);
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
	public Map<String, Object> asLocalMap() {
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParameterValueProvider) {
			obj = ((ParameterValueProvider) obj).asMap();
		}
		if (obj instanceof Map) {
			Map map = (Map) obj;
			return asMap().equals(map);
		}
		return false;
	}
}
