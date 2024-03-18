/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.lexer;

import spoon.support.util.internal.trie.Trie;

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
	private static final Trie<JavaKeyword> KEYWORD_TRIE = Trie.ofWords(
			Arrays.stream(JavaKeyword.values())
					.collect(Collectors.toMap(JavaKeyword::toString, Function.identity()))
	);
	private final char[] content;
	private final CharStream charStream;
	private int nextPos;

	public JavaLexer(char[] content, int start, int end) {
		this.charStream = new CharStream(content, start, end);
		this.content = this.charStream.readAll();
		this.nextPos = 0;
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
				return createToken(TokenType.SEPARATOR, pos);
			case '.':
				if (hasMore(2) && peek() == '.' && peek(1) == '.') {
					skip(2);
				}
				return createToken(TokenType.SEPARATOR, pos);
			case ':':
				if (hasMore() && peek() == ':') {
					next();
					return createToken(TokenType.SEPARATOR, pos);
				}
				return createToken(TokenType.OPERATOR, pos);
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
				return createToken(TokenType.OPERATOR, pos);
			default:
				skip(-1); // reset to previous
				return lexLiteralOrKeywordOrIdentifier();
		}
	}

	private Token createToken(TokenType type, int pos) {
		return new Token(type, this.charStream.remapPosition(pos), this.charStream.remapPosition(this.nextPos));
	}

	private Token angleBracket(int pos, int found, int maxFound, char bracket) {
		if (hasMore()) {
			char peek = peek();
			if (peek == '=') {
				next();
				return createToken(TokenType.OPERATOR, pos);
			}
			if (peek == bracket && found < maxFound) {
				next();
				return angleBracket(pos, found + 1, maxFound, bracket);
			}
		}
		return createToken(TokenType.OPERATOR, pos);
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
			Optional<JavaKeyword> match = KEYWORD_TRIE.findMatch(this.content, pos, this.nextPos);
			if (match.isPresent()) {
				return createToken(TokenType.KEYWORD, pos);
			}
			// special case: non-sealed is not a valid java identifier, but a keyword
			if (hasMore("sealed".length())
					&& isNon(pos, this.content)
					&& isDashSealed(content, pos + 3) // skip the non
			) {
				skip("-sealed".length());
				return createToken(TokenType.KEYWORD, pos);
			}
			return createToken(TokenType.IDENTIFIER, pos);
		} else if (Character.isDigit(next)) {
			readNumericLiteral(next);
			return createToken(TokenType.LITERAL, pos);
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
				return createToken(TokenType.LITERAL, pos);
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
				return createToken(TokenType.LITERAL, pos);
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
				return createToken(TokenType.LITERAL, startPos);
			}
			next();
		}
		return null;
	}

	private Token singleOrDoubleOperator(int startPos, char nextForDouble) {
		if (hasMore() && peek() == nextForDouble) {
			next();
		}
		return createToken(TokenType.OPERATOR, startPos);
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
		return createToken(TokenType.OPERATOR, startPos);
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
		return this.nextPos + i < this.content.length;
	}

	private static boolean isNon(int pos, char[] content) {
		return content.length - pos >= 3 && content[pos++] == 'n' && content[pos++] == 'o' && content[pos] == 'n';
	}

	private static boolean isDashSealed(char[] content, int pos) {
		return content[pos++] == '-' &&
				content[pos++] == 's' &&
				content[pos++] == 'e' &&
				content[pos++] == 'a' &&
				content[pos++] == 'l' &&
				content[pos++] == 'e' &&
				content[pos] == 'd';
	}

	static volatile Token store;
	static volatile char[] lastContent;

	public static void main(String[] args) throws IOException {
		if (true) {
			char[] content = """
					public void \\ud801\\udc00method() {};
					""".toCharArray();
			// char[] content = "\\u007b".toCharArray();
			JavaLexer lexer = new JavaLexer(content, 0, content.length);
			Token token;
			while ((token = lexer.lex()) != null) {
				System.out.println(token.formatted(content));
			}

			// return;
		}
		Path all = Path.of("src/main/java");
		// Path guava = Path.of("~/IdeaProjects/jdk");
		Path jdk = Path.of("/home/hannes/IdeaProjects/jdk");
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
						this.nextPos = this.content.length; //
						return false;
					}
					retry = true;
				}
			}
		} while (retry);
		return hasMore();
	}

	int indexOf(char c, int start) {
		for (int i = start; i < this.content.length; i++) {
			if (this.content[i] == c) {
				return i;
			}
		}
		return -1;
	}

}
