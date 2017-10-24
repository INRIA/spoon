package spoon.test.jdtimportbuilder.testclasses.itf;

public interface DumbItf {
    int uneMethod();

    default int defaultMethod() {
        return 42;
    }

    static int staticMethod() {
        return 12;
    }

    String STATIC_STRING = "bla";
}
