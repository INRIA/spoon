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
 * Abstraction for functions in the Spoon realm.
 * It is used in the query stack, for example by {@link CtQueryable#map(CtFunction)}
 * It is compatible with Java 8 lambdas, hence enable to write one-liner queries with lambdas.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface CtFunction<T, R> {
	R apply(T input);
}
