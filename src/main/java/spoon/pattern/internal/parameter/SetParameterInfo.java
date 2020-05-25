/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import spoon.support.util.ImmutableMap;

/**
 */
public class SetParameterInfo extends AbstractParameterInfo {

	public SetParameterInfo(AbstractParameterInfo next) {
		super(next);
	}

	@Override
	protected String getPlainName() {
		return getWrappedName(getContainerName());
	}

	@Override
	protected String getWrappedName(String containerName) {
		return containerName;
	}

	@Override
	protected Object addValueAs(Object container, Function<Object, Object> merger) {
		Set<Object> set = castTo(container, Set.class);
		Object newValue = merger.apply(null);
		if (newValue == NO_MERGE) {
			return NO_MERGE;
		}
		if (newValue == null) {
			//nothing to add. Keep existing set
			return set;
		}
		if (set.contains(newValue)) {
			//the value is already there
			return set;
		}
		Set<Object> newSet = new LinkedHashSet<>(set.size() + 1);
		newSet.addAll(set);
		if (newValue instanceof Collection) {
			if (newSet.addAll((Collection) newValue) == false) {
				//all the values were already there. Return original set
				return set;
			}
		} else {
			newSet.add(newValue);
		}
		return Collections.unmodifiableSet(newSet);
	}

	@Override
	protected Set<Object> getEmptyContainer() {
		return Collections.emptySet();
	}
	@Override
	protected Object getValue(ImmutableMap parameters) {
		return castTo(super.getValue(parameters), Set.class);
	}

	@Override
	protected <T> T castTo(Object o, Class<T> type) {
		if (o instanceof List) {
			o = new LinkedHashSet<>((List) o);
		} else if (o instanceof Object[]) {
			o = new LinkedHashSet<>(Arrays.asList((Object[]) o));
		}
		return super.castTo(o, type);
	}
}
