/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 * 	<p>This package defines the Spoon's compile-time meta-model of Java programs.
 * <p>The meta-model defines a read/write compile-time meta-representation of Java 5 programs.
 * The programmers should instantiate or resolve the meta-elements by using {@link spoon.reflect.factory.Factory}'s sub-factories because it ensures
 * the model consistency. The {@link spoon.reflect.factory.CoreFactory} is the raw factory for program elements and is the
 * only factory to be implemented when wanting to provide an alternative implementation of the Spoon meta-model.
 * <h2>Related Documentation</h2>
 * <ul>
 * <li><a href="http://spoon.gforge.inria.fr/">Spoon Official Web Site</a>
 * </ul>
 */
package spoon.reflect;
