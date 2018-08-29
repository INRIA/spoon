package spoon.test.position.testclasses;

import java.util.function.Predicate;

public class FooLambda {

	public static Predicate<Integer> m() {
		return i -> i.compareTo(7) > 0;
	}
}