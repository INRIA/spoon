/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	/** Implement this method to transform the input typed by T into an object of type R */
	R apply(T input);
}
