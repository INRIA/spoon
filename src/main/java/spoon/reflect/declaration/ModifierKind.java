/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
