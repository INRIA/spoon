/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.support.Experimental;

/**
 * a {@link SourceFragment} of some primitive String token,
 * like separator, operator, whitespace, ...
 */
@Experimental
public class TokenSourceFragment implements SourceFragment {

	private final String source;
	private final TokenType type;

	public TokenSourceFragment(String source, TokenType type) {
		this.source = source;
		this.type = type;
	}

	@Override
	public String getSourceCode() {
		return source;
	}

	/**
	 * @return type of token of this fragment
	 */
	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "|" + getSourceCode() + "|";
	}
}
