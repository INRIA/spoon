/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.visitor.TokenWriter;

/**
 * Type of {@link TokenSourceFragment} token.
 * Note: These types mirrors the methods of {@link TokenWriter}
 */
public enum TokenType {

	SEPARATOR(false, false),
	OPERATOR(false, false),
	LITERAL(false, false),
	KEYWORD(false, false),
	IDENTIFIER(false, false),
	CODE_SNIPPET(false, false),
	COMMENT(false, false),
	NEW_LINE(true, false),
	INC_TAB(true, true),
	DEC_TAB(true, true),
	SPACE(true, false);

	private final boolean whiteSpace;
	private final boolean tab;

	TokenType(boolean whiteSpace, boolean tab) {
		this.whiteSpace = whiteSpace;
		this.tab = tab;
	}
	boolean isWhiteSpace() {
		return whiteSpace;
	}
	public boolean isTab() {
		return tab;
	}
}
