package toto;

public interface DumbItf {
    public static int ANOTHER_INT = 42; // this should be explicitely static and public, and implicitely final

    int CONSTANT_INT = 12; // this should be static public and final

    // this should be only public
    default int bla() {
        return 42;
    }

    void machin(); // this should be public

    // this should be static and public
    static String foo() {
        return "bla";
    }

    // this should be public too
    public void anotherOne();
}