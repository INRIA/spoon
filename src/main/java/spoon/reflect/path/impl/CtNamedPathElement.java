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
package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A CtPathElement that match on CtNamedElement#getSimpleName
 */
public class CtNamedPathElement extends AbstractPathElement<CtElement, CtElement> {
	public static final String STRING = ".";
	public static final String WILDCARD = "*";
	public static final String RECURSIVE_WILDCARD = "**";

	private final String pattern;
	private final Pattern rePattern;

	public CtNamedPathElement(String pattern) {
		this(pattern, true);
	}

	private static Set<String> failingPatterns = new HashSet<>();

	public CtNamedPathElement(String pattern, boolean canBeRegexp) {
		this.pattern = pattern;
		Pattern p = null;
		if (canBeRegexp && canBeRegExpPattern(pattern) && !failingPatterns.contains(pattern)) {
			try {
				p = Pattern.compile(pattern);
			} catch (PatternSyntaxException e) {
				failingPatterns.add(pattern);
			}
		}
		this.rePattern = p;
	}

	private boolean canBeRegExpPattern(String str) {
		// if there is "()", it refers to a method signature
		// so it cannot be a RegExp
		return !str.contains("()");
	}

	public String getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return STRING + getPattern() + getParamString();
	}

	@Override
	public Collection<CtElement> getElements(Collection<CtElement> roots) {
		Collection<CtElement> results = new ArrayList<>();
		for (CtElement element : roots) {
			results.addAll(scanElements(getChildren(element)));
		}
		return results;
	}

	public Collection<CtElement> scanElements(Collection<? extends CtElement> roots) {
		NameScanner nameScanner = new NameScanner();
		if (RECURSIVE_WILDCARD.equals(pattern)) {
			nameScanner.recurse(roots);
		} else {
			nameScanner.scan(roots);
		}
		return nameScanner.getResults();
	}

	private class NameScanner extends CtInheritanceScanner {
		private Collection<CtElement> results = new LinkedList<>();

		NameScanner() {
		}

		@Override
		public void scanCtElement(CtElement e) {
			if (WILDCARD.equals(pattern) || RECURSIVE_WILDCARD.equals(pattern)) {
				results.add(e);
			} else if (e instanceof CtExecutable && matchPattern(getSignature((CtExecutable) e))) {
				results.add(e);
			} else if (e instanceof CtNamedElement && matchPattern(((CtNamedElement) e).getSimpleName())) {
				results.add(e);
			} else if (e instanceof CtReference && matchPattern(((CtReference) e).getSimpleName())) {
				results.add(e);
			}
		}

		private boolean matchPattern(String str) {
			if (str.equals(pattern)) {
				return true;
			}
			return rePattern != null && rePattern.matcher(str).matches();
		}

		private void recurse(Collection<? extends CtElement> elements) {
			scan(elements);
			for (CtElement element : elements) {
				recurse(getChildren(element));
			}
		}

		public Collection<CtElement> getResults() {
			return results;
		}
	}

	private static String getSignature(CtExecutable exec) {
		String sign = exec.getSignature();
		if (exec instanceof CtConstructor) {
			int idx = sign.indexOf('(');
			return sign.substring(idx);
		}
		return sign;
	}
}
