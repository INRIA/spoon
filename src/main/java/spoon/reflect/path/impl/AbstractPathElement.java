/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Partial implementation for CtPathElement
 */
public abstract class AbstractPathElement<P extends CtElement, T extends CtElement> implements CtPathElement<P, T> {

	private Map<String, String> arguments = new TreeMap<>();

	public Map<String, String> getArguments() {
		return arguments;
	}

	@Override
	public <C extends CtPathElement<P, T>> C addArgument(String key, String value) {
		arguments.put(key, value);
		return (C) this;
	}

	Collection<CtElement> getChilds(CtElement element) {
		final Collection<CtElement> elements = new ArrayList<>();
		if (element != null) {
			element.accept(new CtScanner() {
				@Override
				public void scan(CtElement element) {
					elements.add(element);
				}
			});
		}
		return elements;
	}

	protected String getParamString() {
		if (arguments.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder("[");

		for (Iterator<Map.Entry<String, String>> iter = arguments.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> value = iter.next();
			builder.append(value.getKey() + "=" + value.getValue());
			if (iter.hasNext()) {
				builder.append(";");
			}
		}

		return builder.append("]").toString();
	}
}
