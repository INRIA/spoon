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
