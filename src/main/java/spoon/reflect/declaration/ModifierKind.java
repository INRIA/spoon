/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

/**
 * Represents a modifier on the declaration of a program element such as a
 * class, method, or field.
 *
 * The order is important, because it is always pretty--printed is this order, enabling to have a JLS-compliant,
 * checkstyle compliant generated code (thanks to EnumSet used for modifiers).
 */

public enum ModifierKind {

	/**
	 * The modifier <tt>public</tt>
	 */
	PUBLIC,
	/**
	 * The modifier <tt>protected</tt>
	 */
	PROTECTED,
	/**
	 * The modifier <tt>private</tt>
	 */
	PRIVATE,
	/**
	 * The modifier <tt>abstract</tt>
	 */
	ABSTRACT,
	/**
	 * The modifier <tt>static</tt>
	 */
	STATIC,
	/**
	 * The modifier <tt>final</tt>
	 */
	FINAL,
	/**
	 * The modifier <tt>transient</tt>
	 */
	TRANSIENT,
	/**
	 * The modifier <tt>volatile</tt>
	 */
	VOLATILE,
	/**
	 * The modifier <tt>synchronized</tt>
	 */
	SYNCHRONIZED,
	/**
	 * The modifier <tt>native</tt>
	 */
	NATIVE,
	/**
	 * The modifier <tt>strictfp</tt>
	 */
	STRICTFP;

	private String lowercase = null; // modifier name in lowercase

	/**
	 * Returns this modifier's name in lowercase.
	 */
	@Override
	public String toString() {
		if (lowercase == null) {
			lowercase = name().toLowerCase(java.util.Locale.US);
		}
		return lowercase;
	}

}
