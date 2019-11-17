package spoon.test.position.testclasses;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MoreLambda {

    public static Predicate<Integer> m0() {
        return integer -> integer.compareTo(7) > 0;
    }

    public static Predicate<Integer> m1() {
        return in -> in.compareTo(7) > 0;
    }

    public static BiPredicate<Integer, Integer> m2() {
        return (i,j) -> i.compareTo(j) > 0;
    }

    public static BiPredicate<Integer, Integer> m3() {
        return (i ,j) -> i.compareTo(j) > 0;
    }

    public static BiPredicate<Integer, Integer> m4() {
        return (i , j) -> i.compareTo(j) > 0;
    }

    public static BiPredicate<Integer, Integer> m5() {
        return (in , jn) -> in.compareTo(jn) > 0;
    }

    public static BiPredicate<Integer, Integer> m6() {
        return (integer , jnteger) -> integer.compareTo(jnteger) > 0;
    }

    public static BiPredicate<Integer, Integer> m7() {
        return (in, jn) -> in.compareTo(jn) > 0;
    }

    public static BiPredicate<Integer, Integer> m8() {
        return ( integer ,jnteger) -> integer.compareTo(jnteger) > 0;
    }
}