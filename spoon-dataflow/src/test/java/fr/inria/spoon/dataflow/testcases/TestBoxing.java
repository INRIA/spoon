package fr.inria.spoon.dataflow.testcases;

public class TestBoxing
{
    void testBoxing1()
    {
        int a = 42;
        Integer x = a;
        if (x == null) {} //@ALWAYS_FALSE
        if (x == 42) {} //@ALWAYS_TRUE
    }

    void testBoxing2()
    {
        Integer x = 42;
        if (x == null) {} //@ALWAYS_FALSE
        if (x == 42) {} //@ALWAYS_TRUE
    }

    void testBoxing3()
    {
        Integer a = 1234;
        Integer b = 1234;
        if (a == b) {} //@ALWAYS_FALSE
    }

    void testBoxing4(int x)
    {
        Integer a = x;
        Integer b = x;
        if (a == null) {} //@ALWAYS_FALSE
        if (b != null) {} //@ALWAYS_TRUE
        if (a == b) {} //@ALWAYS_FALSE
    }

    void testBoxing5()
    {
        Integer a = new Integer(42);
        int b = a;
        if (b == 42) {} //@ALWAYS_TRUE
    }

    void testBoxing6()
    {
        Byte b = 42;
        if (b == 42) {} //@ALWAYS_TRUE

        Short s = 42;
        if (s == 42) {} //@ALWAYS_TRUE

        Integer i = 42;
        if (i == 42L) {} //@ALWAYS_TRUE

        Long l = 42L;
        if (i == 42) {} //@ALWAYS_TRUE
    }

    void testBoxingBinOp1()
    {
        Integer x = 0;
        x += 42;
        if (x == null) {} //@ALWAYS_FALSE
        if (x == 42) {} //@ALWAYS_TRUE
    }

    void testBooleanUnboxing()
    {
        Boolean b = false;
        if (b) {} //@ALWAYS_FALSE
    }

    void testShortCircuitUnboxing(boolean x)
    {
        Boolean b = true;
        if (x && b) {} //@ALWAYS_TRUE
        if (b && x) {} //@ALWAYS_TRUE
    }

    void testIntegerUnboxing()
    {
        Integer a = new Integer(42);
        a = a * 2;
        if (a == 2 * 42) {} //@ALWAYS_TRUE
    }
}
