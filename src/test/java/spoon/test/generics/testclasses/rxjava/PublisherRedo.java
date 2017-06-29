package spoon.test.generics.testclasses.rxjava;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Created by urli on 07/06/2017.
 */
public final class PublisherRedo<T> implements Publisher<T> {
    final Function<? super Observable<Try<Optional<Object>>>, ? extends Publisher<?>> manager;

    public PublisherRedo(Function<? super Observable<Try<Optional<Object>>>, ? extends Publisher<?>> manager) {
        this.manager = manager;
    }

    public void subscribe(Subscriber<? super T> s) {

        BehaviorSubject<Try<Optional<Object>>> subject = BehaviorSubject.create();
        RedoSubscriber<T> parent = new RedoSubscriber<>();
        Publisher<?> action = manager.apply(subject);

        action.subscribe(new ToNotificationSubscriber<>(parent::handle));
    }

    private void subscribe(ToNotificationSubscriber truc) {

    }

    static final class RedoSubscriber<T> extends AtomicBoolean implements Subscriber<T> {
        void handle(Try<Optional<Object>> notification) {

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
}
