/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

/**
 * This enum specifies the element type which is annotated by the annotation
 */
public enum CtAnnotatedElementType {
	/**
	 * Class, interface (including annotation type), or enum declaration
	 */
	TYPE,

	/**
	 * Field declaration (includes enum constants)
	 */
	FIELD,

	/**
	 * Method declaration
	 */
	METHOD,

	/**
	 * Parameter declaration
	 */
	PARAMETER,

	/**
	 * Constructor declaration
	 */
	CONSTRUCTOR,

	/**
	 * Local variable declaration
	 */
	LOCAL_VARIABLE,

	/**
	 * Annotation type declaration
	 */
	ANNOTATION_TYPE,

	/**
	 * Package declaration
	 */
	PACKAGE,

	/**
	 * Type parameter declaration
	 */
	TYPE_PARAMETER,

	/**
	 * Use of a type
	 */
	TYPE_USE
}
