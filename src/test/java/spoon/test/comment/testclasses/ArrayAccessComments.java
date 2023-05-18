package spoon.test.comment.testclasses;

public class ArrayAccessComments {

    public void bar(int[] foo)
    {
        foo// comment 1
                [1] = 0;
        int bar = foo // comment 2
                [0];
    }
}
