/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * visits siblings of input element.
 * The sibling is the element whose sibling.getParent()==input.getParent()
 * <br>
 * The siblings are visited in order in which they would be visited by CtScanner.
 * The input element is by default not visited. But if {@link #includingSelf(boolean)} is called with value true,
 * then input element is visited too in the order in which CtScanner would visit it.
 *
 *  The visiting order is relevant, because this scanner is used for example resolve local variable declarations.
 */
public class SiblingsFunction implements CtConsumableFunction<CtElement> {

	/**
	 * Defines visiting mode
	 */
	public enum Mode {
		ALL,	//all siblings are visited
		PREVIOUS, //only previous siblings of input element
		NEXT	//only next siblings of input element
	}

	private Mode mode = Mode.ALL;
	private boolean includingSelf = false;

	public SiblingsFunction() {
	}

	/**
	 * @param includingSelf if false then input element is not visited
	 */
	public SiblingsFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	/**
	 * @param mode defines which siblings will be visited. See {@link Mode} for possible values
	 */
	public SiblingsFunction mode(Mode mode) {
		this.mode = mode;
		return this;
	}

	@Override
	public void apply(final CtElement input, final CtConsumer<Object> outputConsumer) {
		final CtElement parent = input.getParent();
		parent.accept(new CtScanner() {
			boolean hasVisitedInput = false;
			boolean visitPrev = mode == Mode.ALL || mode == Mode.PREVIOUS;
			boolean visitNext = mode == Mode.ALL || mode == Mode.NEXT;
			@Override
			public void scan(CtElement element) {
				if (element != null && element.getParent() == parent) {
					//visit only elements whose parent is same
					boolean canVisit = hasVisitedInput ? visitNext : visitPrev;
					if (input == element) {
						hasVisitedInput = true;
						canVisit = includingSelf;
					}
					if (canVisit) {
						outputConsumer.accept(element);
					}
				}
			}
		});
	}
}
