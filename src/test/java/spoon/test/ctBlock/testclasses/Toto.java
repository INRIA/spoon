package spoon.test.ctBlock.testclasses;

/**
 * Created by urli on 15/03/2017.
 */
public class Toto {
    public void foo() {
        int i = 1;
        i++;
        if (i > 0) {
            java.lang.System.out.println("test");
        }
        i++;
    }

    public void bar() {
        switch ("truc") {
            case "t":
                int i = 0;
                i++;
                System.out.println(i);

            case "u":
                i = 3;
                i = 4;
        }
    }
}
