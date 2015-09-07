package spoon.reflect.path.impl;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A CtPathElement that match on CtNamedElement#getSimpleName
 */
public class CtNamedPathElement extends AbstractPathElement<CtElement, CtElement> {
	public static final String STRING = ".";
	public static final String WILDCARD = "*";
	public static final String RECURSIVE_WILCARD = "**";

	private final String pattern;

	public CtNamedPathElement(String pattern) {
		this.pattern = pattern;
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
		Collection<CtElement> results = new ArrayList<CtElement>();
		for (CtElement element : roots) {
			results.addAll(scanElements(getChilds(element)));
		}
		return results;
	}

	public Collection<CtElement> scanElements(Collection<? extends CtElement> roots) {
		NameScanner nameScanner = new NameScanner();
		if (RECURSIVE_WILCARD.equals(pattern)) {
			nameScanner.recurse(roots);
		} else {
			nameScanner.scan(roots);
		}
		return nameScanner.getResults();
	}

	private class NameScanner extends CtInheritanceScanner {
		private Collection<CtElement> results = new LinkedList<CtElement>();

		NameScanner() {
		}

		@Override
		public void scanCtElement(CtElement e) {
			if (WILDCARD.equals(pattern) || RECURSIVE_WILCARD.equals(pattern)) {
				results.add(e);
			} else if (e instanceof CtNamedElement
					&& ((CtNamedElement) e).getSimpleName().matches(pattern)) {
				results.add(e);
			}
		}

		private void recurse(Collection<? extends CtElement> elements) {
			scan(elements);
			for (CtElement element : elements) {
				recurse(getChilds(element));
			}
		}

		public Collection<CtElement> getResults() {
			return results;
		}
	}

}
