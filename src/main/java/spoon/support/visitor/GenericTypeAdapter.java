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
package spoon.support.visitor;

import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

/**
 * Provides adapting of generic types from one scope to another scope.
 */
public interface GenericTypeAdapter {
	/**
	 * adapts `type` to the {@link CtTypeReference}
	 * of the scope of this {@link ClassTypingContext}
	 *
	 * This mapping function is able to resolve {@link CtTypeParameter} of:<br>
	 * A) input type or any super class or any enclosing class of input type or it's super class<br>
	 * B) super interfaces of input type or super interfaces of it's super classes.<br>
	 *
	 * @param type to be adapted type
	 * @return {@link CtTypeReference} adapted to scope of this {@link ClassTypingContext}
	 * or null if type cannot be adapted to this `scope`.
	 */
	CtTypeReference<?> adaptType(CtTypeInformation type);

	/**
	 * @return the {@link GenericTypeAdapter}, which adapts generic types of enclosing type
	 */
	GenericTypeAdapter getEnclosingGenericTypeAdapter();
}
