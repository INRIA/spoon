package spoon.test.comment.testclasses;

public class CatchComments {
    public static void exampleMethod() {
        try {
            Object o = new Object();
        } catch (Exception e) // first comment
        // second comment
        {
            // third comment
            int x = 42;
        }
    }
}
