package fr.inria.sandbox;

import java.util.function.BiFunction;

public class VarInLambda {

    void m1() {
        BiFunction<Integer, Long, Integer> f1 = (var x, var y) -> x + y;
    }
}
