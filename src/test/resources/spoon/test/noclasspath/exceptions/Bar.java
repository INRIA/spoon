package spoon.test.noclasspath.exceptions
public class Bar {
    void testMultiCatchQualifiedExceptionsInLambda() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
            } catch (java.io.IOException | java.lang.InterruptedException e) {
            }
        });
    }
}
