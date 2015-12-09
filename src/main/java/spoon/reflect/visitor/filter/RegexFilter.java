/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

	public boolean matches(E element) {
		Matcher m = regex.matcher(element.toString());
		return m.matches();
	}

	public Class<CtElement> getType() {
		return CtElement.class;
	}
}
