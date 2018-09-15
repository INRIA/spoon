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
		super();
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

	@Override
	public String getToken() {
		return token;
	}

	private static final Set<String> modifierKeywords = new HashSet<>(Arrays.asList(
			"public", "protected", "private", "static", "default", "final"));

	static boolean isModifierKeyword(TokenType tokenType, String token) {
		return tokenType == TokenType.KEYWORD && modifierKeywords.contains(token);
	}

	@Override
	public boolean isWhitespace() {
		return type.isWhiteSpace();
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.name() + ": \'" + token + "\'";
	}
}

