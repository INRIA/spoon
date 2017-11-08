package spoon.test.position.testclasses;

public class ImplicitBlock {
    boolean cond = true;

    public int method() {
        if (cond) return 10;
        else return 20;
    }
}
