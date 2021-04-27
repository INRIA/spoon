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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static spoon.smpl.SmPLLexer.TokenType.Addition;
import static spoon.smpl.SmPLLexer.TokenType.DisjunctionBegin;
import static spoon.smpl.SmPLLexer.TokenType.DisjunctionEnd;
import static spoon.smpl.SmPLLexer.TokenType.Dots;
import static spoon.smpl.SmPLLexer.TokenType.MetavarIdentifier;
import static spoon.smpl.SmPLLexer.TokenType.MetavarType;
import static spoon.smpl.SmPLLexer.TokenType.Newline;
import static spoon.smpl.SmPLLexer.TokenType.OptDotsBegin;
import static spoon.smpl.SmPLLexer.TokenType.OptDotsEnd;
import static spoon.smpl.SmPLLexer.TokenType.Rulename;
import static spoon.smpl.SmPLLexer.TokenType.WhenAny;
import static spoon.smpl.SmPLLexer.TokenType.WhenExists;
import static spoon.smpl.SmPLLexer.TokenType.WhenNotEqual;

/**
 * SmPLProblemDetector provides static analysis problem detection on SmPL patch code.
 */
public class SmPLProblemDetector {
	/**
	 * Hide utility class constructor.
	 */
	private SmPLProblemDetector() { }

	/**
	 * Type/severity of problem.
	 */
	public enum ProblemType {
		/**
		 * Warning, likely recoverable.
		 */
		Warning,

		/**
		 * Error, likely unrecoverable.
		 */
		Error
	}

	/**
	 * A Problem notification consists of a type/severity and a message.
	 */
	public static class Problem {
		/**
		 * Create a new Problem notification.
		 *
		 * @param type    Type/severity of problem
		 * @param message Message describing the problem
		 */
		public Problem(ProblemType type, String message) {
			this.type = type;
			this.message = message;
		}

		@Override
		public String toString() {
			return type.toString() + ": " + message;
		}

		/**
		 * Type/severity of problem.
		 */
		public final ProblemType type;

		/**
		 * Message describing the problem.
		 */
		public final String message;
	}

	/**
	 * Detect problems in plain text SmPL patch code.
	 *
	 * @param smpl Plain text SmPL patch code
	 * @return List of detected problems
	 */
	public static List<Problem> detectProblems(String smpl) {
		return null;
	}

	/**
	 * Detect problems in token stream produced by SmPLLexer.
	 *
	 * @param tokens SmPLLexer Token stream
	 * @return List of detected problems
	 */
	public static List<Problem> detectProblems(List<SmPLLexer.Token> tokens) {
		List<Problem> problems = new ArrayList<>();

		problems.addAll(detectSuperfluousDotsOperators(tokens));
		problems.addAll(detectConsecutiveDotsOperators(tokens));
		problems.addAll(detectDotsInDisjunctions(tokens));
		problems.addAll(detectDotsInAdditions(tokens));

		return problems;
	}

	/**
	 * Detect dots operator on addition lines.
	 *
	 * @param tokens List of tokens
	 * @return List of problems detected
	 */
	private static List<Problem> detectDotsInAdditions(List<SmPLLexer.Token> tokens) {
		List<Problem> problems = new ArrayList<>();
		boolean isAddition = false;

		for (SmPLLexer.Token token : tokens) {
			if (token.getType() == Addition) {
				isAddition = true;
			} else if (token.getType() == Newline) {
				isAddition = false;
			} else if (isAddition && token.getType() == Dots) {
				problems.add(new Problem(ProblemType.Error, "Dots operator in addition at " + token.getPosition().toString()));
			}
		}

		return problems;
	}

	/**
	 * Detect dots operators inside of pattern disjunctions.
	 *
	 * @param tokens List of tokens
	 * @return List of problems detected
	 */
	private static List<Problem> detectDotsInDisjunctions(List<SmPLLexer.Token> tokens) {
		List<Problem> problems = new ArrayList<>();
		int disjunctionDepth = 0;

		for (SmPLLexer.Token token : tokens) {
			if (token.getType() == DisjunctionBegin) {
				disjunctionDepth += 1;
			} else if (token.getType() == DisjunctionEnd) {
				disjunctionDepth -= 1;
			} else if (disjunctionDepth > 0 && (Arrays.asList(Dots, OptDotsBegin, OptDotsEnd).contains(token.getType()))) {
				problems.add(new Problem(ProblemType.Error, "Dots operator in pattern disjunction at " + token.getPosition().toString()));
			}
		}

		return problems;
	}

	/**
	 * Detect superfluous dots operators at the start and/or end of a rule body. This problem occurs in patches that
	 * do not match on the method header and therefore are fitted with implicit dots surrounding the rule body.
	 *
	 * @param tokens List of tokens
	 * @return List of problems detected
	 */
	private static List<Problem> detectSuperfluousDotsOperators(List<SmPLLexer.Token> tokens) {
		List<Problem> problems = new ArrayList<>();

		SmPLLexer.Token firstBodyToken = nextNonMatch(tokens, 0, Newline, Rulename, MetavarType, MetavarIdentifier);

		if (firstBodyToken != null && (firstBodyToken.getType() == Dots || firstBodyToken.getType() == OptDotsBegin)) {
			problems.add(new Problem(ProblemType.Error, "Superfluous dots operator at " + firstBodyToken.getPosition().toString()));
		}

		// TODO: trailing superfluous dots

		return problems;
	}

	/**
	 * Detect consecutive dots operators.
	 *
	 * @param tokens List of tokens
	 * @return List of problems detected
	 */
	private static List<Problem> detectConsecutiveDotsOperators(List<SmPLLexer.Token> tokens) {
		List<Problem> problems = new ArrayList<>();

		for (int i = 0; i < tokens.size() - 1; ++i) {
			SmPLLexer.Token t1 = tokens.get(i);

			if (t1.getType() == SmPLLexer.TokenType.Dots) {
				SmPLLexer.Token t2 = nextNonMatch(tokens, i + 1, Newline, WhenAny, WhenExists, WhenNotEqual);

				if (t2 != null && t2.getType() == Dots) {
					problems.add(new Problem(ProblemType.Error, "Consecutive dots operators at " + t1.getPosition().toString()));
				}
			}
		}

		return problems;
	}

	/**
	 * Filter a list of tokens to include only the given types.
	 *
	 * @param tokens List of tokens to filter
	 * @param ts     Types to retain
	 * @return List of tokens filtered to only retain the given types
	 */
	private static List<SmPLLexer.Token> retain(List<SmPLLexer.Token> tokens, SmPLLexer.TokenType... ts) {
		return tokens.stream().filter(t -> Set.of(ts).contains(t.getType())).collect(Collectors.toList());
	}

	/**
	 * Filter a list of tokens to remove the given types.
	 *
	 * @param tokens List of tokens to filter
	 * @param ts     Types to remove
	 * @return List of tokens filtered to remove the given types
	 */
	private static List<SmPLLexer.Token> remove(List<SmPLLexer.Token> tokens, SmPLLexer.TokenType... ts) {
		return tokens.stream().filter(t -> !Set.of(ts).contains(t.getType())).collect(Collectors.toList());
	}

	/**
	 * Find next matching token of specific type(s).
	 *
	 * @param tokens     List of tokens to search
	 * @param startIndex List index where search should start
	 * @param matchTypes Types to match
	 * @return First matching token, or null if there was no token matching the given type(s)
	 */
	private static SmPLLexer.Token nextMatch(List<SmPLLexer.Token> tokens, int startIndex, SmPLLexer.TokenType... matchTypes) {
		List<SmPLLexer.Token> stuff = retain(tokens.subList(startIndex, tokens.size() - 1), matchTypes);
		return stuff.isEmpty() ? null : stuff.get(0);
	}

	/**
	 * Find next token that does not match the given specific type(s).
	 *
	 * @param tokens     List of tokens to search
	 * @param startIndex List index where search should start
	 * @param matchTypes Types to ignore
	 * @return First token of some other type, or null if there was no such token
	 */
	private static SmPLLexer.Token nextNonMatch(List<SmPLLexer.Token> tokens, int startIndex, SmPLLexer.TokenType... matchTypes) {
		List<SmPLLexer.Token> stuff = remove(tokens.subList(startIndex, tokens.size() - 1), matchTypes);
		return stuff.isEmpty() ? null : stuff.get(0);
	}
}
