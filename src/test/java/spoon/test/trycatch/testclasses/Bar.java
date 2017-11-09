package spoon.test.trycatch.testclasses;

import java.io.File;

public class Bar {
    public Statement foobar(Statement base) {
        try {
            File f = new File("/tmp/foobar");
            throw new Exception("machin");
        } catch (final Exception e) {
            return new Statement() {
                @Override public void evaluate() throws Throwable {
                    throw new RuntimeException("Invalid parameters for Timeout", e);
                }
            };
        }
    }
}