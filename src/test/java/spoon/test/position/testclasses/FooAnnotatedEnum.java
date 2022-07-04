package spoon.test.position.testclasses;

public enum FooAnnotatedEnum {
    FOO,
    // A comment before the annotation ...
    @Deprecated
    /**
     * And another one after
     */
    BAR;
}
