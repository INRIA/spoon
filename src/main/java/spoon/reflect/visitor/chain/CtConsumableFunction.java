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
