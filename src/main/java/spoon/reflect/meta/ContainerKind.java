/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
