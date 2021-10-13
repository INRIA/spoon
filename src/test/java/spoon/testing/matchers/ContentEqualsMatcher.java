package spoon.testing.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.HashSet;

/**
 * A matcher that compares the contents of two collections.
 *
 * The contents of two collections are considered as equal
 * if both contain the exact same elements. Order and frequency
 * of elements is <b>not</b> considered.
 *
 * @param <T> the type of the collection to check
 * @param <E> the element type stored in the collection
 */
public class ContentEqualsMatcher<T extends Collection<E>, E> extends TypeSafeMatcher<T> {
	private final Collection<E> elements;

	public ContentEqualsMatcher(Collection<E> elements) {
		this.elements = elements;
	}

	@Override
	protected boolean matchesSafely(T item) {
		HashSet<E> expected = new HashSet<>(elements);
		HashSet<E> actual = new HashSet<>(item);
		return expected.equals(actual);
	}

	@Override
	public void describeTo(Description description) {
		description
				.appendText("contains only but all of ")
				.appendValue(elements);
	}
}
