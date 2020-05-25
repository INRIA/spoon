/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import spoon.pattern.Pattern;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.node.RootNode;
import spoon.reflect.factory.Factory;
import spoon.support.util.ImmutableMap;

/**
 * Represents the parameter of {@link Pattern}
 * defines acceptable value of parameter value during matching. For example type, filter on attribute values.
 */
public interface ParameterInfo {
	int UNLIMITED_OCCURRENCES = Integer.MAX_VALUE;

	/**
	 * @return the full name of the parameter from the root of parameter container to the value represented by this {@link ParameterInfo}
	 */
	String getName();

	/**
	 * Matches `value` into `parameters` under the name/structure defined by this ParameterInfo.
	 * 1) checks that value matches with optional internal rules of this {@link ParameterInfo}
	 * 2) creates new copy of {@link ImmutableMap} which contains the new `value` and returns that copy
	 *
	 * @param parameters the existing parameters
	 * @param value the new, to be stored value
	 * @return copy of `parameters` with new value or existing `parameters` if value is already there or null if value doesn't fit into these parameters
	 */
	ImmutableMap addValueAs(ImmutableMap parameters, Object value);

	/**
	 * Takes the value of parameter identified by this {@link ParameterInfo} from the `parameters`
	 * and adds that 0, 1 or more values into result (depending on type of result)
	 * @param factory the factory used to create new entities if conversion of value is needed before it can be added into `result`
	 * @param result the receiver of the result value. It defined required type of returned value and multiplicity of returned value
	 * @param parameters here are stored all the parameter values
	 */
	<T> void getValueAs(Factory factory, ResultHolder<T> result, ImmutableMap parameters);

	/**
	 * @return true if the value container has to be a List, otherwise the container will be a single value
	 */
	boolean isMultiple();

	/**
	 * @return a type of parameter value - if known
	 *
	 * Note: Pattern builder needs to know the value type to be able to select substitute node.
	 * For example patter:
	 *   return _expression_.S();
	 * either replaces only `_expression_.S()` if the parameter value is an expression
	 * or replaces `return _expression_.S()` if the parameter value is a CtBlock
	 */
	Class<?> getParameterValueType();

	/**
	 * @return the strategy used to resolve conflict between two {@link RootNode}s
	 */
	Quantifier getMatchingStrategy();

	/**
	 * @return true if this matcher can be applied more then once in the same container of targets
	 * Note: even if false, it may be applied again to another container and to match EQUAL value
	 */
	boolean isRepeatable();

	/**
	 * @param parameters matching parameters
	 * @return true if the ValueResolver of this parameter MUST match with next target in the state defined by current `parameters`.
	 * false if match is optional
	 */
	boolean isMandatory(ImmutableMap parameters);

	/**
	 * @param parameters matching parameters
	 * @return true if the ValueResolver of this parameter should be processed again to match next target in the state defined by current `parameters`.
	 */
	boolean isTryNextMatch(ImmutableMap parameters);
}
