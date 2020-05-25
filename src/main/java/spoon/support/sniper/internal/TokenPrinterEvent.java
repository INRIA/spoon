/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.path.CtRole;

/**
 * Represents an action of Printer, which prints a token
 */
public abstract class TokenPrinterEvent implements PrinterEvent {
	private final TokenType type;
	private final String token;
	private final CtComment comment;

	public TokenPrinterEvent(TokenType type, String token, CtComment comment) {
		this.type = type;
		this.token = token;
		this.comment = comment;
	}

	@Override
	public CtRole getRole() {
		if (type == TokenType.COMMENT) {
			return CtRole.COMMENT;
		}
		if (isModifierKeyword(type, token)) {
			return CtRole.MODIFIER;
		}
		return null;
	}

	@Override
	public SourcePositionHolder getElement() {
		return comment;
	}

	/** @return printed token or null if printing complex element or comment */
	public String getToken() {
		return token;
	}

	private static final Set<String> modifierKeywords = new HashSet<>(Arrays.asList(
			"public", "protected", "private", "static", "default", "final"));

	static boolean isModifierKeyword(TokenType tokenType, String token) {
		return tokenType == TokenType.KEYWORD && modifierKeywords.contains(token);
	}

	/** @return true if printing white space token. It means New line, space or TAB. */
	public boolean isWhitespace() {
		return type.isWhiteSpace();
	}

	/** Returns the token type */
	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.name() + ": \'" + token + "\'";
	}

	public TokenType getTokenType() {
		return type;
	}
}

