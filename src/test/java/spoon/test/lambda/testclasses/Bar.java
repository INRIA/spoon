package spoon.test.lambda.testclasses;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Bar<T> {
	public static <T> Bar<T> m(CompletableFuture<? extends T> future) {
		Objects.requireNonNull(future);
		return create(s -> {
			future.whenComplete((v, e) -> {
			});
		});
	}

	public static <T> Bar<T> create(SingleOnSubscribe<T> onSubscribe) {
		return new Bar<>();
	}

	public interface SingleOnSubscribe<T> extends Consumer<SingleSubscriber<? super T>> {
	}

	public interface SingleSubscriber<T> {
		void onSubscribe(Disposable d);
		void onSuccess(T value);
		void onError(Throwable e);
	}

	public interface Disposable {
		void dispose();
	}
}
