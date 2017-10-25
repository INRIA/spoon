package toto;

public interface DumbItf {
    int CONSTANT_INT = 12; // this should be static and public

    default int bla();

    void machin(); // this should be public

    // this should be static and public
    static String foo() {
        return "bla";
    }
}