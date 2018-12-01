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
package spoon.reflect.visitor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

/**
 * Maps names to CtElements, which are visible at current scanning place
 */
abstract class AbstractNameScope implements NameScope {

	private final NameScope parent;
	private final CtElement scopeElement;

	protected AbstractNameScope(NameScope parent, CtElement scopeElement) {
		this.parent = parent;
		this.scopeElement = scopeElement;
	}

	/**
	 * @return the {@link CtElement} which represents the current scope
	 */
	@Override
	public final CtElement getScopeElement() {
		return scopeElement;
	}

	@Override
	public final Optional<NameScope> getParent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * @param name to be searched simple name
	 * @param consumer is called for each named element with same name which are accessible from this {@link AbstractNameScope}
	 * 	as long as there are some elements and consumer returns null. If `consumer` return not null value then it is returned
	 * @return the value returned by `consumer` or null
	 */
	@Override
	public final <T> T forEachElementByName(String name, Function<? super CtNamedElement, T> consumer) {
		T r = forEachLocalElementByName(name, consumer);
		if (r != null) {
			return r;
		}
		if (scopeElement instanceof CtNamedElement) {
			CtNamedElement named = (CtNamedElement) scopeElement;
			if (name.equals(named.getSimpleName())) {
				r = consumer.apply(named);
				if (r != null) {
					return r;
				}
			}
		}
		if (parent != null) {
			return parent.forEachElementByName(name, consumer);
		}
		return null;
	}

	protected abstract <T> T forEachLocalElementByName(String name, Function<? super CtNamedElement, T> consumer);

	protected static <T> T forEachByName(Map<String, CtNamedElement> map, String name, Function<? super CtNamedElement, T> consumer) {
		CtNamedElement named = map.get(name);
		if (named != null) {
			return consumer.apply(named);
		}
		return null;
	}
}
