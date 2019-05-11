package fr.inria.spoon.dataflow.testcases;

public class TestUnreachable
{
    void testUnreachable1(boolean c)
    {
        if (c)
        {
            if (5 == 5) {} //@ALWAYS_TRUE
            if (5 != 5) {} //@ALWAYS_FALSE
            return;
        }

        if (c) //@ALWAYS_FALSE
        {
            if (5 == 5) {} // ok (it's inside unreachable condition)
            if (5 != 5) {} // ok (it's inside unreachable condition)
        }
    }

    void testUnreachable2(boolean c)
    {
        if (c)
        {
            if (5 == 5) {} //@ALWAYS_TRUE
            if (5 != 5) {} //@ALWAYS_FALSE
            throw new RuntimeException();
        }

        if (c) //@ALWAYS_FALSE
        {
            if (5 == 5) {} // ok (it's inside unreachable condition)
            if (5 != 5) {} // ok (it's inside unreachable condition)
        }
    }
}
