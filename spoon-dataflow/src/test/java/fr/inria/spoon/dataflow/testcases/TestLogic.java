package fr.inria.spoon.dataflow.testcases;

public class TestLogic
{
    void testLogic1(int i)
    {
        if (i < 10)
        {
            if (i == 6) {} //ok
        }
    }

    void testLogic2(int a, int b, int c)
    {
        if (a > b)
        {
            if (b > c)
            {
                if (a > c) {} //@ALWAYS_TRUE
            }
        }
    }

    void testLogic3(boolean a, boolean b)
    {
        if ((!a && !b) == !(a || b)) {} //@ALWAYS_TRUE
    }

    void testLogic4()
    {
        int x = 1;
        int y = 4;
        boolean b = x > 5 && y > 2; //@ALWAYS_FALSE
    }

    void testLogic5(boolean cond)
    {
        int x;
        int y;
        if (cond)
        {
            x = 3;
            y = 3;
        }
        else
        {
            y = 3;
            x = 4;
        }
        if (x != 3 && x != 4) {} //@ALWAYS_FALSE
        if (y != 3) {} //@ALWAYS_FALSE
    }

    void testLogic6(int a, int b)
    {
        if (a == 1)
        {
            int c = a * b;
            if (c != b) {} //@ALWAYS_FALSE
        }
    }

    void testLogic7(int a, int b)
    {
        if (a + b < 5)
        {
            if (a > 2 && b > 3) {} //ok (a = 1073741826, b = 1073741826)
            if (a < 0 && a > -2 && b > 4) {} //ok
            if (a < 0 && a > -2 && b > 5) {} //@ALWAYS_FALSE
            if (a < 1 && b < 1) {} //ok
        }
    }

    void testLogic8(int x)
    {
        // quadratic equation
        if (x * x + x - 6 == 0)
        {
            if (x == 2 || x == -3) {} //@ALWAYS_TRUE
            if (x != 2 && x != -3) {} //@ALWAYS_FALSE
        }
    }

    void testLogic9(int x)
    {
        // cubic equation
        if (3 * x * x * x * x + 6 * x * x * x - 9 * x * x == 0)
        {
            if (x == 0 || x == 1 || x == -3) {} //ok (consider overflows)
            if (x != 0 && x != 1 && x != -3) {} //ok (consider overflows)
            if (x == 5) {} //@ALWAYS_FALSE
            if (x == -5) {} //@ALWAYS_FALSE
            if (x == 1000) {} //@ALWAYS_FALSE
            if (x == 44 || x == 33) {} //@ALWAYS_FALSE
        }
    }

    void testLogic10(int x, int y)
    {
        // system of linear equations
        if (2 * x + 3 * y == 12)
        {
            if (5 * x - 2 * y == 11)
            {
                if (x == 3) {} //@ALWAYS_TRUE
                if (y == 2) {} //@ALWAYS_TRUE
            }
        }
    }

    void testLogic11(int x, int y, int z)
    {
        // system of linear equations
        if (x + 3 * y - 2 * z == 5
            && 3 * x + 5 * y + 6 * z == 7
            && 2 * x + 4 * y + 3 * z == 8)
        {
            // solution: (x = 0x7ffffff1, y = 0x80000008, z =  0x00000002)
            if (x == -15
                && y == 8 //@ALWAYS_TRUE
                && z == 2) {} //@ALWAYS_TRUE

            if (x == -15) {} //ok
            if (y == 8) {} //ok
            if (z == 2) {} //ok
        }
    }

    void testLogic12(int x)
    {
        if (x * x + x - 1 == 0) {} //@ALWAYS_FALSE
        if (x * x + x + 1 == 0) {} //@ALWAYS_FALSE
    }

    void testLogic13()
    {
        int a = 1;
        int b = 2;
        int c = 3;
        int d = a + b + c;
        if (d == 6) {} //@ALWAYS_TRUE
        if (d != 6) {} //@ALWAYS_FALSE
        if (d != 7) {} //@ALWAYS_TRUE
    }

    void testLogic14()
    {
        int a = 1;
        a = a + 1;
        if (a == 1) {} //@ALWAYS_FALSE
        if (a == 2) {} //@ALWAYS_TRUE
        if (a == 3) {} //@ALWAYS_FALSE
    }

    void testLogic15(int x, byte y)
    {
        if (x + y == y + x) {} //@ALWAYS_TRUE
    }

    void testLogic16(int x)
    {
        if (x > x) {} //@ALWAYS_FALSE
        if (x < x) {} //@ALWAYS_FALSE
        if (x <= x) {} //@ALWAYS_TRUE
        if (x >= x) {} //@ALWAYS_TRUE
        if (x == x) {} //@ALWAYS_TRUE
    }
}
