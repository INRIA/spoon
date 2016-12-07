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

public class StartQueryStep<O> extends QueryStepImpl<O> {

	private List<O> inputs;

	@SafeVarargs
	public StartQueryStep(O... inputs) {
		super();
		this.inputs = new ArrayList<>(inputs.length);
		for (O in : inputs) {
			this.inputs.add(in);
		}
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
		for (O in : inputs) {
			fireNext(in);
		}
	}

	public List<O> getInputs() {
		return inputs;
	}

	public void setInputs(List<O> inputs) {
		this.inputs = inputs;
	}

	public void addInputs(List<O> inputs) {
		this.inputs = inputs;
	}
}
