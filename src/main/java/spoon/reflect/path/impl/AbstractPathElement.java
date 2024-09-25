/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * Partial implementation for CtPathElement
 */
public abstract class AbstractPathElement<P extends CtElement, T extends CtElement> implements CtPathElement<P, T> {
	public static final String ARGUMENT_START = "[";
	public static final String ARGUMENT_END = "]";
	public static final String ARGUMENT_NAME_SEPARATOR = "=";

	private Map<String, String> arguments = new TreeMap<>();

	public Map<String, String> getArguments() {
		return arguments;
	}

	@Override
	public <C extends CtPathElement<P, T>> C addArgument(String key, String value) {
		arguments.put(key, value);
		return (C) this;
	}

	Collection<CtElement> getChildren(CtElement element) {
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
		StringJoiner joiner = new StringJoiner(";", "[", "]");
		for (Map.Entry<String, String> entry : arguments.entrySet()) {
			joiner.add(entry.getKey() + "=" + entry.getValue());
		}
		return joiner.toString();
	}
}
