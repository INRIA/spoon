package spoon.support.util.internal.lexer;

import javax.lang.model.SourceVersion;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

public class JavaLexer {
	private static final char CHAR_ESCAPE_CHAR_CHAR = '\\';
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
	 *
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

	private void skipUntil(String s) {
		char[] chars = s.toCharArray();
		char first = chars[0];
		int pos = this.nextPos;
		do {
			int index = indexOf(first, pos);
			if (index < 0) {
				return;
			}
			if (Arrays.equals(this.content, index, index + chars.length, chars, 0, chars.length)) {
				this.nextPos = index + chars.length;
				return;
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
			String identifier = new String(this.content, pos, this.nextPos - pos);
			if (SourceVersion.isKeyword(identifier)) {
				return new Token(TokenType.KEYWORD, pos, this.nextPos);
			}
			return new Token(TokenType.IDENTIFIER, pos, this.nextPos);
		} else if (Character.isDigit(next)) {
			if (next == 0) {
				if (hasMore()) {
					switch (peek()) {
						case 'X':
						case 'x':
						case 'B':
						case 'b':
							next();
					}
					readIntegerLiteral();
				}
			}
			return new Token(TokenType.LITERAL, pos, this.nextPos);
		}
		return null;
	}
	// TODO deal with literals properly
	double d = 0x1.fffffeP+127f;

	private void readIntegerLiteral() {
		while (hasMore()) {
			char peek = peek();
			if ('0' <= peek && peek <= '9' || peek == '_') {
				next();
			} else {
				switch (peek) {
					case 'l':
					case 'L':
						next();
						return; // end of literal
				}
			}
		}
	}

	// TODO deal with escape sequences, e.g. "\""
	private Token lexStringLiteral(int pos) {
		if (hasMore(2) && peek() == '"' && peek(1) == '"') {
			skip(2);
			return lexTextBlockLiteral(pos);
		}
		while (hasMore()) {
			char peek = peek();
			if (peek == CHAR_ESCAPE_CHAR_CHAR) {

			}
			if (next() == '"') {
				return new Token(TokenType.LITERAL, pos, this.nextPos);
			}
		}
		return null;
	}

	private Token lexTextBlockLiteral(int pos) {
		while (hasMore(2)) {
			if (peek() == '"' && peek(1) == '"' && peek(2) == '"') {
				skip(2);
				return new Token(TokenType.LITERAL, pos, this.nextPos);
			}
			next();
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

	static volatile String store;
	public static void main(String[] args) throws IOException {
		Path all = Path.of("src/main/java");
		Path other = Path.of("src/main/java/spoon/support/util/internal/lexer");
		try (Stream<Path> stream = Files.walk(other)) {
			stream
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(".java"))
					.map(path -> {
						try {
							return Files.readString(path);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.map(String::toCharArray)
					.forEach(chars -> {
						JavaLexer javaLexer = new JavaLexer(chars, 0, chars.length);
						System.out.println();
						while (true) {
							Token lex = javaLexer.lex();
							if (lex == null) {
								if ("}".equals(store)) { // that doesn't make sense
									System.out.println("suspicious end: " + store);
								}
								return;
							}
							store = lex.formatted(chars);
							System.out.println(store);
						}
					});
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
				if (peek() == '/') {
					skipUntilLineBreak();
					retry = true;
				} else if (peek() == '*') {
					skipUntil("*/");
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
