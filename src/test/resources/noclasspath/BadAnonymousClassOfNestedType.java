/*
Test source file to reproduce the behavior reported in https://github.com/INRIA/spoon/issues/3913
 */
public final class BadAnonymousClassOfNestedType<K, V> {
    public void test() {
        new BadAnonymousClassOfNestedType.GenericType<K, V>(this) {
        };
    }

    static class GenericType<K, V> {
    }
}
