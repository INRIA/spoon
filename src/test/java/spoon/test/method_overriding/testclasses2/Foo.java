package spoon.test.method_overriding.testclasses2;
public class Foo {

    public void useLambda() {
        ObjectInterface objectInterface = () -> { /* do nothing */ };
        objectInterface.doSomething();
    }
}