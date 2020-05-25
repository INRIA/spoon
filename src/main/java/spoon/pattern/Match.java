/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern;

import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.support.util.ImmutableMap;

/**
 * Represents a single match of {@link Pattern}
 */
public class Match {
	private final List<?> matchingElements;
	private final ImmutableMap parameters;

	public Match(List<?> matches, ImmutableMap parameters) {
		this.parameters = parameters;
		this.matchingElements = matches;
	}
	/**
	 * @return {@link List} of elements, which match to the Pattern.
	 * Use {@link #getMatchingElement()} if the {@link Pattern} matches single root element.
	 * But when {@link Pattern} contains sequence of root elements, then this is the right way how to get them all
	 */
	public List<CtElement> getMatchingElements() {
		return getMatchingElements(CtElement.class);
	}
	/**
	 * Same like {@link #getMatchingElement()} but additionally it checks that each matching element is instance of `clazz`
	 * @param clazz the required type of all elements.
	 * @return a {@link List} typed to `clazz` or throws {@link SpoonException} if Pattern matched different elements
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getMatchingElements(Class<T> clazz) {
		for (Object object : matchingElements) {
			if (object != null && clazz.isInstance(object) == false) {
				throw new SpoonException("Match contains a " + object.getClass() + " which cannot be cast to " + clazz);
			}
		}
		return (List<T>) matchingElements;
	}

	/**
	 * @return a matching element of a {@link Pattern}
	 * It fails if {@link Pattern} is designed to match sequence of elements. In such case use {@link #getMatchingElements()}
	 */
	public CtElement getMatchingElement() {
		return getMatchingElement(CtElement.class, true);
	}
	/**
	 * Same like {@link #getMatchingElement()}, but checks that matching element is expected class and casts returned value to that type
	 * @param clazz required type
	 * @return matched element cast to `clazz`
	 */
	public <T> T getMatchingElement(Class<T> clazz) {
		return getMatchingElement(clazz, true);
	}

	/**
	 * @param clazz the Class of returned element. throws SpoonException if matching value is not assignable to `clazz`
	 * @param failIfMany if there is more then one matching element and `failIfMany` == true, then it throws SpoonException.
	 * @return first matching element
	 */
	private <T> T getMatchingElement(Class<T> clazz, boolean failIfMany) {
		if (matchingElements.isEmpty()) {
			return null;
		}
		if (failIfMany && matchingElements.size() != 1) {
			throw new SpoonException("There is more then one match");
		}
		Object object = matchingElements.get(0);
		if (object != null && clazz.isInstance(object) == false) {
			throw new SpoonException("Match contains a " + object.getClass() + " which cannot be cast to " + clazz);
		}
		return clazz.cast(object);
	}

	/**
	 * @return {@link ImmutableMap} with values of {@link Pattern} parameters, which fits to current match
	 */
	public ImmutableMap getParameters() {
		return parameters;
	}
	/**
	 * @return {@link Map} with values of {@link Pattern} parameters, which fits to current match
	 */
	public Map<String, Object> getParametersMap() {
		return parameters.asMap();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append(parameters.toString());
		sb.append("\n}\n----------");
		for (int i = 0; i < matchingElements.size(); i++) {
			sb.append("\n").append(i + 1).append(") ").append(matchingElements.get(i));
		}
		return sb.toString();
	}
}
