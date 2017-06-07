package spoon.test.generics.testclasses.rxjava;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by urli on 07/06/2017.
 */
public final class ToNotificationSubscriber<T> implements Subscriber<T> {
    final Consumer<? super Try<Optional<Object>>> consumer;

    public ToNotificationSubscriber(Consumer<? super Try<Optional<Object>>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onNext(T var1) {

    }

    @Override
    public void onError(Throwable var1) {

    }

    @Override
    public void onComplete() {

    }
}
