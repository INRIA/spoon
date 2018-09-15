/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
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
