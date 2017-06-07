package spoon.test.generics.testclasses.rxjava;

/**
 * Created by urli on 07/06/2017.
 */
public interface Subscriber<T> {
    void onNext(T var1);

    void onError(Throwable var1);

    void onComplete();
}
