package fr.inria.spoon.dataflow.testcases;

public class TestShortCircuit
{
    void testShortCircuit1(boolean a)
    {
        if (a || a) {} //@ALWAYS_FALSE
        if (a && a) {} //@ALWAYS_TRUE
        if (a && !a) {} //@ALWAYS_FALSE
        if (a || !a) {} //@ALWAYS_TRUE
    }

    void testShortCircuit2(int a, boolean b)
    {
        if (b && b) {} //@ALWAYS_TRUE
        if (b || b) {} //@ALWAYS_FALSE
        if (!b && !b) {} //@ALWAYS_TRUE
        if (!b || !b) {} //@ALWAYS_FALSE
        if (b == true && b == true) {} //@ALWAYS_TRUE

        if (a == 1 && a < 3) {} //@ALWAYS_TRUE
        if (a == 3 && a != 1) {} //@ALWAYS_TRUE
        if (a > 3 && a > 1) {} //@ALWAYS_TRUE
        if (a > 1 && a > 3) {}
        if (a > 3 || a > 1) {}
        if (a > 1 || a > 3) {} //@ALWAYS_FALSE
        if (a < 3 && a < 1) {}
        if (a < 1 && a < 3) {} //@ALWAYS_TRUE
        if (a < -3 && a < -1) {} //@ALWAYS_TRUE
        if (a < -1 || a < -3) {} //@ALWAYS_FALSE

        if (a > 1 || a < 3) {} //@ALWAYS_TRUE
        if (a < 3 && a > 1) {} //ok
    }

    void testShortCircuit3(int a, boolean b, boolean c, boolean d)
    {
        if (a > 1 && b && a > 4) {}
        if (a > 4 && b && !c && a > 1 && d) {} //@ALWAYS_TRUE

        if (a > 1 && (b && a > 4)) {}
        if (a > 1 && b && a > 4) {}
        if ((a > 1 && b) && a > 4) {}

        if (a > 1 || b && a > 3) {} //@ALWAYS_FALSE
        if (a > 3 || b && a > 1) {} //ok
        if (a > 3 || b && a > 3) {} //@ALWAYS_FALSE

        if (a > 3 && (b || a > 0 || c)) {} //@ALWAYS_TRUE
        if (a > 3 && (b && a > 0 || c)) {} //@ALWAYS_TRUE
    }

    void testShortCircuit4(int a, boolean b)
    {
        if (a > 1 && !(b && a < 0)) {} //@ALWAYS_FALSE //@ALWAYS_TRUE
        if (!(b && a < 0) && a > 1) {}

        if (!(b && a < 0) || a > 1) {} //@ALWAYS_FALSE
        if (a > 1 || !(b && a < 0)) {}

        boolean c = !(b && a < 0) || a > 1; //@ALWAYS_FALSE
        c = a > 1 || !(b && a < 0);
    }

    void testShortCircuit5(int a, boolean b, boolean c)
    {
        boolean f = a > 0 && a > 1 && a > 2 && a > 3 && a > 4 && a > 5 && b && !c &&
                a > 6 && a > 7 && a > 8 && a > 9 && a > 10 && a > 11 && a > 12 && a > 13 &&
                a > 14 && a > 15 && a > 16 && a > 17 && a > 18 && a > 19 && a > 20 && a > 21 &&
                a > 22 && a > 23 && a > 24 && a > 25 && a > 26 && a > 27 && a > 28 && a > 29 &&
                a > 30 && a > 31 && a > 32 && a > 33 && a > 34 && a > 35 && a > 36 && a > 37;
    }

    void testShortCircuit6(int a, boolean b, boolean c)
    {
        boolean f = a != 0 && a != 1 && a != 2 && a != 3 && a != 4 && a != 5 && b && !c && //ok
                a != 6 && a != 7 && a != 8 && a != 9 && a != 10 && a != 11 && a != 12 && a != 13 &&
                a != 14 && a != 15 && a != 16 && a != 17 && a != 18 && a != 19 && a != 20 && a != 21 &&
                a != 22 && a != 23 && a != 24 && a != 25 && a != 26 && a != 27 && a != 28 && a != 29 &&
                a != 30 && a != 31 && a != 32 && a != 33 && a != 34 && a != 35 && a != 36 && a != 37;
    }

    void testShortCircuit7(int a, int b, int c)
    {
        if (b > 0 && c > b && c > 0) {} //@ALWAYS_TRUE
        if (c > b && b > 0 && c > 0) {} //@ALWAYS_TRUE
        if (c > b && b > 0 && c > 0 || c < 0) {} //@ALWAYS_TRUE
        if (b > 0 && (c < 0 || (b < c && c > 0))) {} //@ALWAYS_TRUE
        if (b > 0 && (c < 0 || (b < c && c > 0))) {} //@ALWAYS_TRUE
        if (a > 0 && b > 0 && (c < 0 || (b < c && c > 0))) {} //@ALWAYS_TRUE
        if (a > 0 && b > 0 && c < 0 && c > b) {} //@ALWAYS_FALSE
        if (a > 0 && b > 0 && c > b || c > 0) {}
    }
}
