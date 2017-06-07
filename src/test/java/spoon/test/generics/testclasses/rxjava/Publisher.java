package spoon.test.generics.testclasses.rxjava;

/**
 * Created by urli on 07/06/2017.
 */
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> var1);
}
