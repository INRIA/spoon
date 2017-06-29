package spoon.test.generics.testclasses.rxjava;

/**
 * Created by urli on 07/06/2017.
 */
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
