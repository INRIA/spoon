package spoon.test.fieldaccesses.testclasses;

import java.util.logging.Logger;

public class MyClass {
    private static final Logger LOG = Logger.getGlobal();

    public void foo() {
        Runnable r = () -> {
            LOG.info("bla");
        };
    }
}