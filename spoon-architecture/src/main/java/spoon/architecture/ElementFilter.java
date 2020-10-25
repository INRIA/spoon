package spoon.architecture;
// TODO: Naming

import java.util.function.Predicate;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ElementFilter {

	private ElementFilter() {

	}
	public static <T extends CtElement> Filter<T> ofClassObject(Class<T> elementType,
			Predicate<? super T> predicate) {
		AbstractFilter<T> typeFilter = new TypeFilter<T>(elementType);
		return new Filter<T>() {
			@Override
			public boolean matches(T element) {
				return typeFilter.matches(element) && predicate.test(element);
			}

		};
	}

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
