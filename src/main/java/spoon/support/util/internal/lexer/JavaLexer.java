package spoon.support.util.internal.lexer;

import spoon.reflect.declaration.ModifierKind;
import spoon.support.util.internal.trie.WordTrie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaLexer {
	private static final char CHAR_ESCAPE_CHAR_CHAR = '\\';
	private static final WordTrie<ModifierKind> MODIFIER_TRIE = WordTrie.ofWords(
			Arrays.stream(ModifierKind.values())
					.collect(Collectors.toMap(ModifierKind::toString, Function.identity()))
	);
	private final char[] content;
	private final int start;
	private final int end;
	private int nextPos;

	public JavaLexer(char[] content, int start, int end) {
		this.content = content;
		this.start = start;
		this.end = end;
		this.nextPos = this.start;
	}

	/**
	 * @return {@code null} if no more tokens can be lexed in the range of this lexer.
	 */
	public Token lex() {
		if (!skipCommentsAndWhitespaces()) {
			return null;
		}
		int pos = this.nextPos;
		char c = next();
		switch (c) {
			case '(':
			case ')':
			case '{':
			case '}':
			case '[':
			case ']':
			case ';':
			case ',':
			case '@':
				return new Token(TokenType.SEPARATOR, pos, this.nextPos);
			case '.':
				if (hasMore(2) && peek() == '.' && peek(1) == '.') {
					skip(2);
				}
				return new Token(TokenType.SEPARATOR, pos, this.nextPos);
			case ':':
				if (hasMore() && peek() == ':') {
					next();
					return new Token(TokenType.SEPARATOR, pos, this.nextPos);
				}
				return new Token(TokenType.OPERATOR, pos, this.nextPos);
			case '/': // no comment, already skipped
			case '=':
			case '*':
			case '^':
			case '%':
			case '!':
				return singleOrDoubleOperator(pos, '=');
			case '+':
				return singleOrDoubleOperator(pos, '=', '+');
			case '-':
				return singleOrDoubleOperator(pos, '=', '-', '>');
			case '&':
				return singleOrDoubleOperator(pos, '=', '&');
			case '|':
				return singleOrDoubleOperator(pos, '=', '|');
			case '>':
				return angleBracket(pos, 1, 3, '>');
			case '<':
				return angleBracket(pos, 1, 2, '<');
			case '\'':
				return lexCharacterLiteral(pos);
			case '"':
				return lexStringLiteral(pos);
			case '~':
			case '?':
				return new Token(TokenType.OPERATOR, pos, this.nextPos);
			default:
				skip(-1); // reset to previous
				return lexLiteralOrKeywordOrIdentifier();
		}
	}

	private Token angleBracket(int pos, int found, int maxFound, char bracket) {
		if (hasMore()) {
			char peek = peek();
			if (peek == '=') {
				next();
				return new Token(TokenType.OPERATOR, pos, this.nextPos);
			}
			if (peek == bracket && found < maxFound) {
				next();
				return angleBracket(pos, found + 1, maxFound, bracket);
			}
		}
		return new Token(TokenType.OPERATOR, pos, this.nextPos);
	}

	private void skipUntilLineBreak() {
		while (hasMore()) {
			char peek = peek();
			if (peek == '\n' || peek == '\r') {
				skipWhitespaces();
				return;
			}
			next();
		}
	}

	private boolean skipUntil(String s) {
		char[] chars = s.toCharArray();
		char first = chars[0];
		int pos = this.nextPos;
		do {
			int index = indexOf(first, pos);
			if (index < 0) {
				// TODO what if not present?
				return false;
			}
			if (Arrays.equals(this.content, index, index + chars.length, chars, 0, chars.length)) {
				this.nextPos = index + chars.length;
				return true;
			} else {
				pos = index + 1;
			}
		} while (true);
	}

	private Token lexLiteralOrKeywordOrIdentifier() {
		int pos = this.nextPos;
		char next = next(); // assuming next is available
		if (Character.isJavaIdentifierStart(next)) {
			while (hasMore() && Character.isJavaIdentifierPart(peek())) {
				next();
			}
			Optional<ModifierKind> match = MODIFIER_TRIE.findMatch(this.content, pos, this.nextPos);
			if (match.isPresent()) {
				return new Token(TokenType.KEYWORD, pos, this.nextPos);
			}
			return new Token(TokenType.IDENTIFIER, pos, this.nextPos);
		} else if (Character.isDigit(next)) {
			readNumericLiteral(next);
			return new Token(TokenType.LITERAL, pos, this.nextPos);
		}
		return null;
	}

	private void readNumericLiteral(char first) {
		if (!hasMore()) {
			return;
		}
		// check if hexadecimal notation
		if (first == '0') {
			if (peek() == 'x' || peek() == 'X') {
				next();
				readHexadecimalsAndUnderscore();
				if (hasMore() && peek() == '.') {
					next();
					readHexadecimalFloatingPointLiteral();
				} else {
					readHexadecimalsAndUnderscore();
				}
			} else if (peek() == 'b' || peek() == 'B') {
				next();
				readDigitsOrUnderscore();
			} else {
				next();
				readDigitsOrUnderscore();
			}
		} else {
			readDigitsOrUnderscore();
			if (hasMore() && peek() == '.') {
				next();
				readDigitsOrUnderscore();
			}
		}
		if (hasMore()) {
			char peek = peek();
			switch (peek) {
				case 'd':
				case 'D':
				case 'f':
				case 'F':
				case 'l':
				case 'L':
					next();
			}
		}
	}

	private void readDigitsOrUnderscore() {
		while (hasMore()) {
			char peek = peek();
			if ('0' <= peek && peek <= '9' || peek == '_') {
				next();
			} else {
				return;
			}
		}
	}

	// the part after the .
	private void readHexadecimalFloatingPointLiteral() {
		readHexadecimalsAndUnderscore();
		if (hasMore() && peek() == 'p' || peek() == 'P') {
			next();
			if (hasMore() && peek() == '+') {
				next();
				readDigitsOrUnderscore();
			}
		}
	}

	private void readHexadecimalsAndUnderscore() {
		while (hasMore()) {
			char peek = peek();
			if ('0' <= peek && peek <= '9'
					|| 'A' <= peek && peek <= 'F'
					|| 'a' <= peek && peek <= 'f'
					|| peek == '_') {
				next();
			} else {
				return;
			}
		}
	}

	private Token lexStringLiteral(int pos) {
		if (hasMore(2) && peek() == '"' && peek(1) == '"') {
			skip(2);
			return lexTextBlockLiteral(pos);
		}
		// TODO use indexOf and check if escaped
		while (hasMore()) {
			char peek = peek();
			if (peek == CHAR_ESCAPE_CHAR_CHAR) {
				next();
				if (hasMore()) {
					next(); // assuming the string is correct, we're skipping every escapable char, including "
				}
			} else if (next() == '"') {
				return new Token(TokenType.LITERAL, pos, this.nextPos);
			}
		}
		return null;
	}

	private Token lexTextBlockLiteral(int pos) {
		while (hasMore(2)) {
			char peek = peek();
			if (peek == CHAR_ESCAPE_CHAR_CHAR) {
				next();
				if (hasMore()) {
					next();// assuming the string is correct, we're skipping every escapable char, including "
				}
			} else if (peek() == '"' && peek(1) == '"' && peek(2) == '"') {
				skip(3);
				return new Token(TokenType.LITERAL, pos, this.nextPos);
			} else {
				next();
			}
		}
		return null;
	}

	private Token lexCharacterLiteral(int startPos) {
		while (hasMore()) {
			char peek = peek();
			if (peek == CHAR_ESCAPE_CHAR_CHAR) {
				skip(2);
				continue; // "unsafe" check, assuming there is a closing '
			} else if (peek == '\'') {
				next();
				return new Token(TokenType.LITERAL, startPos, this.nextPos);
			}
			next();
		}
		return null;
	}

	private Token singleOrDoubleOperator(int startPos, char nextForDouble) {
		if (hasMore() && peek() == nextForDouble) {
			next();
		}
		return new Token(TokenType.OPERATOR, startPos, this.nextPos);
	}

	private Token singleOrDoubleOperator(int startPos, char... anyNext) {
		if (hasMore()) {
			char peek = peek();
			for (char c : anyNext) {
				if (peek == c) {
					next();
					break;
				}
			}
		}
		return new Token(TokenType.OPERATOR, startPos, this.nextPos);
	}

	private void skip(int i) {
		this.nextPos += i;
	}

	private char peek() {
		return peek(0);
	}

	private char peek(int offset) {
		return this.content[this.nextPos + offset];
	}

	private char next() {
		return this.content[this.nextPos++];
	}

	private boolean hasMore() {
		return hasMore(0);
	}

	private boolean hasMore(int i) {
		return this.nextPos + i < this.end;
	}

	static volatile Token store;
	static volatile char[] lastContent;

	public static void main(String[] args) throws IOException {
		Path all = Path.of("src/main/java");
		Path guava = Path.of("C:\\Users\\Hannes\\Desktop\\deleteme\\guava");
		Path jdk = Path.of("C:\\Users\\Hannes\\Desktop\\deleteme\\jdk");
		Path other = Path.of("src/");
		class C {
			private final Path file;
			private final char[] content;

			C(Path file, char[] content) {
				this.file = file;
				this.content = content;
			}
		}
		try (Stream<Path> stream = Files.walk(jdk)) {
			IntSummaryStatistics statistics = stream
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(".java"))
					.map(path -> {
						try {
							return new C(path, Files.readString(path).toCharArray());
						} catch (IOException e) {
							System.out.println("Couldn't read " + path);
							return null;
						}
					})
					.filter(Objects::nonNull)
					.mapToInt(c -> {
						char[] chars = c.content;
						int count = 0;
						JavaLexer javaLexer = new JavaLexer(chars, 0, chars.length);
						// System.out.println("Processing file " + c.file + " of length " + chars.length);
						while (true) {
							Token lex = javaLexer.lex();
							if (lex == null) {
								if (lastContent == c.content) {
									String value = store.valueForContent(c.content);
									if (!"}".equals(value) && (!";".equals(value) && !c.file.getFileName().toString().equals("package-info.java"))) {
										System.out.println("suspicious end: " + store.formatted(c.content) + " (file: " + c.file + ")");
									}
								}
								return count;
							}
							count++;
							store = lex;
							lastContent = c.content;
							// System.out.println(store);
						}
					}).summaryStatistics();
			System.out.println("stats: " + statistics);
		}
	}

	private boolean skipWhitespaces() {
		while (hasMore() && Character.isWhitespace(peek())) {
			next();
		}
		return hasMore();
	}

	private boolean skipCommentsAndWhitespaces() {
		boolean retry;
		do {
			retry = false;
			skipWhitespaces();
			if (hasMore(2) && peek() == '/') {
				if (peek(1) == '/') {
					skipUntilLineBreak();
					retry = true;
				} else if (peek(1) == '*') {
					if (!skipUntil("*/")) {
						this.nextPos = this.end; //
						return false;
					}
					retry = true;
				}
			}
		} while (retry);
		return hasMore();
	}

	int indexOf(char c, int start) {
		for (int i = start; i < this.end; i++) {
			if (this.content[i] == c) {
				return i;
			}
		}
		return -1;
	}

}
