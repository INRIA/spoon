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

import spoon.support.util.SafeInvoker;
/**
 * {@link QueryStep} which uses a {@link ChainableFunction} as a mapping function.<br><br>
 *
 * If the type of QueryStep input is not assignable to type of the first input parameter of the function
 * then such input is silently ignored
 *
 * @param <O> the type of the element produced by this {@link QueryStep}.
 */
public class ChainableFunctionQueryStep<O> extends QueryStepImpl<O> {

	private SafeInvoker<ChainableFunction<?, ?>> code = new SafeInvoker<>("apply", 2);

	public <I> ChainableFunctionQueryStep(ChainableFunction<I, O> code) {
		this.code.setDelegate(code);
	}

	@Override
	public void accept(Object input) {
		//check that input type can be assigned to TypeX of Function.apply(TypeX element, Consumer c)
		if (code.isParameterTypeAssignableFrom(input, null)) {
			try {
				this.code.invoke(input, getNextConsumer());
			} catch (ClassCastException e) {
				//in case of Lambda expressions, the type of apply method cannot be detected,
				//so then it fails with CCE. Handle it silently with meaning: "input element does not match. Ignore it"
			}
		}
	}
}
