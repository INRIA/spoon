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
package spoon.smpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * SmPLGrep provides a facility for extracting a pattern of strings from an SmPL rule along with the corresponding
 * facility for matching such a pattern against the plain text source code of a given executable AST block. The
 * intention is to use SmPLGrep as a preprocessing step to reduce the number of target methods that need to be fully
 * model-checked, by excluding methods that cannot possibly match the rule simply based on the code not containing
 * certain strings contained in the rule.
 */
public class SmPLGrep {
	/**
	 * Hide utility class constructor.
	 */
	private SmPLGrep() { }

	/**
	 * Build a pattern from a given SmPL rule instance
	 *
	 * @param rule SmPL rule
	 * @return Pattern representing strings required to be present in matching code
	 */
	public static Pattern buildPattern(SmPLRule rule) {
		return buildPattern(rule.getSource());
	}

	/**
	 * Build a pattern from a given plain-text SmPL patch.
	 *
	 * @param smpl Plain-text SmPL patch
	 * @return Pattern representing strings required to be present in matching code
	 */
	public static Pattern buildPattern(String smpl) {
		List<SmPLLexer.Token> tokens = SmPLLexer.lex(smpl);
		Pattern result = new Pattern();
		Set<String> metavars = new HashSet<>();
		java.util.regex.Pattern targetPattern = java.util.regex.Pattern.compile("[0-9.]+[0-9fL]+|[A-Za-z0-9_]+");
		boolean isAddition = false;

		for (SmPLLexer.Token token : tokens) {
			switch (token.getType()) {
				case Newline:
					isAddition = false;
					break;

				case Addition:
					isAddition = true;
					break;

				case DisjunctionBegin:
					result.enterDisjunction();
					break;

				case DisjunctionContinue:
					result.continueDisjunction();
					break;

				case DisjunctionEnd:
					result.exitDisjunction();
					break;

				case MetavarType:
					// TODO: use SmPLJavaDSL for checking for typenames of specifically-typed metavars
					if (!Arrays.asList("identifier", "type", "constant", "expression").contains(token.getText())) {
						result.addString(token.getText());
					}

					break;

				case MetavarIdentifier:
					metavars.add(token.getText());
					break;

				case Code:
					if (isAddition) {
						break;
					}

					for (java.util.regex.MatchResult mr : targetPattern.matcher(token.getText()).results().collect(Collectors.toList())) {
						String match = mr.group();

						if (!metavars.contains(match)) {
							result.addString(match);
						}
					}

					break;

				default:
					break;
			}
		}

		return result;
	}

	/**
	 * SmPLGrep.Pattern represents a set of Strings (and/or disjunctions over Strings) that all must be present in a
	 * given target String for the target String to be considered as matching.
	 * <p>
	 * For example, the pattern (foo & bar & (x | (y & z))) would match a String containing both substrings "foo" and
	 * "bar", and either the substring "x" or both substrings "y" and "z".
	 */
	public static class Pattern {
		/**
		 * Base pattern element type.
		 */
		private abstract static class PatternNode extends ArrayList<PatternNode> {
			public abstract boolean matches(String s);
		}

		/**
		 * Pattern element for a single String.
		 */
		private static class _String extends PatternNode {
			/**
			 * Create a new single String pattern node.
			 *
			 * @param value String value
			 */
			_String(String value) {
				this.value = value;
				this.lowerCaseValue = value.toLowerCase();
			}

			/**
			 * String value.
			 */
			public final String value;

			/**
			 * Lowercase String value;
			 */
			public final String lowerCaseValue;

			/**
			 * Check if a given target String contains this String.
			 *
			 * @param s Target String
			 * @return True if target String contains this String, false otherwise
			 */
			@Override
			public boolean matches(String s) {
				return s.contains(lowerCaseValue);
			}

			@Override
			public String toString() {
				return value;
			}

			@Override
			public boolean equals(Object other) {
				return other == this || (other instanceof _String && ((_String) other).lowerCaseValue.equals(lowerCaseValue));
			}

			/**
			 * Disabled.
			 *
			 * @param patternNode Irrelevant
			 * @return false
			 */
			@Override
			public boolean add(PatternNode patternNode) {
				return false;
			}
		}

		/**
		 * Pattern element representing a conjunction of other pattern elements.
		 */
		private static class Conjunction extends PatternNode {
			/**
			 * Check if a given target String matches all the elements of this conjunction.
			 *
			 * @param s Target String
			 * @return True if target String matches all elements of the conjunction, false otherwise
			 */
			@Override
			public boolean matches(String s) {
				return size() == 0 || stream().allMatch(node -> node.matches(s));
			}

			@Override
			public String toString() {
				if (size() == 1) {
					return get(0).toString();
				} else {
					return "(" + stream().map(PatternNode::toString).collect(Collectors.joining(" & ")) + ")";
				}
			}

			@Override
			public boolean add(PatternNode node) {
				return (node instanceof _String && contains(node)) ? false : super.add(node);
			}
		}

		/**
		 * Pattern element representing a disjunction of other pattern elements.
		 */
		private static class Disjunction extends PatternNode {
			/**
			 * Check if a given target String contains some element of this disjunction.
			 *
			 * @param s Target String
			 * @return True if target String contains any element of this disjunction, false otherwise
			 */
			@Override
			public boolean matches(String s) {
				return size() == 0 || stream().anyMatch(node -> node.matches(s));
			}

			@Override
			public String toString() {
				if (size() == 1) {
					return get(0).toString();
				} else {
					return "(" + stream().map(PatternNode::toString).collect(Collectors.joining(" | ")) + ")";
				}
			}

			@Override
			public boolean add(PatternNode node) {
				return (node instanceof _String && contains(node)) ? false : super.add(node);
			}
		}

		/**
		 * Create a new Pattern.
		 */
		public Pattern() {
			patternNodeStack = new Stack<>();
			patternNodeStack.push(new Conjunction());
		}

		/**
		 * Create a new disjunction at the current cursor position in the pattern and place the cursor in its first
		 * clause.
		 */
		public void enterDisjunction() {
			Disjunction disjunction = new Disjunction();
			Conjunction firstClause = new Conjunction();

			disjunction.add(firstClause);

			patternNodeStack.peek().add(disjunction);
			patternNodeStack.push(disjunction);
			patternNodeStack.push(firstClause);
		}

		/**
		 * Create a new clause for the current disjunction and move to cursor into it.
		 */
		public void continueDisjunction() {
			if (patternNodeStack.size() < 3) {
				throw new IllegalStateException("Not in a disjunction");
			}

			if (patternNodeStack.peek().size() > 0) {
				Conjunction nextClause = new Conjunction();
				patternNodeStack.pop();
				patternNodeStack.peek().add(nextClause);
				patternNodeStack.push(nextClause);
			}
		}

		/**
		 * Exit the current disjunction, moving the cursor back out into the enclosing pattern element.
		 */
		public void exitDisjunction() {
			if (patternNodeStack.size() < 3) {
				throw new IllegalStateException("Not in a disjunction");
			}

			Conjunction lastClause = (Conjunction) patternNodeStack.pop();
			Disjunction disjunction = (Disjunction) patternNodeStack.pop();

			if (lastClause.size() == 0) {
				removeFrom(disjunction, lastClause);
			}

			if (disjunction.size() == 0) {
				removeFrom(patternNodeStack.peek(), disjunction);
			}
		}

		/**
		 * Remove an inner pattern node from an outer container by object identity.
		 *
		 * @param container Outer container node
		 * @param element   Inner element node
		 */
		private void removeFrom(PatternNode container, PatternNode element) {
			for (int i = 0; i < container.size(); ++i) {
				if (container.get(i) == element) {
					container.remove(i);
					return;
				}
			}
		}

		/**
		 * Add a String at the current position of the cursor.
		 *
		 * @param s String to add
		 */
		public void addString(String s) {
			patternNodeStack.peek().add(new _String(s));
		}

		/**
		 * Check if the Pattern matches a given String. The target String is considered to match if all Strings
		 * required by the Pattern are present, taking disjunctions under account.
		 *
		 * @param s String to match
		 * @return True if the String matches the Pattern, false otherwise
		 */
		public boolean matches(String s) {
			return patternNodeStack.peek().matches(s.toLowerCase());
		}

		@Override
		public String toString() {
			return patternNodeStack.peek().toString();
		}

		/**
		 * Stack of pattern nodes, top of stack serves as cursor position.
		 */
		private Stack<PatternNode> patternNodeStack;
	}
}
