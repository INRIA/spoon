package spoon.test.reference;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;


public class A {

    int field;

    public void b(int param) {
        IntUnaryOperator f1 = x -> x;
        IntBinaryOperator f2 = (a, b) -> a + b;
        try {
            System.out.println(f1.applyAsInt(f2.applyAsInt(field, param)));
        } catch (RuntimeException e) {
            throw e;
        }
    }
}