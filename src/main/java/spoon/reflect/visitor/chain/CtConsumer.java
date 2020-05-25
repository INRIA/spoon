/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

/**
 * The functional interface used to receive objects.
 * It is used for example to receive results of the query in {@link CtQuery#forEach(CtConsumer)}
 *
 * @param <T> - the type of accepted elements
 */
public interface CtConsumer<T> {
	/** Implement this method to do something with object "t" passed as parameter */
	void accept(T t);
}
