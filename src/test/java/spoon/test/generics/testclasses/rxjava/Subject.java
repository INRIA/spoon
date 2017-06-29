package spoon.test.generics.testclasses.rxjava;

/**
 * Created by urli on 07/06/2017.
 */
public abstract class Subject<T, R> extends Observable<R> implements Processor<T, R> {
}
