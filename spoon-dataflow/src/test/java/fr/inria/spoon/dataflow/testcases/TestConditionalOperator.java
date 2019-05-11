package fr.inria.spoon.dataflow.testcases;

public class TestConditionalOperator
{
    void testConditionalOperator1()
    {
        int x = 3;
        int y = (x == 3) ? 5 : 2; //@ALWAYS_TRUE
        if (y == 5) {} //@ALWAYS_TRUE
    }

    void testConditionalOperator2()
    {
        int x = 3;
        int z = (x == 3) ? ++x : --x; //@ALWAYS_TRUE
        if (z == 4) {} //@ALWAYS_TRUE
        if (x == 4) {} //@ALWAYS_TRUE
    }

    void testConditionalOperator3(int x)
    {
        boolean z = x > 3 ? x == 1 : x == 0; //@ALWAYS_FALSE
    }

    void testConditionalOperator4(boolean cond)
    {
        Object x = cond ? new Integer(42) : new Double(14.0);
        if (x == null) {} //@ALWAYS_FALSE
        if ((Integer) x == 42) {}
    }
}
