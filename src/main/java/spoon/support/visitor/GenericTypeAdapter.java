/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

/**
 * Provides adapting of generic types from one scope to another scope.
 */
public interface GenericTypeAdapter {
	/**
	 * @return the scope of this type adapter
	 */
	CtFormalTypeDeclarer getAdaptationScope();
	/**
	 * adapts `type` to the {@link CtTypeReference}
	 * of the scope of this {@link GenericTypeAdapter}
	 *
	 * This mapping function is able to resolve {@link CtTypeParameter} of:<br>
	 * A) input type or any super class or any enclosing class of input type or it's super class<br>
	 * B) super interfaces of input type or super interfaces of it's super classes.<br>
	 *
	 * The type reference is adapted recursive including all it's actual type arguments and bounds.
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
