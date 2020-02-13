package spoon.test.comment.testclasses;

public class EmptyStatementComments {
    void m1() {
        if (true) // comment
            ;

        if (true) /* comment */
            ;
    }

    void m2(int value) {
        switch (value) {
            // commentInline
        }

        switch (value) {
            /* commentBlock */
        }
    }
}
