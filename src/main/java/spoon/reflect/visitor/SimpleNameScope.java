/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.reflect.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;

/**
 * {@link LexicalScope} for exact set of named elements. There can be:
 * <ul>
 * <li>parameter names of constructor, method or lambda
 * <li>parameter name of catch variable
 * <li>local variables of block
 * </ul>
 */
class SimpleNameScope extends AbstractNameScope {

	private final Map<String, CtNamedElement> elementsByName;

	SimpleNameScope(LexicalScope parent, CtElement scopeElement, List<CtParameter<?>> parameters) {
		super(parent, scopeElement);
		elementsByName = new HashMap<>(parameters.size());
		for (CtParameter<?> parameter : parameters) {
			elementsByName.put(parameter.getSimpleName(), parameter);
		}
	}

	SimpleNameScope(LexicalScope parent, CtElement scopeElement) {
		super(parent, scopeElement);
		elementsByName = new HashMap<>();
	}

	@Override
	protected <T> T forEachLocalElementByName(String name, Function<? super CtNamedElement, T> consumer) {
		return forEachByName(elementsByName, name, consumer);
	}

	SimpleNameScope addVariable(CtVariable<?> var) {
		//do not check conflict here. It is OK that local variable hides parameter
		elementsByName.put(var.getSimpleName(), var);
		return this;
	}
}
