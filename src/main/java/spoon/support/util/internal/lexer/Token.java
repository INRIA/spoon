/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal.lexer;

/**
 * @param start the inclusive start position
 * @param end   the exclusive end position
 */
public record Token(TokenType type, int start, int end) {
	private static final int COLUMN_WIDTH = 8;

	/**
	 * {@return the number of chars this token covers}
	 */
	public int length() {
		return end - start;
	}

	/**
	 * {@return the value this token represents for the original content}
	 * @param content the original content.
	 */
	public String valueForContent(char[] content) {
		return new String(content, this.start, this.end - this.start);
	}

	/**
	 * {@return a formatted string representing this token, given the original content}
	 * @param content the original content.
	 */
	public String formatted(char[] content) {
		String type = " ".repeat(10 - this.type.name().length()) + this.type.name();
		String s = String.valueOf(this.start);
		String start = padFailsafe(COLUMN_WIDTH, s);
		String e = String.valueOf(this.end);
		String end = padFailsafe(COLUMN_WIDTH, e);
		return "Token[type: " + type + ", start: " + start + ", end: " + end + ", content: " + valueForContent(content);
	}


	private static String padFailsafe(int width, String content) {
		int count = width - content.length();
		return " ".repeat(Math.max(0, count)) + content;
	}
}
