/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl.pattern;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: merge PatternMatcher, DotsExtPatternMatcher and SubElemPatternMatcher into a single class?

/**
 * SubElemPatternMatcher implements pattern matching with the added ability of matching a given rule
 * pattern against any ElemNode sub-pattern contained in an input pattern. For example, a rule pattern
 * representing an expression may be matched to a statement pattern containing the expression as a
 * sub-pattern.
 */
public class SubElemPatternMatcher {
	/**
	 * A MatchResult consists of a matched code element and the parameter bindings involved.
	 */
	public static class MatchResult {
		/**
		 * Create a new MatchResult.
		 *
		 * @param matchedElement Matched code element
		 * @param parameters     Parameter bindings
		 */
		public MatchResult(CtElement matchedElement, Map<String, Object> parameters) {
			this.matchedElement = matchedElement;
			this.parameters = parameters;
		}

		@Override
		public String toString() {
			return matchedElement.toString() + ":" + parameters.toString();
		}

		/**
		 * Matched code element.
		 */
		public final CtElement matchedElement;

		/**
		 * Parameter bindings.
		 */
		public final Map<String, Object> parameters;
	}

	/**
	 * Create a new SubElemPatternMatcher using the given rule pattern that should be allowed to match any
	 * ElemNode sub-pattern of a target pattern.
	 *
	 * @param rulePattern Rule pattern
	 */
	public SubElemPatternMatcher(PatternNode rulePattern) {
		this.rulePattern = rulePattern;
	}

	/**
	 * Test if the rule pattern matches a given target pattern or any of its ElemNode sub-patterns.
	 *
	 * @param pattern Target pattern
	 * @return True if there is any match, false otherwise
	 */
	public boolean matches(PatternNode pattern) {
		result = new ArrayList<>();
		DotsExtPatternMatcher regularMatcher = new DotsExtPatternMatcher(rulePattern);
		SubElemPatternCollector spc = new SubElemPatternCollector();
		pattern.accept(spc);

		for (ElemNode subPattern : spc.getResult()) {
			subPattern.accept(regularMatcher);

			if (regularMatcher.getResult() == true) {
				result.add(new MatchResult(subPattern.elem, regularMatcher.getParameters()));
			}

			regularMatcher.reset();
		}

		return result.size() > 0;
	}

	/**
	 * Get the results.
	 *
	 * @return List of results
	 */
	public List<MatchResult> getResult() {
		return result;
	}

	/**
	 * Rule pattern.
	 */
	private PatternNode rulePattern;

	/**
	 * List of results.
	 */
	private List<MatchResult> result;
}
