package spoon.test.field.testclasses;

/**
 * Created by urli on 10/03/2017.
 */
public class A {
    public class ClassB {
        public static final String PREFIX = BaseClass.PREFIX + ".b";
        public String getKey() {
            return BaseClass.PREFIX;
        }
    }
}
