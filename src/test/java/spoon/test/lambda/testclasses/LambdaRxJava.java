package spoon.test.lambda.testclasses;

import java.util.function.Function;

/**
 * Created by urli on 09/02/2017.
 */
public class LambdaRxJava {
    public interface NbpOperator extends Function<String, Integer> {}

    public Integer bla(NbpOperator toto) {
        return toto.apply("truc");
    }

    public void toto() {
        bla((NbpOperator) t -> { return t.length(); });
    }
}
