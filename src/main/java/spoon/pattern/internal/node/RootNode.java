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
package spoon.pattern.internal.node;

import java.util.function.BiConsumer;

import spoon.pattern.Generator;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.matcher.Matchers;
import spoon.pattern.internal.matcher.TobeMatched;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.support.util.ImmutableMap;

/**
 * Represents a parameterized Pattern ValueResolver, which can be used
 * <ul>
 * <li>to generate a zero, one or more copies of model using provided parameters</li>
 * <li>to match zero, one or more instances of model and deliver a matching parameters</li>
 * </ul>
 */
public interface RootNode extends Matchers {
	/**
	 * Calls consumer for each pair of parameter definition ({@link ParameterInfo}) and {@link RootNode}, which uses it
	 * @param consumer the receiver of pairs of {@link ParameterInfo} and {@link RootNode}
	 */
	void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer);

	/**
	 * Generates zero, one or more target depending on kind of this {@link RootNode}, expected `result` and input `parameters`
	 * @param generator {@link Generator} which drives generation process
	 * @param result holder for the generated objects
	 * @param parameters a {@link ImmutableMap} holding parameters
	 */
	<T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters);

	/**
	 * @return true if generated result has to be evaluated to apply simplifications.
	 * e.g. ("a" + "b") id simplified to "ab"
	 */
	boolean isSimplifyGenerated();
	/**
	 * @param simplifyGenerated true if generated result of this {@link RootNode} has to be evaluated to apply simplifications.
	 */
	void setSimplifyGenerated(boolean simplifyGenerated);

	/**
	 * @param targets to be matched target nodes and input parameters
	 * @param nextMatchers Chain of matchers which has to be processed after this {@link RootNode}
	 * @return new parameters and container with remaining targets
	 */
	TobeMatched matchTargets(TobeMatched targets, Matchers nextMatchers);

	/**
	 * The special implementation of {@link Matchers}, which is used as last {@link RootNode} in case when ALL target nodes must match with all template nodes
	 */
	Matchers MATCH_ALL = new Matchers() {
		@Override
		public TobeMatched matchAllWith(TobeMatched targets) {
			//It matches only when there is no remaining target element
			return targets.hasTargets() ? null : targets;
		}
	};
	/**
	 * The special implementation of {@link Matchers}, which is used as last {@link RootNode} in case when SOME target nodes must match with all template nodes
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
	 * Call it to modify Pattern structure. It is actually called mainly by PatternBuilder.
	 * TODO: May be we can move this method into some internal interface?
	 * @param oldNode old {@link RootNode}
	 * @param newNode new {@link RootNode}
	 * @return a true if `oldNode` was found in this {@link RootNode} or it's children and replaced by `newNode`
	 * false if `oldNode` was not found
	 */
	boolean replaceNode(RootNode oldNode, RootNode newNode);
}
