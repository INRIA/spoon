package fr.inria.spoon.dataflow.testcases;

public class TestInteger
{
    void testInteger1()
    {
        Integer i1 = new Integer(42);
        if (i1 == 42) {} //@ALWAYS_TRUE
        Integer i2 = new Integer(42);
        if (i1 == i2) {} //@ALWAYS_FALSE
    }

    void testInteger2()
    {
        char c = 42;
        Integer i1 = new Integer(c);
        if (i1 == 42) {} //@ALWAYS_TRUE
    }

    void testInteger3()
    {
        Integer a = new Integer(42);
        Integer b = null;
        b = a;
        if (b == 42) {} //@ALWAYS_TRUE
        if (b == null) {} //@ALWAYS_FALSE
        if (a == 42) {} //@ALWAYS_TRUE
        if (a == null) {} //@ALWAYS_FALSE
    }

    void testInteger4(Integer c)
    {
        Integer a = new Integer(42);
        Integer b = a;
        if (b == 42) {} //@ALWAYS_TRUE
        if (b == a) {} //@ALWAYS_TRUE
        a = new Integer(32);
        if (b == 42) {} //@ALWAYS_TRUE
        if (b == a) {} //@ALWAYS_FALSE
        a = c;
        if (b == 42) {} //@ALWAYS_TRUE
        if (b == a) {} //ok
    }

    void testInteger5()
    {
        Integer a = new Integer(200);
        Integer b = new Integer(200);
        if (a == b) {} //@ALWAYS_FALSE
        if (a != b) {} //@ALWAYS_TRUE
        if (a > b) {} //@ALWAYS_FALSE
        if (a >= b) {} //@ALWAYS_TRUE
    }

    void testInteger6(boolean cond)
    {
        Integer a = new Integer(42);
        Integer b = new Integer(32);
        if (a == b) {} //@ALWAYS_FALSE
        if (a > b) {} //@ALWAYS_TRUE
        if (a < b) {} //@ALWAYS_FALSE
        if (cond)
        {
            a = b;
        }
        if (a == b) {} //ok
        if (a > b) {} //ok
        if (a < b) {} //@ALWAYS_FALSE
        if (a <= b) {} //ok
    }
}
