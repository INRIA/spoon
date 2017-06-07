package spoon.test.generics.testclasses.rxjava;

/**
 * Created by urli on 07/06/2017.
 */
public final class BehaviorSubject<T> extends Subject<T, T> {
    public static <T> BehaviorSubject<T> create() {
        return new BehaviorSubject<>();
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
