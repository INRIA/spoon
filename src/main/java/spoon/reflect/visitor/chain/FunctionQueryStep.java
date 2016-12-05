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

import spoon.Launcher;
import spoon.support.util.SafeInvoker;

public class FunctionQueryStep<O> extends QueryStep<O> {

	private SafeInvoker<Function<?, ?>> code = new SafeInvoker<>("apply", 1);

	public <I> FunctionQueryStep(Function<I, O> code) {
		this.code.setDelegate(code);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Object input) {
		Object result;
		if (code.isParameterTypeAssignableFrom(input)) {
			try {
				result = code.invoke(input);
			} catch (ClassCastException e) {
				code.onClassCastException(e, input);
				return;
			}
		} else {
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
			for (O out : (Iterable<O>) result) {
				fireNext(out);
			}
		} else {
			fireNext((O) result);
		}
	}
}
