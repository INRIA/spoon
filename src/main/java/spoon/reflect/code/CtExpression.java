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
package spoon.reflect.code;

import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.TemplateParameter;

import java.util.List;

/**
 * This abstract code element defines a typed expression.
 *
 * @param <T>
 * 		the "return type" of this expression
 */
public interface CtExpression<T> extends CtCodeElement, CtTypedElement<T>, TemplateParameter<T> {

	/**
	 * Returns the type casts if any.
	 */
	List<CtTypeReference<?>> getTypeCasts();

	/**
	 * Sets the type casts.
	 */
	<C extends CtExpression<T>> C setTypeCasts(List<CtTypeReference<?>> types);

	/**
	 * Adds a type cast.
	 */
	<C extends CtExpression<T>> C addTypeCast(CtTypeReference<?> type);

	/**
	 * Replaces this element by another one.
	 */
	<E extends T> void replace(CtExpression<E> element);

	@Override
	CtExpression<T> clone();
}
