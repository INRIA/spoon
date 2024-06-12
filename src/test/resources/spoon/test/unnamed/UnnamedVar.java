package spoon.test.unnamed;

import java.util.function.Supplier;
import java.util.stream.Stream;

class UnnamedVar {
    void localVariableDeclaration() {
        int _ = 3;
    }

    void forStatement() {
        for (int _ = 0;;) {}
    }

    void tryWithResources(Supplier<AutoCloseable> resourceProvider) {
        try (java.lang.AutoCloseable _ = resourceProvider.get()) {

        }
    }

    record MyRecord(String s) {}
    void pattern(Object o) {
        switch (o) {
            case MyRecord(java.lang.String _) -> {}
            default -> {}
        }
    }

    void exception() {
        try {

        } catch (java.lang.Exception _) {

        }
    }

    void lambda() {
        Stream.empty().map(_ -> null);
    }

    void lambda2() {
        Stream.empty().map((var _) -> null);
    }
}