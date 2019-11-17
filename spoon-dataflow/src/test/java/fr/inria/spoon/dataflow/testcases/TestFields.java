package fr.inria.spoon.dataflow.testcases;

public class TestFields
{
    private class C1 { int x; }
    private class C2 { C1 c1; }
    private class C3 { C2 c2; }
    private class C4 { C3 c3; }
    private class O1 { Object x; }
    private class X1 { int x; }
    private class X2 { Integer x; }
    private class X3 { X1 t; }
    private class X4 { X2 t; }
    public static int staticInteger;
    public static C1 staticObject;

    void testFields1()
    {
        C1 a = new C1();
        C1 tmp = new C1();
        a.x = 10;
        if (a.x == 10) {} //@ALWAYS_TRUE
        C1 b = a;
        if (b.x == 10) {} //@ALWAYS_TRUE
        if (tmp.x == 10) {} //ok
    }

    void testFields2()
    {
        C1 a = new C1();
        C1 b = a;
        if (a == b) {} //@ALWAYS_TRUE
        b.x = 10;
        if (b.x == 10) {} //@ALWAYS_TRUE
        if (a.x == 10) {} //@ALWAYS_TRUE
        C1 c = new C1();
        if (c.x == 10) {} //ok
    }

    void testFields3()
    {
        O1 a = new O1();
        O1 tmp = new O1();
        a.x = null;
        if (a.x == null) {} //@ALWAYS_TRUE
        O1 b = a;
        if (b == null) {} //@ALWAYS_FALSE
        if (b.x == null) {} //@ALWAYS_TRUE
    }

    void testFields4()
    {
        O1 a = new O1();
        O1 tmp = new O1();
        if (a.x == null) {} //ok
        O1 b = a;
        if (b == null) {} //@ALWAYS_FALSE
        if (b.x == null) {} //ok
    }

    void testFields5()
    {
        X3 t = new X3();
        t.t = new X1();
        t.t.x = 10;
        if (t.t.x == 10) {} //@ALWAYS_TRUE
        t = new X3();
        t.t = new X1();
        if (t.t == null) {} //@ALWAYS_FALSE
        if (t.t.x == 10) {} //ok
    }

    void testFields6()
    {
        X3 t = new X3();
        if (t == null) {} //@ALWAYS_FALSE
        if (t.t == null) {} //ok
        if (t.t.x == 10) {} //ok
        t = new X3();
        if (t.t == null) {} //ok
        if (t.t.x == 10) {} //ok
    }

    void testFields7()
    {
        X3 t = new X3();
        if (t.t.x == 10) {} //ok
        if (t.t == null) {} //@ALWAYS_FALSE
    }

    void testFields8()
    {
        X3 t = new X3();
        t.t.x = 142;
        if (t.t.x == 142) {} //@ALWAYS_TRUE
    }

    void testFields9()
    {
        X4 t = new X4();
        t.t = new X2();
        t.t.x = new Integer(10);
        if (t.t.x == 10) {} //@ALWAYS_TRUE
        t = new X4();
        t.t = new X2();
        if (t.t == null) {} //@ALWAYS_FALSE
        if (t.t.x == 10) {} //ok
    }

    class X5 { Object o; }
    void testFields10()
    {
        X5 a = new X5();
        a.o = new Object();
        X5 b = a;
        b.o = null;
        if (a.o == null) {} //@ALWAYS_TRUE
        if (b.o == null) {} //@ALWAYS_TRUE
        if (a.o == b.o) {} //@ALWAYS_TRUE
        if (b.o == a.o) {} //@ALWAYS_TRUE
    }

    class X7 { Integer i; }
    void testFields11()
    {
        X7 t = new X7();
        t.i = new Integer(10);
        X7 z = new X7();
        z.i = t.i;
        if (z == t) {} //@ALWAYS_FALSE
        if (z.i == 42) {} //@ALWAYS_FALSE
        if (z.i == t.i) {} //@ALWAYS_TRUE
    }

    class X8 { int x; }
    void testFields12()
    {
        X8 a = new X8();
        X8 b = a;
        b.x = 42;
        if (a.x == 42) {} //@ALWAYS_TRUE
        if (b.x == 42) {} //@ALWAYS_TRUE
    }

    class X9 { int x; }
    void testFields13(boolean cond)
    {
        X9 a = new X9();
        a.x = 10;
        X9 b = new X9();
        if (cond)
        {
            b = a;
        }
        b.x = 42;
        if (a.x == 10) {} //ok
    }

    void testFields14(boolean cond)
    {
        X9 a = new X9();
        a.x = 15;
        if (cond)
        {
            a.x = 31;
        }
        if (a.x == 31) {} //ok
        if (a.x == 15) {} //ok
        if (a.x == 31 || a.x == 15) {} //@ALWAYS_TRUE
    }

    void testFields15()
    {
        C4 a = new C4();
        a.c3 = new C3();
        a.c3.c2 = new C2();
        a.c3.c2.c1 = new C1();
        a.c3.c2.c1.x = 5;

        C4 b = a;
        if (b == a) {} //@ALWAYS_TRUE
        if (b.c3 == null) {} //@ALWAYS_FALSE
        if (b.c3.c2 == null) {} //@ALWAYS_FALSE
        if (b.c3.c2.c1 == null) {} //@ALWAYS_FALSE
        if (b.c3.c2.c1.x == 5) {} //@ALWAYS_TRUE

        if (a.c3.c2.c1.x == 5) {} //@ALWAYS_TRUE

        C4 c = new C4();
        if (c.c3.c2.c1.x == 5) {} //ok
    }

    void testFields16(boolean cond)
    {
        C4 a = new C4();
        a.c3 = new C3();
        a.c3.c2 = new C2();
        a.c3.c2.c1 = new C1();
        a.c3.c2.c1.x = 5;
        if (a.c3.c2.c1.x == 5) {} //@ALWAYS_TRUE
        if (cond)
        {
            a = new C4();
        }
        if (a == null) {} //@ALWAYS_FALSE
        if (a.c3 == null) {} //ok
        if (a.c3.c2 == null) {} //ok
        if (a.c3.c2.c1 == null) {} //ok
        if (a.c3.c2.c1.x == 5) {} //ok
    }

    void testUnknownFields1(X2 a)
    {
        if (a == a) {} //@ALWAYS_TRUE
        if (a == null) {} //ok
        if (a.x == 10) {} //ok
    }

    void testUnknownFields2(X2 a, X2 b)
    {
        X2 t = a;
        if (a == t) {} //@ALWAYS_TRUE
        if (a.x == t.x) {} //@ALWAYS_TRUE
        if (t.x == t.x) {} //@ALWAYS_TRUE
        if (a.x == b.x) {} //ok
        if (a == b) {} //ok
    }

    void testUnknownFields3(X2 a, X2 b)
    {
        if (a == b)
        {
            return;
        }
        if (a == b) {} //@ALWAYS_FALSE
    }

    void testUnknownFields4(X2 a, X2 b)
    {
        if (a.x == b.x)
        {
            return;
        }
        if (a == b) {} //@ALWAYS_FALSE
        if (a.x == b.x) {} //@ALWAYS_FALSE
    }

    void testUnknownFields5(X2 a, X2 b)
    {
        if (a.x != b.x)
        {
            return;
        }
        if (a == b) {} //ok
        if (a.x == b.x) {} //@ALWAYS_TRUE
    }

    void testUnknownFields6(boolean cond, X2 a, X2 b)
    {
        if (cond)
        {
            a = new X2();
        }
        if (a == b)
        {
            if (b == null) {} //ok
            if (cond)
            {
                if (b == null) {} //@ALWAYS_FALSE
            }
        }
    }

    void testUnknownFields7(X2 a, X2 b)
    {
        if (a == b)
        {
            if (a == b) {} //@ALWAYS_TRUE
            if (a.x == b.x) {} //@ALWAYS_TRUE
        }
    }

    void testUnknownFields8(X2 a, X2 b)
    {
        if (a.x == b.x)
        {
            if (a == b) {} //ok
            if (a.x == b.x) {} //@ALWAYS_TRUE
        }
    }

    void testUnknownFields9(X2 a)
    {
        a.x = 3;
        a.x += 1;
        if (a.x == 4) {} //@ALWAYS_TRUE
    }

    void testStaticField1()
    {
        staticInteger = 42;
        if (staticInteger == 42) {} //@ALWAYS_TRUE
    }

    void testStaticField2()
    {
        staticObject = new C1();
        staticObject.x = 42;
        if (staticObject.x == 42) {} //@ALWAYS_TRUE
        C1 a = staticObject;
        a.x = 1234;
        if (staticObject.x == 1234) {} //@ALWAYS_TRUE
    }
}
