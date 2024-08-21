/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	PUBLIC("public"),
	/**
	 * The modifier <tt>protected</tt>
	 */
	PROTECTED("protected"),
	/**
	 * The modifier <tt>private</tt>
	 */
	PRIVATE("private"),
	/**
	 * The modifier <tt>abstract</tt>
	 */
	ABSTRACT("abstract"),
	/**
	 * The modifier <tt>static</tt>
	 */
	STATIC("static"),
	/**
	 * The modifier <tt>final</tt>
	 */
	FINAL("final"),
	/**
	 * The modifier <tt>transient</tt>
	 */
	TRANSIENT("transient"),
	/**
	 * The modifier <tt>volatile</tt>
	 */
	VOLATILE("volatile"),
	/**
	 * The modifier <tt>synchronized</tt>
	 */
	SYNCHRONIZED("synchronized"),
	/**
	 * The modifier <tt>native</tt>
	 */
	NATIVE("native"),
	/**
	 * The modifier <tt>strictfp</tt>
	 */
	STRICTFP("strictfp"),
	/**
	 * The modifier <tt>non-sealed</tt>
	 */
	NON_SEALED("non-sealed"),
	/**
	 * The modifier <tt>sealed</tt>
	 */
	SEALED("sealed");

	ModifierKind(String lowercase) {
		this.lowercase = lowercase;
	}

	private final String lowercase; // modifier name in lowercase

	/**
	 * Returns this modifier's name in lowercase.
	 */
	@Override
	public String toString() {
		return lowercase;
	}

}
