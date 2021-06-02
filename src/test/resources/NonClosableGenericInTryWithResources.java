/*
Using a non-closeable generic type in a try-with-resources previously caused a crash, see
https://github.com/INRIA/spoon/issues/3951
 */

public class NonClosableGenericInTryWithResources {
    public static void main(String[] args) {
        // we use a non-closeable generic type in a try-with-resources, causing it to become a
        // ProblemReferenceBinding in JDT with a compound name of GenericType<Integer, String>
        try (GenericType<Integer, String> gen = new GenericType<>()) {
            // referencing the gen variable to force creation of a variable access, which
            // previously caused a crash
            gen.toString();
        }

    }

    /**
     * Class that definitely does not implement Closeable.
     */
    public static class GenericType<K, V> {

    }
}