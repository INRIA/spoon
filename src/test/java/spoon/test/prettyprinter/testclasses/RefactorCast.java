package spoon.test.prettyprinter.testclasses;

public class RefactorCast {
    void example() {
        var a = 12345.0;
        var x = ((Double) a).toString();
    }
}
