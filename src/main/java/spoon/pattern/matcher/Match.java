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
package spoon.pattern.matcher;

import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.pattern.ParameterValueProvider;
import spoon.reflect.declaration.CtElement;

/**
 * Represents a Match of TemplateMatcher
 */
public class Match {
	private final List<? extends Object> matchingElements;
	private final ParameterValueProvider parameters;

	Match(List<? extends Object> matches, ParameterValueProvider parameters) {
		this.parameters = parameters;
		this.matchingElements = matches;
	}

	public List<CtElement> getMatchingElements() {
		return getMatchingElements(CtElement.class);
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getMatchingElements(Class<T> clazz) {
		for (Object object : matchingElements) {
			if (object != null && clazz.isInstance(object) == false) {
				throw new SpoonException("Match contains a " + object.getClass() + " which cannot be cast to " + clazz);
			}
		}
		return (List<T>) matchingElements;
	}
	public CtElement getMatchingElement() {
		return getMatchingElement(CtElement.class, true);
	}

	public <T> T getMatchingElement(Class<T> clazz) {
		return getMatchingElement(clazz, true);
	}

	/**
	 * @param clazz the Class of returned element. throws SpoonException if matching value is not assignable to `clazz`
	 * @param failIfMany if there is more then one matching element and `failIfMany` == true, then it throws SpoonException.
	 * @return first matching element
	 */
	public <T> T getMatchingElement(Class<T> clazz, boolean failIfMany) {
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
	 * Replaces all matching elements with `newElements`
	 * @param newElements the elements which has to be used instead of matched element
	 */
	public void replaceMatchesBy(List<CtElement> newElements) {
		if (matchingElements.isEmpty()) {
			throw new SpoonException("Cannot replace empty list of elements");
		}
		CtElement last = null;
		for (CtElement oldElement : getMatchingElements(CtElement.class)) {
			if (last != null) {
				//delete all excluding last
				last.delete();
			}
			last = oldElement;
		}
		//replace last element
		last.replace(newElements);
	}

	public ParameterValueProvider getParameters() {
		return parameters;
	}

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
