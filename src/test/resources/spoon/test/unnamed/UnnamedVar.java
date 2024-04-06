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
        try (AutoCloseable _ = resourceProvider.get()) {

        }
    }

    record MyRecord(String s) {}
    void pattern(Object o) {
        switch (o) {
            case MyRecord(String _) -> {}
        }
    }

    void exception() {
        try {

        } catch (Exception _) {

        }
    }

    void lambda() {
        Stream.empty().map(_ -> null);
    }
}