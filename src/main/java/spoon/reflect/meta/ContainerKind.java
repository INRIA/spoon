/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta;

/**
 * Represents a kind of data, which is used in an field or in a role in the Spoon metamodel.
 * See also {@link spoon.metamodel.MetamodelProperty}.
 */
public enum ContainerKind {
	/**
	 * It is a single value field
	 * Example: {@link spoon.support.reflect.declaration.CtClassImpl#simpleName}
	 */
	SINGLE,


	/**
	 * It is a list of values
	 * Example: {@link spoon.support.reflect.declaration.CtClassImpl#typeMembers}
	 */
	LIST,


	/**
	 * It is a set of values
	 * Example: {@link spoon.support.reflect.declaration.CtPackageImpl#types}
	 */
	SET,


	/**
	 * It is a map&lt;String, T&gt; of values
	 * Example: {@link spoon.support.reflect.declaration.CtAnnotationImpl#elementValues}
	 */
	MAP;
}
