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
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * QueryComposer contains methods, which can be used to create/compose a {@link QueryStep}
 */
public interface QueryComposer {

	/**
	 * Appends queryStep to the current (last) step of the query
	 * and returns queryStep, which was entered. It is now the last query step
	 *
	 * @param queryStep
	 * @return the entered queryStep, which is now the last step of the query
	 */
	<R> QueryStep<R> map(QueryStep<R> queryStep);

	/**
	 * Creates new QueryStep, which will call code
	 * whenever this QueryStep produces an element.
	 * The produced element is sent to code.apply(input, consumer)
	 * method as input parameter, together with consumer,
	 * which will take all the produced elements and sent them
	 * to next query step
	 *
	 * @param code
	 * @return the create QueryStep, which is now the last step of the query
	 */
	<P> QueryStep<P> map(ChainableFunction<?, P> code);

	/**
	 * It behaves depending on the type of returned value like this:
	 * <table>
	 * <tr><td><b>Return type</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Sends input to the next step if returned value is true
	 * <tr><td>{@link Iterable}<td>Sends each item of Iterable to the next step
	 * <tr><td>? extends {@link Object}<td>Sends returned value to the next step
	 * </table><br>
	 *
	 * @param code a Function with one parameter of type I returning value of type R
	 * @return the create QueryStep, which is now the last step of the query
	 */
	<I, R> QueryStep<R> map(Function<I, R> code);

	/**
	 * scan all child elements of input element. Only these elements are sent to, which filter.matches(element)==true
	 *
	 * @param filter used to filter scanned elements
	 * @return the create QueryStep, which is now the last step of the query
	 */
	<T extends CtElement> QueryStep<T> scan(Filter<T> filter);
}
