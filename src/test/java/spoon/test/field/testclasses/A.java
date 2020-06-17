package spoon.test.field.testclasses;

/**
 * Created by urli on 10/03/2017.
 */
public class A {

    int alone1;
    int alone2 = 1;
    int alone3 = 1; // normal case
    int i,j,k;
    public static int l,m = 1,   n=2,   o=3;

    public class ClassB {
        public final static String PREFIX = BaseClass.PREFIX + ".b";
        public String getKey() {
            return BaseClass.PREFIX;
        }
    }
}
