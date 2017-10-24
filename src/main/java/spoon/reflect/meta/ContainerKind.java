/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.meta;

/**
 * Defines kind of container, which is used in an attribute of Spoon model
 */
public enum ContainerKind {
	/**
	 * it is a single value field
	 * Example: CtClassImpl.simpleName
	 */
	SINGLE,
	/**
	 * It is a list of values
	 * Example: CtClassImpl.typeMembers
	 */
	LIST,
	/**
	 * It is a set of values
	 * Example: CtPackageImpl.types
	 */
	SET,
	/**
	 * It is a map&lt;String, T&gt; of values
	 * Example: CtAnnotationImpl.elementValues
	 */
	MAP;
}
