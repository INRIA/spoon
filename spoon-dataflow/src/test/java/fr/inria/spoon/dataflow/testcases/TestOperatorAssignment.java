package fr.inria.spoon.dataflow.testcases;

public class TestOperatorAssignment
{
    void testOperatorAssignment1()
    {
        int b = 1;
        b += 2 + 0 + 2;
        if (b == 1) {} //@ALWAYS_FALSE
        if (b != 4) {} //@ALWAYS_TRUE
        if (b == 5) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignment2()
    {
        byte b = 120;
        b += 123;
        if (b == -13) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignment3()
    {
        byte b = -12;
        char c = 42;
        if (c + b == 30) {} //@ALWAYS_TRUE
        if (b + c == 30) {} //@ALWAYS_TRUE
        c += b;
        if (c == 30) {} //@ALWAYS_TRUE
        if (30 == c) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignment4()
    {
        int i = -12;
        char c = 42;
        c += i;
        if (c == 30) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignment5()
    {
        char c = 42;
        c += -12;
        if (c == 30) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignment6()
    {
        byte b = -12;
        char c = 42;
        c = (char)(c + b); //equivalent to c += b
        if (c == 30) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignmentCast1()
    {
        int x = 0;
        x += (byte) 1000;
        if (x == -24) {} //@ALWAYS_TRUE
    }

    void testOperatorAssignmentOverflow1()
    {
        byte b = -128;
        if ((b -= 1) == 127) {} //@ALWAYS_TRUE

        b = -128;
        if ((b = (byte) (b - 1)) == 127) {} //@ALWAYS_TRUE

        b = -128;
        b = (byte) (b - 1);
        if (b == 127) {} //@ALWAYS_TRUE
    }
}
