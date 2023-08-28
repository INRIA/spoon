/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.internal;

/**
* An element of a description: either an inline tag or a piece of text.
*
* <p>So for example <code>a text</code> or <code>{@link String}</code> could be valid description
* elements.
*/
public interface JavadocDescriptionElement {
	/** pretty-prints the Javadoc fragment */
	String toText();
}
