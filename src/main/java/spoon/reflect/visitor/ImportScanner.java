/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

public interface ImportScanner {
	/**
	 * Computes import of a {@link spoon.reflect.declaration.CtType}
	 * (represent a class).
	 *
	 * @return Imports computes by Spoon, not original imports.
	 */
	Collection<CtTypeReference<?>> computeImports(CtType<?> simpleType);

	/**
	 * Computes imports for all elements.
	 */
	void computeImports(CtElement element);

	/**
	 * Checks if the type is already imported.
	 */
	boolean isImported(CtTypeReference<?> ref);
}
