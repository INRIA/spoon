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
 * Represents a part of source code.
 *
 * See https://github.com/INRIA/spoon/pull/2283
 */
@Experimental
public interface SourceFragment  {
	/**
	 * @return origin source code of whole fragment represented by this instance
	 */
	String getSourceCode();
}
