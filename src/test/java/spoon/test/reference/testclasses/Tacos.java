package spoon.test.reference.testclasses;

import java.util.Comparator;

public class Tacos<U> {
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public final void toSortedList() {
		Comparator.naturalOrder();
	}
}
