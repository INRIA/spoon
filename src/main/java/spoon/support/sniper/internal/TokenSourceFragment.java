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
		super();
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
