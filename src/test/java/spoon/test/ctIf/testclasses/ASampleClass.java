package spoon.test.ctIf.testclasses;

/**
 * Created by urli on 20/04/2017.
 */
public class ASampleClass {
    public static void aStaticMethod(String a) {
        if(a.equals("a")) {
            a = "b";
        } else {
            a = "a";
        }

        if(a.equals("ccc")) {
            a = "ccc";
        } else {
            a = "abdce";
        }

        int w = 3;
    }
}
