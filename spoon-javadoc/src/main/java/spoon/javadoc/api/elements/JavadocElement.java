/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

/**
 * A semantic part of a javadoc comment.
 */
public interface JavadocElement {

	/**
	 * Accepts a javadoc visitor by calling the appropriate visit method.
	 *
	 * @param visitor the visitor to accept
	 * @param <T> the return type of the visitor
	 * @return the value returned by the visitor
	 */
	<T> T accept(JavadocVisitor<T> visitor);
}
