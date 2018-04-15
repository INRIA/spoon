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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 */
public class ListAccessor extends AbstractItemAccessor {

	private final int idx;

	public ListAccessor(ParameterInfo next) {
		this(-1, next);
	}
	public ListAccessor(int idx, ParameterInfo next) {
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
	protected Object getValue(ParameterValueProvider parameters) {
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
