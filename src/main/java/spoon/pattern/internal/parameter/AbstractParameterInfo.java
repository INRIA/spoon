/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.ValueConvertor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.util.ImmutableMap;

public abstract class AbstractParameterInfo implements ParameterInfo {

	/**
	 * is used as return value when value cannot be added
	 */
	protected static final Object NO_MERGE = new Object();

	private final AbstractParameterInfo containerItemAccessor;

	private ContainerKind containerKind = null;
	private Boolean repeatable = null;
	private int minOccurrences = 0;
	private int maxOccurrences = UNLIMITED_OCCURRENCES;
	private Quantifier matchingStrategy = Quantifier.GREEDY;
	private ValueConvertor valueConvertor;

	private Predicate<Object> matchCondition;
	private Class<?> parameterValueType;

	protected AbstractParameterInfo(ParameterInfo containerItemAccessor) {
		this.containerItemAccessor = (AbstractParameterInfo) containerItemAccessor;
	}

	protected String getContainerName() {
		if (containerItemAccessor != null) {
			return containerItemAccessor.getPlainName();
		}
		return "";
	}

	@Override
	public final String getName() {
		AbstractParameterInfo cca = getContainerKindAccessor(getContainerKind(null, null));
		if (cca != null) {
			return cca.getWrappedName(getPlainName());
		}
		return getPlainName();
	}

	protected abstract String getPlainName();

	protected abstract String getWrappedName(String containerName);

	@Override
	public ImmutableMap addValueAs(ImmutableMap parameters, Object value) {
		Class<?> requiredType = getParameterValueType();
		if (requiredType != null && value != null && requiredType.isInstance(value) == false) {
			return null;
		}
		if (matches(value) == false) {
			return null;
		}
		Object newContainer = addValueToContainer(parameters, existingValue -> {
			return merge(existingValue, value);
		});
		if (newContainer == NO_MERGE) {
			return null;
		}
		return (ImmutableMap) newContainer;
	}

	protected Object addValueToContainer(Object container, Function<Object, Object> merger) {
		if (containerItemAccessor != null) {
			return containerItemAccessor.addValueToContainer(container, existingValue -> {
				return addValueAs(existingValue, merger);
			});
		}
		return addValueAs(container, merger);
	}

	protected Object merge(Object existingValue, Object newValue) {
		ContainerKind cc = getContainerKind(existingValue, newValue);
		AbstractParameterInfo cca = getContainerKindAccessor(cc);
		if (cca == null) {
			return mergeSingle(existingValue, newValue);
		}
		return cca.addValueAs(existingValue,
				existingListItemValue -> mergeSingle(existingListItemValue, newValue));
	}

	protected AbstractParameterInfo getContainerKindAccessor(ContainerKind containerKind) {
		switch (containerKind) {
		case SINGLE:
			return null;
		case LIST:
			return new ListParameterInfo(this);
		case SET:
			return new SetParameterInfo(this);
		case MAP:
			return new MapParameterInfo(this);
		}
		throw new SpoonException("Unexpected ContainerKind " + containerKind);
	}

	protected Object mergeSingle(Object existingValue, Object newValue) {
		if (newValue == null && getMinOccurrences() > 0) {
			//the newValue is not optional. Null doesn't matches
			return NO_MERGE;
		}
		if (existingValue != null) {
			if (existingValue.equals(newValue)) {
				//the value is already stored there. Keep existing value
				return existingValue;
			}
			if (newValue != null && existingValue.getClass().equals(newValue.getClass())) {
				if (newValue instanceof CtTypeReference) {
					if (((CtTypeReference<?>) newValue).getTypeErasure().equals(((CtTypeReference<?>) existingValue).getTypeErasure())) {
						//accept type references with different erasure
						return existingValue;
					}
				}
			}
			// another value would be inserted. TemplateMatcher does not support
			// matching of different values for the same template parameter
			Launcher.LOGGER.debug("incongruent match on parameter " + getName() + " with value " + newValue);
			return NO_MERGE;
		}
		return newValue;
	}

	/**
	 * takes existing item value from the `container`,
	 * sends it as parameter into `merger` and get's new to be stored value
	 * stores that value into new `container` and returns it
	 * @param container a container of values
	 * @param merger a code which merges existing value from container with new value and returns merged value, which has to be stored in the container instead
	 * @return copy of the container with merged value
	 */
	protected abstract Object addValueAs(Object container, Function<Object, Object> merger);

	protected <T> T castTo(Object o, Class<T> type) {
		if (o == null) {
			return getEmptyContainer();
		}
		if (type.isInstance(o)) {
			return (T) o;
		}
		throw new SpoonException("Cannot access parameter container of type " + o.getClass() + ". It expects " + type);
	}

	protected abstract <T> T getEmptyContainer();

	/**
	 * @param requiredType a required type of the value which matches as value of this parameter
	 * @param matchCondition a {@link Predicate} which selects matching values
	 * @return
	 */
	public <T> AbstractParameterInfo setMatchCondition(Class<T> requiredType, Predicate<T> matchCondition) {
		this.parameterValueType = requiredType;
		this.matchCondition = (Predicate) matchCondition;
		return this;
	}

	/**
	 * Checks whether `value` matches with required type and match condition.
	 * @param value
	 * @return
	 */
	protected boolean matches(Object value) {
		if (parameterValueType != null && (value == null || parameterValueType.isAssignableFrom(value.getClass()) == false)) {
			return false;
		}
		if (matchCondition == null) {
			//there is no matching condition. Everything matches
			return true;
		}
		//there is a matching condition. It must match
		return matchCondition.test(value);
	}
	/**
	 * @return a type of parameter value - if known
	 *
	 * Note: Pattern builder needs to know the value type to be able to select substitute node.
	 * For example patter:
	 *   return _expression_.S();
	 * either replaces only `_expression_.S()` if the parameter value is an expression
	 * or replaces `return _expression_.S()` if the parameter value is a CtBlock
	 */
	@Override
	public Class<?> getParameterValueType() {
		return parameterValueType;
	}

	/**
	 * @param parameterValueType a type of the value which is acceptable by this parameter
	 * @return this to support fluent API
	 */
	public AbstractParameterInfo setParameterValueType(Class<?> parameterValueType) {
		this.parameterValueType = parameterValueType;
		return this;
	}
	/**
	 * @return true if the value container has to be a List, otherwise the container will be a single value
	 */
	@Override
	public boolean isMultiple() {
		return getContainerKind(null, null) != ContainerKind.SINGLE;
	}

	/**
	 * @param repeatable if this matcher can be applied more than once in the same container of targets
	 * Note: even if false, it may be applied again to another container and to match EQUAL value.
	 * @return this to support fluent API
	 */
	public AbstractParameterInfo setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
		return this;
	}


	public int getMinOccurrences() {
		return minOccurrences;
	}

	public AbstractParameterInfo setMinOccurrences(int minOccurrences) {
		this.minOccurrences = minOccurrences;
		return this;
	}

	/**
	 * @return maximum number of values in this parameter.
	 * Note: if {@link #isMultiple()}==false, then it never returns value &gt; 1
	 */
	public int getMaxOccurrences() {
		return isMultiple() ? maxOccurrences : Math.min(maxOccurrences, 1);
	}

	public void setMaxOccurrences(int maxOccurrences) {
		this.maxOccurrences = maxOccurrences;
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return matchingStrategy;
	}

	public void setMatchingStrategy(Quantifier matchingStrategy) {
		this.matchingStrategy = matchingStrategy;
	}

	/**
	 * @return the {@link ValueConvertor} used by reading and writing into parameter values defined by this {@link ParameterInfo}
	 */
	public ValueConvertor getValueConvertor() {
		if (valueConvertor != null) {
			return valueConvertor;
		}
		if (containerItemAccessor != null) {
			return containerItemAccessor.getValueConvertor();
		}
		throw new SpoonException("ValueConvertor is not defined.");
	}

	/**
	 * @param valueConvertor the {@link ValueConvertor} used by reading and writing into parameter values defined by this {@link ParameterInfo}
	 */
	public AbstractParameterInfo setValueConvertor(ValueConvertor valueConvertor) {
		if (valueConvertor == null) {
			throw new SpoonException("valueConvertor must not be null");
		}
		this.valueConvertor = valueConvertor;
		return this;
	}

	/**
	 * @return true if this matcher can be applied more than once in the same container of targets
	 * Note: even if false, it may be applied again to another container and to match EQUAL value
	 */
	@Override
	public boolean isRepeatable() {
		if (repeatable != null) {
			return repeatable;
		}
		return isMultiple();
	}

	/**
	 * @param parameters matching parameters
	 * @return true if the ValueResolver of this parameter MUST match with next target in the state defined by current `parameters`.
	 * false if match is optional
	 */
	@Override
	public boolean isMandatory(ImmutableMap parameters) {
		int nrOfValues = getNumberOfValues(parameters);
		//current number of values is smaller than minimum number of values. Value is mandatory
		return nrOfValues < getMinOccurrences();
	}

	/**
	 * @param parameters matching parameters
	 * @return true if the ValueResolver of this parameter should be processed again to match next target in the state defined by current `parameters`.
	 */
	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		int nrOfValues = getNumberOfValues(parameters);
		if (getContainerKind(parameters) == ContainerKind.SINGLE) {
			/*
			 * the single value parameters should always try next match.
			 * If the matching value is equal to existing value, then second match is accepted and there stays single value
			 */
			return true;
		}
		//current number of values is smaller than maximum number of values. Can try next match
		return nrOfValues < getMaxOccurrences();
	}

	/**
	 * @param parameters
	 * @return 0 if there is no value. 1 if there is single value or null. Number of values in collection if there is a collection
	 */
	private int getNumberOfValues(ImmutableMap parameters) {
		if (parameters.hasValue(getName()) == false) {
			return 0;
		}
		Object value = parameters.getValue(getName());
		if (value instanceof Collection) {
			return ((Collection) value).size();
		}
		return 1;
	}
	public ContainerKind getContainerKind() {
		return containerKind;
	}

	public AbstractParameterInfo setContainerKind(ContainerKind containerKind) {
		this.containerKind = containerKind;
		return this;
	}
	protected ContainerKind getContainerKind(ImmutableMap params) {
		return getContainerKind(params.getValue(getName()), null);
	}
	protected ContainerKind getContainerKind(Object existingValue, Object value) {
		if (containerKind != null) {
			return containerKind;
		}
		if (existingValue instanceof List) {
			return ContainerKind.LIST;
		}
		if (existingValue instanceof Set) {
			return ContainerKind.SET;
		}
		if (existingValue instanceof Map) {
			return ContainerKind.MAP;
		}
		if (existingValue instanceof ImmutableMap) {
			return ContainerKind.MAP;
		}
		if (existingValue != null) {
			return ContainerKind.SINGLE;
		}

		if (value instanceof List) {
			return ContainerKind.LIST;
		}
		if (value instanceof Set) {
			return ContainerKind.SET;
		}
		if (value instanceof Map.Entry<?, ?>) {
			return ContainerKind.MAP;
		}
		if (value instanceof Map) {
			return ContainerKind.MAP;
		}
		if (value instanceof ImmutableMap) {
			return ContainerKind.MAP;
		}
		return ContainerKind.SINGLE;
	}

	@Override
	public <T> void getValueAs(Factory factory, ResultHolder<T> result, ImmutableMap parameters) {
		//get raw parameter value
		Object rawValue = getValue(parameters);
		if (isMultiple() && rawValue instanceof CtBlock<?>)  {
			/*
			 * The CtBlock of this parameter is just implicit container of list of statements.
			 * Convert it to list here, so further code see list and not the single CtBlock element
			 */
			rawValue = ((CtBlock<?>) rawValue).getStatements();
		}
		convertValue(factory, result, rawValue);
	}

	protected Object getValue(ImmutableMap parameters) {
		if (containerItemAccessor != null) {
			return containerItemAccessor.getValue(parameters);
		}
		return parameters;
	}

	protected <T> void convertValue(Factory factory, ResultHolder<T> result, Object rawValue) {
		//convert raw parameter value to expected type
		if (result.isMultiple()) {
			forEachItem(rawValue, singleValue -> {
				T convertedValue = convertSingleValue(factory, singleValue, result.getRequiredClass());
				if (convertedValue != null) {
					result.addResult(convertedValue);
				}
			});
		} else {
			//single value converts arrays in rawValues into single value
			result.addResult(convertSingleValue(factory, rawValue, result.getRequiredClass()));
		}
	}

	protected <T> T convertSingleValue(Factory factory, Object value, Class<T> type) {
		ValueConvertor valueConvertor = getValueConvertor();
		return valueConvertor.getValueAs(factory, getName(), value, type);
	}

	/**
	 * calls consumer.accept(Object) once for each item of the `multipleValues` collection or array.
	 * If it is not a collection or array then it calls consumer.accept(Object) once with `multipleValues`
	 * If `multipleValues` is null then consumer.accept(Object) is not called
	 * @param multipleValues to be iterated potential collection of items
	 * @param consumer the receiver of items
	 */
	@SuppressWarnings("unchecked")
	static void forEachItem(Object multipleValues, Consumer<Object> consumer) {
		if (multipleValues instanceof CtStatementList) {
			//CtStatementList extends Iterable, but we want to handle it as one node.
			consumer.accept(multipleValues);
			return;
		}
		if (multipleValues instanceof Iterable) {
			for (Object item : (Iterable<Object>) multipleValues) {
				consumer.accept(item);
			}
			return;
		}
		if (multipleValues instanceof Object[]) {
			for (Object item : (Object[]) multipleValues) {
				consumer.accept(item);
			}
			return;
		}
		consumer.accept(multipleValues);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if (getParameterValueType() != null) {
			sb.append(" : ");
			sb.append(getParameterValueType().getName());
		}
		return sb.toString();
	}
}
