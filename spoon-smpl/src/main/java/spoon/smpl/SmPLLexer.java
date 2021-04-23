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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: proper string parsing including escape characters

/**
 * An SmPL lexer.
 */
public class SmPLLexer {
	/**
	 * Hide utility class constructor.
	 */
	private SmPLLexer() { }

	/**
	 * Token types.
	 */
	public enum TokenType {
		/**
		 * A newline / line break.
		 */
		Newline,

		/**
		 * Name of a rule.
		 */
		Rulename,

		/**
		 * Type specifier of a metavariable declaration.
		 */
		MetavarType,

		/**
		 * Identifier in a metavariable declaration.
		 */
		MetavarIdentifier,

		/**
		 * Addition operation.
		 */
		Addition,

		/**
		 * Deletion operation.
		 */
		Deletion,

		/**
		 * Beginning of a pattern disjunction.
		 */
		DisjunctionBegin,

		/**
		 * Continuation of a pattern disjunction.
		 */
		DisjunctionContinue,

		/**
		 * End of a pattern disjunction.
		 */
		DisjunctionEnd,

		/**
		 * Beginning of an optdots block.
		 */
		OptDotsBegin,

		/**
		 * End of an optdots block.
		 */
		OptDotsEnd,

		/**
		 * Dots statement, parameter or argument.
		 */
		Dots,

		/**
		 * Dots constraint relaxation specifier for 'when exists'.
		 */
		WhenExists,

		/**
		 * Dots constraint relaxation specifier for 'when any'.
		 */
		WhenAny,

		/**
		 * Dots constraint specifier for 'when != x'.
		 */
		WhenNotEqual,

		/**
		 * Metavariable additional constraint for 'when matches "..."'
		 */
		WhenMatches,

		/**
		 * Arbitrary code.
		 */
		Code,

		/**
		 * A string of text enclosed in double quotes.
		 */
		String,
	}

	/**
	 * Position of a lexical element.
	 */
	public static class Position {
		/**
		 * Record a new Position.
		 *
		 * @param text Full source text
		 * @param pos  Character offset in source text
		 */
		public Position(String text, int pos) {
			this.text = text;
			this.pos = pos;
		}

		/**
		 * Get full source text.
		 *
		 * @return Full source text
		 */
		public String getText() {
			return text;
		}

		/**
		 * Get line number in source text.
		 *
		 * @return Line number
		 */
		public int getLine() {
			return countOccurrences(text.substring(0, pos), "\n") + 1;
		}

		/**
		 * Get column number in source text.
		 *
		 * @return Column number
		 */
		public int getColumn() {
			Map<Character, Integer> whitespace = new HashMap<>();
			whitespace.put(' ', 1);
			whitespace.put('\t', 4);

			int wschars = 0;
			int wsoffset = 0;

			while ((pos + wschars) < text.length() && whitespace.containsKey(text.charAt(pos + wschars))) {
				wsoffset += whitespace.get(text.charAt(pos + wschars));
				wschars += 1;
			}

			String mytext = text.substring(0, pos);

			if (mytext.contains("\n")) {
				return mytext.substring(mytext.lastIndexOf('\n')).length() + wsoffset;
			} else {
				return 1 + pos + wsoffset;
			}
		}

		/**
		 * Get character offset in source text.
		 *
		 * @return Character offset in source text
		 */
		public int getOffset() {
			return pos;
		}

		@Override
		public String toString() {
			return "line " + getLine() + ", column " + getColumn();
		}

		/**
		 * Count the number of occurrences of a substring in given String.
		 *
		 * @param text String to scan
		 * @param find Substring to count
		 * @return Number of occurrences of substring
		 */
		private static int countOccurrences(String text, String find) {
			return (text.length() - text.replace(find, "").length()) / find.length();
		}

		/**
		 * Source text.
		 */
		private String text;

		/**
		 * Character offset in source text.
		 */
		private int pos;
	}

	/**
	 * A Token represents an atomic lexical element.
	 */
	public static class Token {
		/**
		 * Create a new Token.
		 *
		 * @param tpe  Token type
		 * @param text Source text corresponding to token
		 * @param pos  Position in source text
		 */
		public Token(TokenType tpe, String text, Position pos) {
			this.tpe = tpe;
			this.text = text;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return tpe.toString() + "('" + text + "')";
		}

		/**
		 * Get Token type.
		 *
		 * @return Token type
		 */
		public TokenType getType() {
			return tpe;
		}

		/**
		 * Get source text corresponding to token.
		 *
		 * @return Source text corresponding to token
		 */
		public String getText() {
			return text;
		}

		/**
		 * Get Position in source text.
		 *
		 * @return Position in source text
		 */
		public Position getPosition() {
			return pos;
		}

		/**
		 * Token type.
		 */
		private TokenType tpe;

		/**
		 * Source text.
		 */
		private String text;

		/**
		 * Position in source text.
		 */
		private Position pos;
	}

	/**
	 * Lex the given source text, producing an ordered list of tokens.
	 *
	 * @param text Source text to lex
	 * @return Ordered list of tokens
	 */
	public static List<Token> lex(String text) {
		List<LexerRule> prerulehead = new ArrayList<>();
		List<LexerRule> metavars = new ArrayList<>();
		List<LexerRule> metavarIdentifiers = new ArrayList<>();
		List<LexerRule> rulebody = new ArrayList<>();
		List<LexerRule> dotsConstraints = new ArrayList<>();

		Predicate<List<Token>> always = (tokens) -> true;
		Consumer<Stack<List<LexerRule>>> noop = (ctx) -> {
		};
		TriFunction<Integer, List<Token>, Matcher, Integer> eat = (pos, tokens, match) -> match.end();
		TriFunction<Integer, List<Token>, Matcher, Integer> takeNone = (pos, tokens, match) -> 0;

		prerulehead.add(new LexerRule("whitespace", "(?s)^\\s+", always, noop, eat));
		prerulehead.add(new LexerRule("rulename", "(?s)^@([^@]+)@", always, cswitch(metavars), addTokenFromGroup(TokenType.Rulename, 1, text)));
		prerulehead.add(new LexerRule("atat", "(?s)^@@", always, cswitch(metavars), eat));

		metavars.add(new LexerRule("whitespace", "(?s)^\\s+", always, noop, eat));
		metavars.add(new LexerRule("metavar_type", "(?s)^[A-Za-z_][A-Za-z0-9_]*", always, push(metavarIdentifiers), addToken(TokenType.MetavarType, text)));
		metavars.add(new LexerRule("atat", "(?s)^@@", always, cswitch(rulebody), eat));

		metavarIdentifiers.add(new LexerRule("when_matches", "(?s)^when\\s+matches\\s+", prev(TokenType.MetavarIdentifier), noop, addEmptyToken(TokenType.WhenMatches, text)));
		metavarIdentifiers.add(new LexerRule("string", "(?s)^\"[^\"]+\"", prev(TokenType.WhenMatches), noop, addToken(TokenType.String, text)));
		metavarIdentifiers.add(new LexerRule("metavar_id", "(?s)^[A-Za-z_][A-Za-z0-9_]*", always, noop, addToken(TokenType.MetavarIdentifier, text)));
		metavarIdentifiers.add(new LexerRule("whitespace", "(?s)^\\s+", always, noop, eat));
		metavarIdentifiers.add(new LexerRule("comma", "(?s)^,", always, noop, eat));
		metavarIdentifiers.add(new LexerRule("semicolon", "(?s)^;", always, pop(), eat));

		rulebody.add(new LexerRule("newline", "(?s)^\n", always, noop, addEmptyToken(TokenType.Newline, text)));
		rulebody.add(new LexerRule("optdots_begin", "(?s)^<\\.\\.\\.", prev(TokenType.Newline), push(dotsConstraints), addToken(TokenType.OptDotsBegin, text)));
		rulebody.add(new LexerRule("optdots_end", "(?s)^\\.\\.\\.>", prev(TokenType.Newline), noop, addToken(TokenType.OptDotsEnd, text)));
		rulebody.add(new LexerRule("dots", "(?s)^\\.\\.\\.", always, push(dotsConstraints), addToken(TokenType.Dots, text)));
		rulebody.add(new LexerRule("addition", "(?s)^\\+", prev(TokenType.Newline), noop, addToken(TokenType.Addition, text)));
		rulebody.add(new LexerRule("deletion", "(?s)^\\-", prev(TokenType.Newline), noop, addToken(TokenType.Deletion, text)));
		rulebody.add(new LexerRule("disjunction_begin", "(?s)^\\(", prev(TokenType.Newline), noop, addToken(TokenType.DisjunctionBegin, text)));
		rulebody.add(new LexerRule("disjunction_continue", "(?s)^\\|", prev(TokenType.Newline), noop, addToken(TokenType.DisjunctionContinue, text)));
		rulebody.add(new LexerRule("disjunction_end", "(?s)^\\)", prev(TokenType.Newline), noop, addToken(TokenType.DisjunctionEnd, text)));

		rulebody.add(new LexerRule("code", "(?s)^.", always, noop,
									(pos, tokens, match) -> {
										if (prevType(tokens) == TokenType.Code) {
											Token prev = tokens.remove(tokens.size() - 1);
											tokens.add(new Token(TokenType.Code, prev.text + match.group(), prev.pos));
										} else {
											tokens.add(new Token(TokenType.Code, match.group(), new Position(text, pos)));
										}

										return match.end();
									}));

		dotsConstraints.add(new LexerRule("newline", "(?s)^\n", always, noop, addEmptyToken(TokenType.Newline, text)));
		dotsConstraints.add(new LexerRule("when_exists", "(?s)^\\s*when\\s+exists", always, noop, addToken(TokenType.WhenExists, text)));
		dotsConstraints.add(new LexerRule("when_any", "(?s)^\\s*when\\s+any", always, noop, addToken(TokenType.WhenAny, text)));

		dotsConstraints.add(new LexerRule("when_neq", "(?s)^\\s*when\\s+!=([^\n]+)", always, noop,
											(pos, tokens, match) -> {
												tokens.add(new Token(TokenType.WhenNotEqual,
																		match.group(1).replace("...", SmPLJavaDSL.getDotsParameterOrArgumentElementName()),
																		new Position(text, pos)));
												return match.end();
											}));

		dotsConstraints.add(new LexerRule("anychar", ".", always, pop(), takeNone));

		List<Token> tokens = new ArrayList<>();
		Stack<List<LexerRule>> context = new Stack<>();

		context.push(prerulehead);

		int pos = 0;
		int end = text.length();

		while (pos < end) {
			List<String> expected = new ArrayList<>();
			boolean foundSomething = false;
			String texthere = text.substring(pos);

			List<LexerRule> rules = context.peek();

			for (LexerRule rule : rules) {
				if (!rule.precondition.test(tokens)) {
					continue;
				}

				expected.add(rule.name);
				Matcher matcher = rule.pattern.matcher(texthere);

				if (matcher.find()) {
					rule.contextOp.accept(context);
					pos += rule.outputOp.apply(pos, tokens, matcher);

					//System.out.println("match " + rule.name);
					//System.out.println(tokens);

					foundSomething = true;
					break;
				}
			}

			if (!foundSomething) {
				throw new RuntimeException("Lex error at offset " + Integer.toString(pos) + ", expected one of " + expected.toString());
			}
		}

		return tokens;
	}

	/**
	 * A lexer rule.
	 */
	private static class LexerRule {
		/**
		 * Create a new lexer rule.
		 *
		 * @param name         Name of rule
		 * @param regex        Regular expression for matching source text
		 * @param precondition Precondition for attempting to match rule
		 * @param contextOp    Context mutator function
		 * @param outputOp     Output mutator function
		 */
		LexerRule(String name, String regex,
							Predicate<List<Token>> precondition,
							Consumer<Stack<List<LexerRule>>> contextOp,
							TriFunction<Integer, List<Token>, Matcher, Integer> outputOp) {
			this.name = name;
			this.pattern = Pattern.compile(regex);
			this.precondition = precondition;
			this.contextOp = contextOp;
			this.outputOp = outputOp;
		}

		/**
		 * Name of rule.
		 */
		public final String name;

		/**
		 * Regex pattern of rule.
		 */
		public final Pattern pattern;

		/**
		 * Precondition that should hold before even attempting to match the rule.
		 */
		public final Predicate<List<Token>> precondition;

		/**
		 * Context mutator to apply when the rule matches.
		 */
		public final Consumer<Stack<List<LexerRule>>> contextOp;

		/**
		 * Output mutator to apply when the rule matches.
		 */
		public final TriFunction<Integer, List<Token>, Matcher, Integer> outputOp;
	}

	/**
	 * Try to match the given Regex pattern, returning either the matching Matcher or null.
	 *
	 * @param pattern Pattern to match
	 * @param text    Text to match pattern against
	 * @return Matching Matcher if the pattern matched, null otherwise
	 */
	private static Matcher tryMatch(Pattern pattern, String text) {
		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {
			return matcher;
		} else {
			return null;
		}
	}

	/**
	 * Get the ending offset of the region matched by the given pattern (if any).
	 *
	 * @param pattern Pattern to match
	 * @param text    Text to match pattern against
	 * @return Ending offset of region matched by pattern, or 0 if pattern did not match
	 */
	private static int eat(Pattern pattern, String text) {
		Matcher matcher = tryMatch(pattern, text);

		return (matcher == null) ? 0 : matcher.end();
	}

	/**
	 * Check type of last token in list.
	 *
	 * @param tokens List of tokens
	 * @return Type of last token in list
	 */
	private static TokenType prevType(List<Token> tokens) {
		return (tokens.isEmpty()) ? null : tokens.get(tokens.size() - 1).tpe;
	}

	/**
	 * Functional interface for function of three variables.
	 *
	 * @param <T> Type of first variable
	 * @param <U> Type of second variable
	 * @param <V> Type of third variable
	 * @param <S> Return type
	 */
	private interface TriFunction<T, U, V, S> {
		S apply(T t, U u, V v);
	}

	/**
	 * Create a functional predicate for checking the type of the most recently recorded token.
	 *
	 * @param tpe Type to match
	 * @return Functional predicate returning true if most recently recorded token matches the given type, otherwise returning false
	 */
	private static Predicate<List<Token>> prev(TokenType tpe) {
		return (tokens) -> prevType(tokens) == tpe;
	}

	/**
	 * Create a context mutator that replaces the current context with another context.
	 *
	 * @param newContext Context to switch to
	 * @return Context mutator that switches contexts
	 */
	private static Consumer<Stack<List<LexerRule>>> cswitch(List<LexerRule> newContext) {
		return (ctx) -> {
			ctx.pop();
			ctx.push(newContext);
		};
	}

	/**
	 * Create a context mutator that adds a context to the context stack.
	 *
	 * @param context Context to push on the stack
	 * @return Context mutator that pushes the given context on the stack
	 */
	private static Consumer<Stack<List<LexerRule>>> push(List<LexerRule> context) {
		return (ctx) -> {
			ctx.push(context);
		};
	}

	/**
	 * Create a context mutator that removes the current context from the context stack.
	 *
	 * @return Context mutator that pops the context stack
	 */
	private static Consumer<Stack<List<LexerRule>>> pop() {
		return Stack::pop;
	}

	/**
	 * Create an output mutator that records a token of the given type with an empty textual value.
	 *
	 * @param tpe  Token type to record
	 * @param text Source text for which to record the token position
	 * @return Output mutator that records a token of given type with empty text
	 */
	private static TriFunction<Integer, List<Token>, Matcher, Integer> addEmptyToken(TokenType tpe, String text) {
		return (pos, tokens, match) -> {
			tokens.add(new Token(tpe, "", new Position(text, pos)));
			return match.end();
		};
	}

	/**
	 * Create an output mutator that records a token of the given type taking the textual value from the matched source
	 * text.
	 *
	 * @param tpe  Token type to record
	 * @param text Source text for which to record the token position
	 * @return Output mutator that records a token of given type with textual value taken from matched source
	 */
	private static TriFunction<Integer, List<Token>, Matcher, Integer> addToken(TokenType tpe, String text) {
		return (pos, tokens, match) -> {
			tokens.add(new Token(tpe, match.group(), new Position(text, pos)));
			return match.end();
		};
	}

	/**
	 * Create an output mutator that records a token of the given type taking the textual value from the specified
	 * match group of the matched source text.
	 *
	 * @param tpe  Token type to record
	 * @param text Source text for which to record the token position
	 * @return Output mutator that records a token of given type with textual value taken from specified match group of matched source
	 */
	private static TriFunction<Integer, List<Token>, Matcher, Integer> addTokenFromGroup(TokenType tpe, int group, String text) {
		return (pos, tokens, match) -> {
			tokens.add(new Token(tpe, match.group(group), new Position(text, pos)));
			return match.end();
		};
	}
}
