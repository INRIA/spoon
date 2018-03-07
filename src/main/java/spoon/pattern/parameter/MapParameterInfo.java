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

import java.util.Map;
import java.util.function.Function;

/**
 * A kind of {@link ParameterInfo} which returns value by the named parameter
 * From a container of type {@link ParameterValueProvider} or {@link Map}
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
		if (containerName.length() > 0) {
			containerName += ".";
		}
		return containerName + name;
	}

	@Override
	protected Object addValueAs(Object container, Function<Object, Object> merger) {
		ParameterValueProvider parameters = castTo(container, ParameterValueProvider.class);
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
				return parameters.putValueToCopy(newEntryKey, newEntryValue);
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
						parameters = parameters.putValueToCopy(newEntryKey, newEntryValue);
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
		return parameters.putValueToCopy(name, newValue);
	}

	@Override
	protected Object getValue(ParameterValueProvider parameters) {
		ParameterValueProvider map = castTo(super.getValue(parameters), ParameterValueProvider.class);
		return name == null ? map : map.getValue(name);
	}

	@Override
	protected <T> T castTo(Object o, Class<T> type) {
		if (o instanceof Map) {
			o = new UnmodifiableParameterValueProvider((Map) o);
		}
		return super.castTo(o, type);
	}

	private static final ParameterValueProvider EMPTY = new UnmodifiableParameterValueProvider();
	@Override
	protected ParameterValueProvider getEmptyContainer() {
		return EMPTY;
	}
}
