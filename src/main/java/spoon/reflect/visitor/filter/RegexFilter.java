/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Filters elements with a regular expression on the element's code. Example:
 *
 * <pre>
 * CtFieldAccess thisAccess = type.getElements(new ExpressionFilter(&quot;this&quot;))
 * 		.get(0);
 * </pre>
 */
public class RegexFilter<E extends CtElement> implements Filter<E> {
	private final Pattern regex;

	public RegexFilter(String regex) {
		if (regex == null) {
			throw new IllegalArgumentException();
		}
		this.regex = Pattern.compile(regex);
	}

	@Override
	public boolean matches(E element) {
		Matcher m = regex.matcher(element.toString());
		return m.matches();
	}

	public Class<CtElement> getType() {
		return CtElement.class;
	}
}
