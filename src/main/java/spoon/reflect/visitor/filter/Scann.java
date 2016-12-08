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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.ChainableFunction;
import spoon.reflect.visitor.chain.Consumer;
import spoon.reflect.visitor.chain.QueryStep;

/**
 * A scanner which can be used as Query step.
 * In the Query chain (see {@link QueryStep}) it starts scanning from the QueryStep input element
 * and sends each visited child elment as output of this QueryStep
 */
public class Scann extends CtScanner implements ChainableFunction<CtElement, CtElement> {

	private Consumer<CtElement> output;

	public Scann() {
	}

	@Override
	public void apply(CtElement input, Consumer<CtElement> output) {
		this.output = output;
		scan(input);
	}

	@Override
	public void scan(CtElement element) {
		output.accept(element);
		super.scan(element);
	}
}
