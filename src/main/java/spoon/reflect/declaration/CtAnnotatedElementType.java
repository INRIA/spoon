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
