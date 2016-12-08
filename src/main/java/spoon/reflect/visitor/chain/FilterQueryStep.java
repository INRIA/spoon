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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.support.util.SafeInvoker;

/**
 * {@link QueryStep} which uses a {@link Filter} as a mapping function.
 * If the {@link Filter#matches(CtElement)} returns true then input element is sent to QueryStep output.
 * Otherwise input element is skipped - output is not produced
 *
 * It is used internally in {@link QueryStep#scan(Filter)}.
 *
 * @param <O> the type of the element produced by this {@link QueryStep}
 */
public class FilterQueryStep<O extends CtElement> extends QueryStepImpl<O> {

	private SafeInvoker<Filter<O>> code = new SafeInvoker<>("matches", 1);

	public <I> FilterQueryStep(Filter<O> filter) {
		this.code.setDelegate(filter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Object input) {
		boolean matches = false;
		//check that input type can be assigned to TypeX of Fitler.matches(TypeX element)
		if (code.isParameterTypeAssignableFrom(input)) {
			try {
				matches = (Boolean) this.code.invoke(input);
			} catch (ClassCastException e) {
				//in case of Lambda expressions, the type of matches method cannot be detected,
				//so then it fails with CCE. Handle it silently with meaning: "input element does not match. Ignore it"
			}
		}
		if (matches) {
			//send input to output, because Fitler.matches returned true
			fireNext((O) input);
		}
	}
}
