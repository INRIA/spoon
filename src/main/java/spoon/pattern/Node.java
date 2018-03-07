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
package spoon.pattern;

import java.util.List;
import java.util.function.BiConsumer;

import spoon.pattern.matcher.Matchers;
import spoon.pattern.matcher.TobeMatched;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.factory.Factory;

/**
 * Represents a parameterized Pattern ValueResolver, which can be used
 * <ul>
 * <li>to generate a zero, one or more copies of model using provided parameters</li>
 * <li>to match zero, one or more instances of model and deliver a matching parameters</li>
 * </ul>
 */
public interface Node extends Matchers {
	/**
	 * Calls consumer for each pair of parameter definition ({@link ParameterInfo}) and {@link Node}, which uses it
	 * @param consumer the receiver of pairs of {@link ParameterInfo} and {@link Node}
	 */
	void forEachParameterInfo(BiConsumer<ParameterInfo, Node> consumer);

	/**
	 * Generates zero, one or more target depending on kind of this {@link Node}, expected `result` and input `parameters`
	 * @param factory TODO
	 */
	<T> void generateTargets(Factory factory, ResultHolder<T> result, ParameterValueProvider parameters);

	/**
	 * Generates one target depending on kind of this {@link Node}, expected `expectedType` and input `parameters`
	 * @param factory TODO
	 * @param parameters {@link ParameterValueProvider}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a generate value or null
	 */
	default <T> T generateTarget(Factory factory, ParameterValueProvider parameters, Class<T> expectedType) {
		ResultHolder.Single<T> result = new ResultHolder.Single<>(expectedType);
		generateTargets(factory, result, parameters);
		return result.getResult();
	}

	/**
	 * Generates zero, one or more targets depending on kind of this {@link Node}, expected `expectedType` and input `parameters`
	 * @param factory TODO
	 * @param parameters {@link ParameterValueProvider}
	 * @param expectedType defines {@link Class} of returned value
	 *
	 * @return a {@link List} of generated targets
	 */
	default <T> List<T> generateTargets(Factory factory, ParameterValueProvider parameters, Class<T> expectedType) {
		ResultHolder.Multiple<T> result = new ResultHolder.Multiple<>(expectedType);
		generateTargets(factory, result, parameters);
		return result.getResult();
	}

	/**
	 * @param targets to be matched target nodes and input parameters
	 * @param nextMatchers Chain of matchers which has to be processed after this {@link Node}
	 * @return new parameters and container with remaining targets
	 */
	TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers);

	/**
	 * The special implementation of {@link Matchers}, which is used as last {@link Node} in case when ALL target nodes must match with all template nodes
	 */
	Matchers MATCH_ALL = new Matchers() {
		@Override
		public TobeMatched matchAllWith(TobeMatched targets) {
			//It matches only when there is no remaining target element
			return targets.hasTargets() ? null : targets;
		}
	};
	/**
	 * The special implementation of {@link Matchers}, which is used as last {@link Node} in case when SOME target nodes must match with all template nodes
	 */
	Matchers MATCH_PART = new Matchers() {
		@Override
		public TobeMatched matchAllWith(TobeMatched targets) {
			//There can remain some unmatched target(s) - it is OK in this context.
			return targets;
		}
	};

	@Override
	default TobeMatched matchAllWith(TobeMatched targets) {
		return matchTargets(targets, MATCH_PART);
	}

	/**
	 * @param oldNode old {@link Node}
	 * @param newNode new {@link Node}
	 * @return a true if `oldNode` was found in this {@link Node} or it's children and replaced by `newNode`
	 * false if `oldNode` was not found
	 */
	boolean replaceNode(Node oldNode, Node newNode);
}
