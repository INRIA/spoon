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

import java.lang.reflect.Array;

import spoon.Launcher;
import spoon.support.util.SafeInvoker;

/**
 * {@link QueryStep} which uses a {@link Function} as a mapping function.<br><br>
 * It behaves depending on the type of returned value like this:
 * <table>
 * <tr><td><b>Return type</b><td><b>Behavior</b>
 * <tr><td>{@link Boolean}<td>Sends input to the next step if returned value is true
 * <tr><td>{@link Iterable}<td>Sends each item of Iterable to the next step
 * <tr><td>{@link Object[]}<td>Sends each item of Array to the next step
 * <tr><td>? extends {@link Object}<td>Sends returned value to the next step
 * </table><br>
 *
 * If the type of QueryStep input is not assignable to type of input parameter of the function
 * then such input is silently ignored
 *
 * @param <O> the type of the element produced by this {@link QueryStep}.
 */
public class FunctionQueryStep<O> extends QueryStepImpl<O> {

	private SafeInvoker<Function<?, ?>> code = new SafeInvoker<>("apply", 1);

	public <I> FunctionQueryStep(Function<I, O> code) {
		this.code.setDelegate(code);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Object input) {
		Object result;
		//check that input type can be assigned to TypeX of Function.apply(TypeX element)
		if (code.isParameterTypeAssignableFrom(input)) {
			try {
				result = code.invoke(input);
			} catch (ClassCastException e) {
				//in case of Lambda expressions, the type of apply method cannot be detected,
				//so then it fails with CCE. Handle it silently with meaning: "input element does not match. Ignore it"
				return;
			}
		} else {
			return;
		}
		if (result == null) {
			return;
		}
		if (result instanceof Boolean) {
			//the code is a predicate. send the input to output if result is true
			if ((Boolean) result) {
				fireNext((O) input);
			} else {
				if (Launcher.LOGGER.isDebugEnabled()) {
					Launcher.LOGGER.debug(getDescription() + " predicate is false on " + input);
				}
			}
		}
		if (result instanceof Iterable) {
			//send each item of Iterable to the next step
			for (O out : (Iterable<O>) result) {
				fireNext(out);
			}
		} else if (result.getClass().isArray()) {
			//send each item of Array to the next step
			for (int i = 0; i < Array.getLength(result); i++) {
				fireNext(Array.get(result, i));
			}
		} else {
			fireNext((O) result);
		}
	}

	@Override
	public QueryStep<O> setLogging(boolean logging) {
		code.setLogging(logging);
		return super.setLogging(logging);
	}
}
