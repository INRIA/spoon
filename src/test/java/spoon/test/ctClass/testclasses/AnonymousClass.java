package spoon.test.ctClass.testclasses;

import java.util.Comparator;

/**
 * Created by urli on 11/10/2017.
 */
public class AnonymousClass {

    final int machin = new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
            return 0;
        }
    }.compare(1,2);
}
