package spoon.test.ctType.testclasses;

public class MultiInterfaceImplementation implements
        java.util.function.Supplier<Integer>,
        java.util.function.Consumer<Integer>,
        java.lang.Comparable<java.lang.Integer> {
    public int compareTo(Integer i) {
        return 0;
    }

    public Integer get() {
        return 1;
    }

    public void accept(Integer i) {
        // thanks!
    }
}


