package spoon.test.lambda.testclasses;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Panini {
	private static <T1, T2, R> Function<Object[], R> toFunction(BiFunction<? super T1, ? super T2, ? extends R> biFunction) {
		Objects.requireNonNull(biFunction);
		return a -> {
			if (a.length != 2) {
				throw new IllegalArgumentException("Array of size 2 expected but got " + a.length);
			}
			return ((BiFunction<Object, Object, R>)biFunction).apply(a[0], a[1]);
		};
	}
}
