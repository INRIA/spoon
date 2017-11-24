package spoon.test.modifiers.testclasses;

public abstract class AbstractClass {

    private int privateField;

    protected boolean protectedField;

    private static int privateStaticField = 12;

    public final String publicFinalField = "S";

    public static final int method() {
        return 42;
    }

    public static int onlyStatic() {
        return 42;
    }

    protected abstract void otherMethod();

    abstract int anotherOne();
}
