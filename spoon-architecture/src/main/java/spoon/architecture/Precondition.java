/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
//TODO: more doc
/**
 * This class defines a precondition for a spoon meta model based architecture test. A precondition is used for selecting elements, that must hold a condition.
 */
public class Precondition<T extends CtElement> implements IPrecondition<T, CtModel> {

	private Filter<T> typeFilter;
	private Predicate<? super T> condition;
	private Precondition(Filter<T> typeFilter, Predicate<? super T> condition) {
		this.typeFilter = typeFilter;
		this.condition = condition;
	}
	/*
	* SafeVarargs because
	* - no reference to the array escapes the method
	* - no store operations to the array are done
	* - only read operations
	*/
	@SafeVarargs
	public static <T extends CtElement> Precondition<T> of(Filter<T> elementFilter, Predicate<? super T>...conditions) {
		Predicate<T> startValue = (value) -> true;
		for (Predicate<? super T> condition : conditions) {
			startValue = startValue.and(condition);
		}
		return new Precondition<>(elementFilter, startValue);
	}

	public static <T extends CtElement> Precondition<T> of(Filter<T> elementFilter, Iterable<Predicate<? super T>> conditions) {
		Predicate<T> startValue = value -> true;
		for (Predicate<? super T> condition : conditions) {
			startValue = startValue.and(condition);
		}
		return new Precondition<>(elementFilter, startValue);
	}

	public static <T extends CtElement> Precondition<T> of(Filter<T> elementFilter, Predicate<? super T> conditions) {
		return new Precondition<>(elementFilter, conditions);
	}

	public static <T extends CtElement> Precondition<T> of(Filter<T> elementFilter) {
		return Precondition.of(elementFilter, value -> true);
	}
	@Override
	public Collection<T> apply(CtModel t) {
		return t.getElements(typeFilter).stream().filter(condition).collect(Collectors.toSet());
	}
}
