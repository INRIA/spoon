package spoon.test.prettyprinter.testclasses.difftest;

public class ConditionalComment {
    public String test() {
        boolean test = //
                true || //
                        true;

        if (test //
                 // test
                && test) {

        }

        return true ? //
                "test1" : //
                "test2";
    }
}
