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

/**
 * Functional interface, which represents a algorithm,
 * which returns one or more results by calling of output.accept(oneResult)<br>
 * It is used by {@link CtQueryable#map(CtLazyFunction)} to efficiently implement CtScanner based queries.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result produced by this function
 */
public interface CtLazyFunction<T, R> {
	/**
	 * @param input the input of the function
	 * @param outputConsumer the consumer which accepts the results of this function.
	 */
	void apply(T input, CtConsumer<R> outputConsumer);
}
