package spoon.test.imports.testclasses2;

public final class Interners {
    private static class WeakInterner<E> {
        private enum Dummy {
            VALUE;}

        java.util.List<Interners.WeakInterner.Dummy> list;
    }

    private Interners() {
    }
}
