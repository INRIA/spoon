package spoon.test.trycatch.testclasses;

public class CatcherWithQualifiedAndUnqualifiedTypeRefs {
    public static void main(String[] args) {
        try {
            throw new InterruptedException();
        } catch (InterruptedException | java.lang.RuntimeException e) {
            // pass
        }
    }
}
