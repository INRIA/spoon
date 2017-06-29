package spoon.test.generics.testclasses.rxjava;

import java.util.Objects;

/**
 * Created by urli on 07/06/2017.
 */
public class Observable<T> implements Publisher<T> {

    public final void subscribe(Subscriber<? super T> s) {
        Objects.requireNonNull(s);
    }
}
