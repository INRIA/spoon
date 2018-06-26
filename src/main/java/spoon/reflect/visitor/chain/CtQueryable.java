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
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Represents an object on which one can make queries.
 * It is implemented
 * <ol>
 * <li> by {@link CtElement} to allow creation of a new query on
 * children of an element.
 * <li> by {@link CtQuery} to allow reusable queries and chaining query steps.
 * </ol>
 *
 * The main methods are documented in CtQuery
 */
public interface CtQueryable {

	/**
	 * @see CtQuery#filterChildren(Filter)
	 */
	<R extends CtElement> CtQuery filterChildren(Filter<R> filter);

	/**
	 * @see CtQuery#map(CtFunction)
	 */
	<I, R> CtQuery map(CtFunction<I, R> function);

	/**
	 * @see CtQuery#map(CtConsumableFunction)
	 */
	<I> CtQuery map(CtConsumableFunction<I> queryStep);

}
