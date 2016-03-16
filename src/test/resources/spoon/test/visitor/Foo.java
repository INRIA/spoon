package spoon.test.visitor;

public class Foo {

    public static int factorial(final int x) {
        if (x < 0) {
            throw new IllegalArgumentException("x < 0");
        } else if (x == 0) {
            return 1;
        } else {
            return x * factorial(x-1);
        }
    }
}
