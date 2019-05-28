package spoon.test.generics.testclasses5;

import java.util.List;

public class A <T>  {
    void m1(List<Integer> list1, List<T> list2, List<List<Integer>> list3, List list4) {
    }

    <U> void m2() {
    }

    <U extends T> void m3() {
    }
}
