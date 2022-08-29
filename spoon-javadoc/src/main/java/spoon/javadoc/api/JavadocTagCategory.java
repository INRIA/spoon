/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api;

/**
 * The category (block or inline) a javadoc tag belongs to. A tag might be able to be used as
 * <em>both</em> (e.g. {@code @return}.
 */
public enum JavadocTagCategory {
	INLINE,
	BLOCK,
}
