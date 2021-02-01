package spoon.smpl;

import java.util.Objects;

/**
 * TriConsumer is a functional Consumer interface of three parameters.
 *
 * @param <T> Type of first parameter
 * @param <U> Type of second parameter
 * @param <V> Type of third parameter
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	void accept(T var1, U var2, V var3);

	default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
		Objects.requireNonNull(after);
		return (x, y, z) -> {
			this.accept(x, y, z);
			after.accept(x, y, z);
		};
	}
}
