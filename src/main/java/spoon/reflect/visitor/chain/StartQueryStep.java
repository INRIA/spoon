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

import java.util.ArrayList;
import java.util.List;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

/**
 * The QueryStep, which knows the constant list of input elements.
 * Is automatically created by {@link CtElement#map()} and  {@link CtElement#scan()}
 * To assure that instance of CtElement is used as input of the query when {@link QueryStep#forEach(Consumer)} or {@link QueryStep#list()} are used
 *
 * @param <O> - output type
 */
public class StartQueryStep<O> extends QueryStepImpl<O> {

	private List<O> inputs;

	@SafeVarargs
	public StartQueryStep(O... inputs) {
		super();
		addInput(inputs);
	}

	@Override
	public void accept(Object input) {
		if (input != null) {
			fireNext(input);
			if (inputs.size() > 0) {
				throw new SpoonException("QueryHead may have only one input");
			}
		} else {
			run();
		}
	}

	public void run() {
		if (inputs.size() == 0) {
			throw new SpoonException("First QueryStep has registered no input");
		}
		for (O in : inputs) {
			fireNext(in);
		}
	}

	/**
	 * @return list of elements which will be used as input of the query
	 */
	public List<O> getInputs() {
		return inputs;
	}

	/**
	 * sets list of elements which will be used as input of the query
	 */
	public void setInputs(List<O> inputs) {
		this.inputs = new ArrayList<>(inputs);
	}

	/**
	 * adds list of elements which will be used as input of the query too
	 * @param inputs
	 */
	public void addInput(O... inputs) {
		if (this.inputs == null) {
			this.inputs = new ArrayList<>(inputs.length);
		}
		for (O in : inputs) {
			this.inputs.add(in);
		}
	}
}
