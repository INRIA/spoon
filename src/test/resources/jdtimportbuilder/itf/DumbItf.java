package jdtimportbuilder.itf;

public interface DumbItf {
    int uneMethod();

    default int anotherDefaultMethod() {
        return 42;
    }

    static int anotherStaticMethod() {
        return 12;
    }

    String MYSTRING = "bla";
}
