/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import spoon.support.util.ImmutableMap;

/**
 */
public class ListParameterInfo extends AbstractParameterInfo {

	private final int idx;

	public ListParameterInfo(ParameterInfo next) {
		this(-1, next);
	}
	public ListParameterInfo(int idx, ParameterInfo next) {
		super(next);
		this.idx = idx;
	}

	@Override
	protected String getPlainName() {
		return getWrappedName(getContainerName());
	}

	@Override
	protected String getWrappedName(String containerName) {
		if (idx < 0) {
			return containerName;
		}
		return containerName + "[" + idx + "]";
	}

	@Override
	protected Object addValueAs(Object container, Function<Object, Object> merger) {
		List<Object> list = castTo(container, List.class);
		Object existingValue = getExistingValue(list);
		Object newValue = merger.apply(existingValue);
		if (newValue == NO_MERGE) {
			return NO_MERGE;
		}
		if (existingValue == newValue) {
			//the value is already there. Keep existing list
			return list;
		}
		if (newValue == null) {
			//nothing to add. Keep existing list
			return list;
		}
		List<Object> newList = new ArrayList<>(list.size() + 1);
		newList.addAll(list);
		if (idx >= 0) {
			while (idx >= newList.size()) {
				newList.add(null);
			}
			newList.set(idx, newValue);
		} else {
			if (newValue instanceof Collection) {
				newList.addAll((Collection) newValue);
			} else {
				newList.add(newValue);
			}
		}
		return Collections.unmodifiableList(newList);
	}

	protected Object getExistingValue(List<Object> list) {
		if (list == null || idx < 0 || idx >= list.size()) {
			return null;
		}
		return list.get(idx);
	}
	@Override
	protected List<Object> getEmptyContainer() {
		return Collections.emptyList();
	}
	@Override
	protected Object getValue(ImmutableMap parameters) {
		List<Object> list = castTo(super.getValue(parameters), List.class);
		if (idx < 0) {
			return list;
		}
		if (idx < list.size()) {
			return list.get(idx);
		}
		return null;
	}
	@Override
	protected <T> T castTo(Object o, Class<T> type) {
		if (o instanceof Set) {
			o = new ArrayList<>((Set) o);
		} else if (o instanceof Object[]) {
			o = Arrays.asList((Object[]) o);
		}
		return super.castTo(o, type);
	}
}
