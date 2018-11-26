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
package spoon.decompiler;

import spoon.reflect.declaration.CtType;

public interface TypeTransformer {

	/**
	 * User's implementation of transformation to apply on type.
	 * @param type type to be transformed
	 */
	void transform(CtType type);

	/**
	 * User defined filter to discard type that will not be transformed by the SpoonClassFileTransformer.
	 * @param type type considered for transformation
	 */
	default boolean accept(CtType type) {
		return true;
	}
}
