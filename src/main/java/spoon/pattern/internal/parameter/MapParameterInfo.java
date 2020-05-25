/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import java.util.Map;
import java.util.function.Function;

import spoon.support.util.ImmutableMap;
import spoon.support.util.ImmutableMapImpl;

/**
 * A kind of {@link ParameterInfo} which returns value by the named parameter
 * From a container of type {@link ImmutableMap} or {@link Map}
 */
public class MapParameterInfo extends AbstractParameterInfo {

	private final String name;

	public MapParameterInfo(String name) {
		this(name, null);
	}
	public MapParameterInfo(AbstractParameterInfo next) {
		this(null, next);
	}
	public MapParameterInfo(String name, AbstractParameterInfo next) {
		super(next);
		this.name = name;
	}

	@Override
	protected String getPlainName() {
		return getWrappedName(getContainerName());
	}

	@Override
	protected String getWrappedName(String containerName) {
		if (name == null) {
			return containerName;
		}
		if (!containerName.isEmpty()) {
			containerName += ".";
		}
		return containerName + name;
	}

	@Override
	protected Object addValueAs(Object container, Function<Object, Object> merger) {
		ImmutableMap parameters = castTo(container, ImmutableMap.class);
		if (name == null) {
			//This accessor matches any entry - has no predefined key
			Object newValue = merger.apply(null);
			if (newValue == null) {
				//The accessor has no key, so null value means null Entry so nothing to add. Keep existing map
				return parameters;
			}
			if (newValue == NO_MERGE) {
				return NO_MERGE;
			}
			if (newValue instanceof Map.Entry<?, ?>) {
				Map.Entry<?, ?> newEntry = (Map.Entry<?, ?>) newValue;
				String newEntryKey = (String) newEntry.getKey();
				Object existingValue = parameters.getValue(newEntryKey);
				Object newEntryValue = merge(existingValue, newEntry.getValue());
				if (newEntryValue == NO_MERGE) {
					return NO_MERGE;
				}
				if (existingValue == newEntryValue) {
					//it is already there
					return parameters;
				}
				return parameters.putValue(newEntryKey, newEntryValue);
			}
			if (newValue instanceof Map) {
				Map<String, Object> newMap = (Map) newValue;
				for (Map.Entry<String, Object> newEntry : newMap.entrySet()) {
					String newEntryKey = newEntry.getKey();
					Object existingValue = parameters.getValue(newEntryKey);
					Object newEntryValue = merge(existingValue, newEntry.getValue());
					if (newEntryValue == NO_MERGE) {
						return NO_MERGE;
					}
					if (existingValue != newEntryValue) {
						//it is not there yet. Add it
						parameters = parameters.putValue(newEntryKey, newEntryValue);
					}
					//it is there, continue to check next entry
				}
				return parameters;
			}
			//only Map.Entries can be added to the Map if there is missing key
			return NO_MERGE;
		}
		Object existingValue = parameters.getValue(name);
		Object newValue = merger.apply(existingValue);
		if (newValue == NO_MERGE) {
			return NO_MERGE;
		}
		if (existingValue == newValue) {
			//it is already there.
			return parameters;
		}
		return parameters.putValue(name, newValue);
	}

	@Override
	protected Object getValue(ImmutableMap parameters) {
		ImmutableMap map = castTo(super.getValue(parameters), ImmutableMap.class);
		return name == null ? map : map.getValue(name);
	}

	@Override
	protected <T> T castTo(Object o, Class<T> type) {
		if (o instanceof Map) {
			o = new ImmutableMapImpl((Map) o);
		}
		return super.castTo(o, type);
	}

	private static final ImmutableMap EMPTY = new ImmutableMapImpl();
	@Override
	protected ImmutableMap getEmptyContainer() {
		return EMPTY;
	}
}
