package spoon.test.prettyprinter.testclasses;

public class RefactorCast {
    void example() {
        double a = 12345.0;
        String x = ((Double) a).toString();
    }
}
