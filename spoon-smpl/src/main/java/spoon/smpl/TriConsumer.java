package spoon.smpl;

import java.util.Objects;

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
