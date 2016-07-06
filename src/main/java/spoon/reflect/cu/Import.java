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
package spoon.reflect.cu;

import spoon.reflect.reference.CtReference;

/**
 * This interface represents imports in a compilation unit. Imports are not part
 * of the AST and are generated automatically. However, when the auto-import
 * feature of a compilation unit is turned off, the programmer can manually
 * specify the imports to be done.
 *
 * @see spoon.compiler.Environment#isAutoImports()
 * @see spoon.compiler.Environment#setAutoImports(boolean)
 */
public interface Import {

	/**
	 * Gets the Java string declaration of the import.
	 */
	String toString();

	/**
	 * Gets the reference of the element that is imported.
	 */
	CtReference getReference();

}
