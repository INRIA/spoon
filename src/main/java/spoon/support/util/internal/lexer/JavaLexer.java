/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.lexer;

import org.jspecify.annotations.Nullable;
import spoon.support.util.internal.trie.Trie;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Character.isJavaIdentifierPart;

public class JavaLexer {
	private static final char ESCAPE_CHAR = '\\';
	private static final Trie<JavaKeyword> KEYWORD_TRIE = Trie.ofWords(
		Arrays.stream(JavaKeyword.values())
			.collect(Collectors.toMap(JavaKeyword::toString, Function.identity()))
	);
	private final char[] content;
	private final CharRemapper charRemapper;
	private int nextPos;

	/**
	 * Creates a java lexer for the given content and range.
	 *
	 * @param content the content to lex.
	 * @param start   the start offset of the range to lex (inclusive)
	 * @param end     the end offset of the range to lex (exclusive)
	 */
	public JavaLexer(char[] content, int start, int end) {
		this.charRemapper = new CharRemapper(content, start, end);
		this.content = this.charRemapper.remapContent();
		this.nextPos = 0;
	}

	/**
	 * {@return {@code null} if no more tokens can be lexed in the range of this lexer}
	 */
	public @Nullable Token lex() {
		if (!skipCommentsAndWhitespaces()) {
			return null;
		}
		int pos = this.nextPos;
		char c = next();
		return switch (c) {
			case '(', ')', '{', '}', '[', ']', ';', ',', '@' -> createToken(TokenType.SEPARATOR, pos);
			case '.' -> {
				if (hasMore(2) && peek() == '.' && peek(1) == '.') {
					skip(2);
				}
				yield createToken(TokenType.SEPARATOR, pos);
			}
			case ':' -> {
				if (hasMore() && peek() == ':') {
					next();
					yield createToken(TokenType.SEPARATOR, pos);
				}
				yield createToken(TokenType.OPERATOR, pos);
			}
			// no comment, already skipped
			case '/', '=', '*', '^', '%', '!' -> lexSingleOrDoubleOperator(pos, '=');
			case '+' -> lexSingleOrDoubleOperator(pos, '=', '+');
			case '-' -> lexSingleOrDoubleOperator(pos, '=', '-', '>');
			case '&' -> lexSingleOrDoubleOperator(pos, '=', '&');
			case '|' -> lexSingleOrDoubleOperator(pos, '=', '|');
			case '>' -> lexAngleBracket(pos, 1, 3, '>');
			case '<' -> lexAngleBracket(pos, 1, 2, '<');
			case '\'' -> lexCharacterLiteral(pos);
			case '"' -> lexStringLiteral(pos);
			case '~', '?' -> createToken(TokenType.OPERATOR, pos);
			default -> {
				skip(-1); // reset to previous
				yield lexLiteralOrKeywordOrIdentifier();
			}
		};
	}

	private Token createToken(TokenType type, int pos) {
		return new Token(type, this.charRemapper.remapPosition(pos), this.charRemapper.remapPosition(this.nextPos));
	}

	private Token lexAngleBracket(int pos, int found, int maxFound, char bracket) {
		if (hasMore()) {
			char peek = peek();
			if (peek == '=') {
				next();
				return createToken(TokenType.OPERATOR, pos);
			}
			if (peek == bracket && found < maxFound) {
				next();
				return lexAngleBracket(pos, found + 1, maxFound, bracket);
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

	private @Nullable Token lexLiteralOrKeywordOrIdentifier() {
		int pos = this.nextPos;
		char next = next(); // assuming next is available as `lex` already checks that
		if (Character.isJavaIdentifierStart(next)) {
			while (hasMore() && isJavaIdentifierPart(peek())) {
				next();
			}
			Optional<JavaKeyword> match = KEYWORD_TRIE.findMatch(this.content, pos, this.nextPos);
			if (match.isPresent()) {
				return createToken(TokenType.KEYWORD, pos);
			}
			// special case: non-sealed is not a valid java identifier, but a keyword
			if (isNonSealed(pos)) {
				skip("sealed".length() + 1); // move behind
				return createToken(TokenType.KEYWORD, pos);
			}
			return createToken(TokenType.IDENTIFIER, pos);
		} else if (Character.isDigit(next)) {
			readNumericLiteral(next);
			return createToken(TokenType.LITERAL, pos);
		}
		return null;
	}

	private boolean isNonSealed(int pos) {
		int requiredForNonSealed = "sealed".length();
		boolean startsWithNonSealed = hasMore(requiredForNonSealed)
			&& isNon(pos, this.content)
			&& isDashSealed(content, this.nextPos);
		if (startsWithNonSealed) {
			return !hasMore(requiredForNonSealed + 1)
				|| !isJavaIdentifierPart(peek(requiredForNonSealed + 1));
		}
		return false;
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

	private @Nullable Token lexStringLiteral(int pos) {
		if (hasMore(2) && peek() == '"' && peek(1) == '"') {
			skip(2);
			return lexTextBlockLiteral(pos);
		}
		while (hasMore()) {
			char peek = peek();
			if (peek == ESCAPE_CHAR) {
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

	private @Nullable Token lexTextBlockLiteral(int pos) {
		while (hasMore(2)) {
			char peek = peek();
			if (peek == ESCAPE_CHAR) {
				next();
				if (hasMore()) {
					next(); // assuming the string is correct, we're skipping every escapable char, including "
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

	private @Nullable Token lexCharacterLiteral(int startPos) {
		while (hasMore()) {
			char peek = peek();
			if (peek == ESCAPE_CHAR) {
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

	private Token lexSingleOrDoubleOperator(int startPos, char nextForDouble) {
		if (hasMore() && peek() == nextForDouble) {
			next();
		}
		return createToken(TokenType.OPERATOR, startPos);
	}

	private Token lexSingleOrDoubleOperator(int startPos, char... anyNext) {
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
		int p = pos;
		return content.length - p >= 3 && content[p++] == 'n' && content[p++] == 'o' && content[p] == 'n';
	}

	private static boolean isDashSealed(char[] content, int pos) {
		int p = pos;
		return content[p++] == '-'
			&& content[p++] == 's'
			&& content[p++] == 'e'
			&& content[p++] == 'a'
			&& content[p++] == 'l'
			&& content[p++] == 'e'
			&& content[p] == 'd';
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
						this.nextPos = this.content.length; // comment does not end, but the content does
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
