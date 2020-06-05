package spoon.test.field.testclasses;

/**
 * Created by urli on 10/03/2017.
 */
public class A {

    int i,j;
    int k;
    public static int l,m = 1;

    public class ClassB {
        public final static String PREFIX = BaseClass.PREFIX + ".b";
        public String getKey() {
            return BaseClass.PREFIX;
        }
    }
}
