/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * This defines an util class for creating filter combining type filtering and a predicate.
 * Either by providing a class object or a filter object. If you need multiple predicates connect them by {@code Predicate#and(Predicate)}
 */
public class ElementFilters {

	private ElementFilters() {

	}
/**
 * Creates a filter converting all matching elements to the given class. The elements are first converted and then checked by the predicate.
 * @param <T>  element type
 * @param elementType  class object for element type.
 * @param predicate  filter condition
 * @return  a type filter converting all elements holding a predicate.
 */
	public static <T extends CtElement> Filter<T> ofClassObject(Class<T> elementType,
			Predicate<? super T> predicate) {
		Filter<T> typeFilter = new TypeFilter<T>(elementType);
		return ofTypeFilter(typeFilter, predicate);
	}

/**
 * Creates a filter converting all matching elements.
 * @param <T>  element type
 * @param typeFilter  a filter for ast elements
 * @param predicate  filter condition
 * @return  a type filter matching all elements holding a predicate.
 */
	public static <T extends CtElement> Filter<T> ofTypeFilter(Filter<T> typeFilter,
			Predicate<? super T> predicate) {
		return new Filter<T>() {
			@Override
			public boolean matches(T element) {
				return typeFilter.matches(element) && predicate.test(element);
			}

		};
	}
}
