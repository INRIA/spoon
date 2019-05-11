package fr.inria.spoon.dataflow.testcases;

public class TestAssignment
{
    void testAssignment1()
    {
        int a;
        int b = a = 5;
        if (a == 5) {} //@ALWAYS_TRUE
        if (a == b) {} //@ALWAYS_TRUE
        if (b == a) {} //@ALWAYS_TRUE
        if (b != a) {} //@ALWAYS_FALSE
    }

    void testAssignment2()
    {
        int a = 5;
        int b = 3;
        if (a == (b = 2)) {} //@ALWAYS_FALSE
        if (b == 2) {} //@ALWAYS_TRUE
        if (a == (b = b + 3)) {} //@ALWAYS_TRUE
        if (a == (b = b - 3)) {} //@ALWAYS_FALSE
        if (a == (b = b + 3)) {} //@ALWAYS_TRUE
        if (a == (b -= 3)) {} //@ALWAYS_FALSE
        if (a == (b += 3)) {} //@ALWAYS_TRUE
    }

    void testAssignment3(int a, int b, int x)
    {
        a = b = x = 3;
        if (a == 3){} //@ALWAYS_TRUE
        if (b == 3){} //@ALWAYS_TRUE
        if (x == 3){} //@ALWAYS_TRUE
    }

    void testAssignment4(int a, int b, int x)
    {
        a = b = x;
        if (a == b) {} //@ALWAYS_TRUE
        if (b == x) {} //@ALWAYS_TRUE
        if (x == a) {} //@ALWAYS_TRUE
    }

    void testAssignment5()
    {
        int a = 1 + 2 + 3 + 4;
        if (a == 1) {} //@ALWAYS_FALSE
        if (a == 4) {} //@ALWAYS_FALSE
        if (a == 10) {} //@ALWAYS_TRUE
    }

    void testAssignment6()
    {
        int b = 4;
        int a = 1 + 2 + 3 + b;
        if (b == 4) {} //@ALWAYS_TRUE
        if (a == 10) {} //@ALWAYS_TRUE
    }

    void testAssignment7()
    {
        int a = 5;
        int b = 3;
        if (a == (b = 2)) {} //@ALWAYS_FALSE
        if (b == 2) {} //@ALWAYS_TRUE
        if (a == (b = b + 3)) {} //@ALWAYS_TRUE
    }

    void testAssignment8()
    {
        boolean c = false;
        if (c = true) {} //@ALWAYS_TRUE
        if (c = false) {} //@ALWAYS_FALSE
    }

    void testAssignment9()
    {
        int a = 3;
        int b = 3;
        if ((a = b += (3 + a + 1)) == 10) {} //@ALWAYS_TRUE
        if (a == 10) {} //@ALWAYS_TRUE
    }

    void testAssignment10()
    {
        int x = (byte) (1000 + 1);
        if (x == -23) {} //@ALWAYS_TRUE
    }

    void testAssignment11()
    {
        int a = 5;
        boolean b = a > 3; //@ALWAYS_TRUE

        boolean c;
        c = a > 3; //@ALWAYS_TRUE

        boolean d = (a > 3 || true); //@ALWAYS_TRUE
    }
}
