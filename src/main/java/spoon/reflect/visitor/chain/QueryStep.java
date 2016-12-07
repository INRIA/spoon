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

import java.util.List;

/**
 *
 *
 *
 * @param <O> the type of the element produced by this QueryStep
 */
public interface QueryStep<O> extends Consumer<Object> {

	QueryStep<Object> getPrev();

	<R> QueryStep<R> map(QueryStep<R> queryStep);

	<P> QueryStep<P> map(AsyncFunction<?, P> code);

	/**
	 * It behaves depending on the type of returned value like this:
	 * <table>
	 * <tr><td><b>Return type</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Sends input to the next step if returned value is true
	 * <tr><td>{@link Iterable}<td>Sends each item of Iterable to the next step
	 * <tr><td>? extends {@link Object}<td>Sends returned value to the next step
	 * </table><br>
	 * @param code a Function with one parameter of type I returning value of type R
	 * @return
	 */
	<I, R> QueryStep<R> map(Function<I, R> code);

	/**
	 * scan all child elements of input element. Only these elements are sent to, which predicate.matches(element)==true
	 *
	 * @param predicate filters scanned
	 * @return
	 */
	<P> QueryStep<P> scan(Predicate<P> predicate);

	List<O> list();

	<R> void forEach(Consumer<R> consumer);

	<T, R> void apply(T input, Consumer<R> output);
}
