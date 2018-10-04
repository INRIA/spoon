package spoon.test.fieldaccesses.testclasses;

public class BUG20160112 {
    BUG20160112 a;
    int us;

    public void test() {
        int z = 0;
        z += a.us;
        return;
    }
}