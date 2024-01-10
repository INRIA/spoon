/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 *  This file originally comes from JavaParser and is distributed under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 */
package spoon.javadoc.internal;

/**
* An element of a description: either an inline tag or a piece of text.
*
* <p>So for example <code>a text</code> or <code>{@link String}</code> could be valid description
* elements.
* @deprecated Use the new javadoc parser submodule, see <a href="https://spoon.gforge.inria.fr/spoon_javadoc.html">Javadoc Parser</a>.
*/
@Deprecated(forRemoval = true, since = "11.0.0")
public interface JavadocDescriptionElement {
	/** pretty-prints the Javadoc fragment */
	String toText();
}
