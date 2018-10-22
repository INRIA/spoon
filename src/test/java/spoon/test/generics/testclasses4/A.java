package spoon.test.generics.testclasses4;

public class A {
    <T extends Enum<T>> void m6(T t) {
    }
}
class B extends A {
    @Override
    <S extends Enum<S>> void m6(S s) {
    }
}

