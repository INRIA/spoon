package spoon.test.comment.testclasses;

import java.util.function.BiFunction;

public class LambdaComments {

    void m1() {
        BiFunction<Integer, Integer, Integer> lambda1 = (a, b) -> /* comment */
            a + b;

        BiFunction<Integer, Integer, Integer> lambda2 = (a, b) -> // comment
            a + b;

        BiFunction<Integer, Integer, Integer> lambda3 = (a, b) ->
            // comment
            a + b;

        BiFunction<Integer, Integer, Integer> lambda4 = /* comment */ (a, b) ->
            a + b;

        BiFunction<Integer, Integer, Integer> lambda5 = (a, b) -> //
                a + b;
    }

    void m2() {
        BiFunction<Integer, Integer, Integer> lambda6 = (a, b) -> { /* comment */
            return a + b;
        };

        BiFunction<Integer, Integer, Integer> lambda7 = (a, b) -> /* comment */ {
            return a + b;
        };

        BiFunction<Integer, Integer, Integer> lambda8 = (a, b) -> {
            /* comment */
            return a + b;
        };
    }

    void m3() {
        BiFunction<Integer, Integer, Integer> lambda9 = (/* param1 */ a, /* param2 */ b) -> a + b;

        BiFunction<Integer, Integer, Integer> lambda10 = (/*param1*/ Integer a, /* param2 */ Integer b) -> a + b;

        BiFunction<Integer, Integer, Integer> lambda11 = (a /* param1 */, b /* param2 */) -> a + b;

        BiFunction<Integer, Integer, Integer> lambda12 = (a, b /* param2 */) -> a + b;

        BiFunction<Integer, Integer, Integer> lambda13 = (/* param1 */ a /* param1 */, /* param2 */ b /* param2 */) -> a + b;
    }
}
