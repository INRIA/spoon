package spoon.test.generics.testclasses.overriding;

public class A {
    <T extends Enum<T>> void m6(T t) {
    }
}
class B extends A {
    @Override
    <S extends Enum<S>> void m6(S s) {
    }
}

