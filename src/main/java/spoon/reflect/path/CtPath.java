/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.path;

import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 * A CtPath allows to define the path to a CtElement in the Spoon model, eg ".spoon.test.path.Foo.foo#body#statement[index=0]"
 */
public interface CtPath {

	/**
	 * Search for elements matching this CtPatch from start nodes given as parameters.
	 */
	<T extends CtElement> List<T> evaluateOn(CtElement... startNode);

	/**
	 *
	 * Returns the path that is relative to the given element (subpath from it to the end of the path).
	 * This is used to have relative paths, instead of absolute path from the root package.
	 *
	 * For example,
	 * "#typeMember[index=2]#body#statement[index=2]#else"
	 * is a relative path to the class of absolute path
	 * "#subPackage[name=spoon]#subPackage[name=test]#subPackage[name=path]#subPackage[name=testclasses]#containedType[name=Foo]#typeMember[index=2]#body#statement[index=2]#else"
	 */
	CtPath relativePath(CtElement parent);

}
