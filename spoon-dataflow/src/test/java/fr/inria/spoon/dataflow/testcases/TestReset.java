package fr.inria.spoon.dataflow.testcases;

public class TestReset
{
    private class C1 { int x; }
    private class C2 { C1 c1; }
    private class C3 { C2 c2; }
    private class C4 { int x; int y; }
    private class C5 { int a; int b; }
    private class C6 { int x; public native void f(); }
    private class C7 { int[] arr1; }
    private native C1 getC1();
    private static int SomeStaticField;
    private native void func1(int arg1, int arg2);
    private native void func2(C4 arg1, C5 arg2);
    private native void func3(C4 arg1);
    private native void func4(C1 arg1);
    private native void func5(C2 arg1);
    private native void func6(String arg1);

    void testReset1(C1 a)
    {
        a.x = 0;
        for (int i = 0; i < 3; i++)
        {
            a.x = i;
        }
        if (a.x == 0) {}
    }

    void testReset2(C1 a)
    {
        if (a.x == 5)
        {
            if (a.x == 0) {} //@ALWAYS_FALSE
            for (int i = 0; i < 3; i++)
            {
                a.x = i;
            }
            if (a.x == 0) {} //ok
        }
        if (a.x == 0) {} //ok
    }

    void testReset3()
    {
        C1 a = getC1();
        for (int i = 0; i < 3; i++)
        {
            a.x = i;
        }
        if (a.x == 0) {} //ok
    }

    void testReset4(C1 a, C1 b)
    {
        if (b.x > 10)
        {
            return;
        }
        a = b;
        if (a.x > 100) {} //@ALWAYS_FALSE
        for (int i = 0; i < 3; i++)
        {
            a.x += i;
        }
        if (a.x == 0) {} //ok
        if (a.x > 100) {} //ok
    }

    void testReset5(C1 a, C1 b)
    {
        if (b.x > 10)
        {
            return;
        }
        a.x = b.x;
        if (a.x > 100) {} //@ALWAYS_FALSE
        for (int i = 0; i < 3; i++)
        {
            a.x = i;
        }
        if (a.x == 0) {} //ok
        if (a.x > 100) {} //ok
    }

    void testReset6()
    {
        SomeStaticField = 5;
        for (int i = 0; i < 3; i++)
        {
            SomeStaticField = i;
        }
        if (SomeStaticField == 5) {} //ok
    }

    void testReset7()
    {
        for (int i = 0; i < 3; i++)
        {
            getC1().x = 0;
        }
        if (getC1().x == 0) {} //ok
    }

    void testReset8()
    {
        C4 a = new C4();
        a.x = 5;
        a.y = 6;
        func1(a.x, a.y);
        if (a == null) {} //@ALWAYS_FALSE
        if (a.x == 5) {} //@ALWAYS_TRUE
        if (a.y == 6) {} //@ALWAYS_TRUE

        C5 b = new C5();
        b.a = 10;
        b.b = 20;
        func2(a, b);
        if (a == null) {} //@ALWAYS_FALSE
        if (b == null) {} //@ALWAYS_FALSE
        if (a.x == 5) {} //ok
        if (a.y == 6) {} //ok
        if (b.a == 10) {} //ok
        if (b.b == 20) {} //ok
    }

    void testReset9()
    {
        C4 a = new C4();
        a.x = 5;
        a.y = 6;
        C5 b = new C5();
        b.a = 1;
        b.b = 2;
        for (int i = 0; i < 10; i++)
        {
            func2(a, b);
        }
        if (a.x == 5) {} //ok
        if (a.y == 6) {} //ok
        if (b.a == 1) {} //ok
        if (b.b == 2) {} //ok
    }

    void testReset10()
    {
        C6 a = new C6();
        a.x = 42;
        for (int i = 0; i < 10; i++)
        {
            a.f();
        }
        if (a.x == 42) {} //ok
    }

    void testReset11()
    {
        C4 a = new C4();
        a.x = 42;
        for (int i = 0; i < 10; i++)
        {
            func3(a);
        }
        if (a.x == 42) {} //ok
    }

    void testReset12()
    {
        C2 a = new C2();
        a.c1 = new C1();
        a.c1.x = 42;
        for (int i = 0; i < 10; i++)
        {
            func4(a.c1);
        }
        if (a == null) {} //@ALWAYS_FALSE
        if (a.c1 == null) {} //@ALWAYS_FALSE
        if (a.c1.x == 42) {} //ok
    }

    void testReset13()
    {
        C2 a = new C2();
        a.c1 = new C1();
        a.c1.x = 42;
        for (int i = 0; i < 10; i++)
        {
            func5(a);
        }
        if (a == null) {} //@ALWAYS_FALSE
        if (a.c1 == null) {} //ok
        if (a.c1.x == 42) {} //ok
    }

    void testReset14()
    {
        String s1 = new String();
        String s2 = new String();
        for (int i = 0; i < 10; i++)
        {
            func6(s1 + s2);
        }
        if (s1 == null) {} //@ALWAYS_FALSE
        if (s2 == null) {} //@ALWAYS_FALSE
    }

    void testReset15()
    {
        C3 a = new C3();
        a.c2 = new C2();
        a.c2.c1 = new C1();
        int i = 0;
        while (i < 10)
        {
            a.c2.c1 = null;
            i++;
        }
        if (a == null) {} //@ALWAYS_FALSE
        if (a.c2 == null) {} //@ALWAYS_FALSE
        if (a.c2.c1 == null) {} //ok

        C3 b = new C3();
        b.c2 = new C2();
        b.c2.c1 = new C1();
        int j = 0;
        while (j < 10)
        {
            b.c2 = new C2();
            j++;
        }
        if (b == null) {} //@ALWAYS_FALSE
        if (b.c2 == null) {} //ok
        if (b.c2.c1 == null) {} //ok
    }

    void testReset16()
    {
        int i = 0;
        int a = 3;
        int c = 4;
        while (i < 10)
        {
            a = i + c;
            i++;
        }
        if (i == 0) {} //@ALWAYS_FALSE
        if (i == 100) {}
        if (a == 3) {}
        if (c == 4) {} //@ALWAYS_TRUE
    }

    void testReset17()
    {
        C1 x1 = new C1();
        C1 x2 = new C1();
        int i = 0;
        int a = 3;
        while (i < 10)
        {
            a = i;
            x1 = null;
            x2.x = 0;
            i++;
        }
        if (i == 0) {} //@ALWAYS_FALSE
        if (i == 100) {}
        if (a == 3) {}
        if (x1 == null) {}
        if (x2 == null) {} //@ALWAYS_FALSE
    }

    void testArrayReset1()
    {
        int[] arr = {1, 2, 3};
        for (int i = 0; i < 3; i++)
        {
            arr[i] = 0;
        }
        if (arr[0] == 0) {}
        if (arr[1] == 0) {}
        if (arr[2] == 0) {}

        if (arr[0] == 1) {}
        if (arr[1] == 2) {}
        if (arr[2] == 3) {}
    }

    void testArrayReset2()
    {
        int[] arr = {1, 2, 3};
        for (int i = 0; i < 3; i++)
        {
            arr[i] = 0;
        }
        if (arr[0] == 0) {} //ok
    }

    void testArrayReset3(boolean cond)
    {
        int[] arr1 = {1,2,3};
        int[] arr2 = {3,4,5};
        if (arr1[0] == 1) {} //@ALWAYS_TRUE
        if (cond)
        {
            arr1 = arr2;
            if (arr1[0] == 3) {} //@ALWAYS_TRUE
        }
        if (arr1[0] == 1 || arr1[0] == 3) {} //@ALWAYS_TRUE
        for (int i = 0; i < 3; i++)
        {
            arr1[i] = 0;
        }
        if (arr1[0] == 1) {}
        if (arr1[0] == 1 || arr1[0] == 3) {}
    }

    void testArrayReset4()
    {
        int[] arr1 = new int[3];
        arr1[0] = 5;
        for (int i = 0; i < 3; i++)
        {
            arr1[i] = 0;
        }
        if (arr1[0] == 0) {}
        if (arr1[0] == 5) {}
    }

    void testArrayReset5()
    {
        C7 o = new C7();
        o.arr1[0] = 5;
        for (int i = 0; i < 3; i++)
        {
            o.arr1[i] = 0;
        }
        if (o.arr1[0] == 0) {}
        if (o.arr1[0] == 5) {}
    }
}
