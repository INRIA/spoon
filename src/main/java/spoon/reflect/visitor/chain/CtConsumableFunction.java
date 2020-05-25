/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.chain;

/**
 * Represents a function, as {@link CtFunction}. However, the main difference is that
 * while a {@link CtFunction} returns something with a standard Java return keyword,
 * a {@link CtConsumableFunction} returns something by passing the returned object
 * as parameter to the given outpuConsumer#accept. This enables to write efficient and concise code in certain situations.
 * It also enables one to emulate several returns, by simply calling several times accept, while not paying
 * the code or performance price of creating a list or an iterable object.
 *
 * It is typically used as parameter of {@link CtQueryable#map(CtConsumableFunction)}, can be written as one-liners
 * with Java8 lambdas:.`cls.map((CtClass&lt;?&gt; c, CtConsumer&lt;Object&gt; out)-&gt;out.accept(c.getParent()))`
 *
 * @param <T> the type of the input to the function
 */
public interface CtConsumableFunction<T> {
	/**
	 * Evaluates the function on the given input.
	 * @param input the input of the function
	 * @param outputConsumer the consumer which accepts the results of this function.
	 */
	void apply(T input, CtConsumer<Object> outputConsumer);
}
