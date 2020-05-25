/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

/**
 * Define a visitable element in spoon. You can read the page Wikipedia http://en.wikipedia.org/wiki/Visitor_pattern.
 */
public interface CtVisitable {
	/**
	 * Accepts a visitor
	 */
	void accept(CtVisitor visitor);
}
