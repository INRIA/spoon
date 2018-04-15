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

import spoon.pattern.matcher.Quantifier;

/**
 * Represents the parameter of {@link Pattern}
 * defines acceptable value of parameter value during matching. For example type, filter on attribute values.
 */
public interface ParameterInfo {
	int UNLIMITED_OCCURENCES = Integer.MAX_VALUE;

	/**
	 * @return the full name of the parameter from the root of parameter container to the value represented by this {@link ParameterInfo}
	 */
	String getName();

	/**
	 * Matches `value` into `parameters` under the name/structure defined by this ParameterInfo.
	 * 1) checks that value matches with the {@link #matchCondition}
	 * 2) creates new copy of {@link ParameterValueProvider} which contains the new `value` and returns that copy
	 *
	 * @param parameters
	 * @param value
	 * @return copy of `parameters` with new value or existing `parameters` if value is already there or null if value doesn't fit into these parameters
	 */
	ParameterValueProvider addValueAs(ParameterValueProvider parameters, Object value);

	<T> void getValueAs(ResultHolder<T> result, ParameterValueProvider parameters);

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
	 * @return the strategy used to resolve conflict between two {@link Node}s
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
	boolean isMandatory(ParameterValueProvider parameters);

	/**
	 * @param parameters matching parameters
	 * @return true if the ValueResolver of this parameter should be processed again to match next target in the state defined by current `parameters`.
	 */
	boolean isTryNextMatch(ParameterValueProvider parameters);
}
