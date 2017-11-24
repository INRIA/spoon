package spoon.test.modifiers.testclasses;

final class ConcreteClass extends AbstractClass {
    public final static String className = ConcreteClass.class.getName();

    private static int test = 42;

    private ConcreteClass() {
        test = 43;
    }

    public ConcreteClass(int i) {
        test = i;
    }

    @Override
    protected void otherMethod() {

    }

    @Override
    final int anotherOne() {
        return 0;
    }
}
