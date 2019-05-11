package fr.inria.spoon.dataflow.testcases;

public class TestAnonymousClass
{
    int z;

    void testAnonymousClass1()
    {
        Object o = new Object()
        {
            @Override
            public int hashCode()
            {
                return 42;
            }
        };
    }

    void testAnonymousClass2()
    {
        final int a = 0;
        z = 0;
        Object o = new Object()
        {
            @Override
            public int hashCode()
            {
                if (z == 0) {} //ok
                if (a == 0) {} //@ALWAYS_TRUE
                z = 42;
                return 42;
            }
        };

        z = 1;
        int u = o.hashCode();
        if (z == 42) {} //FIXME: //@ALWAYS_FALSE

        Object o2 = new Object()
        {
            @Override
            public int hashCode()
            {
                if (z == 0) {} //ok
                return 42;
            }
        };
    }

    void testAnonymousClass3()
    {
        z = 0;
        final int i = 42;
        Object o = new Object()
        {
            @Override
            public int hashCode()
            {
                z = 1 + i;
                return 42;
            }
        };
        if (z == 0) {} //@ALWAYS_TRUE
        if (z == 1) {} //@ALWAYS_FALSE
        o.hashCode();
        if (z == 0) {} //FIXME: //@ALWAYS_TRUE
        if (z == 1) {} //FIXME: //@ALWAYS_FALSE
    }

    class C {int x;}
    void testAnonymousClass4()
    {
        C c = new C();
        c.x = 42;
        Object o = new Object()
        {
            @Override
            public int hashCode()
            {
                c.x = 1; //ok
                return 42;
            }
        };
        if (c == null) {} //@ALWAYS_FALSE
        if (c.x == 42) {} //@ALWAYS_TRUE
        o.hashCode();
        if (c == null) {} //@ALWAYS_FALSE
        if (c.x == 42) {} //FIXME: //@ALWAYS_TRUE
    }

    Object ooo = new Object()
    {
        @Override
        public int hashCode()
        {
            return 42;
        }
    };

    int a;
    final int b = 142;
    class X { int x; }
    void f()
    {
        a = 0;
        int j = 42;
        final int i = 42;
        int[] arr = {1, 2, 3};
        X q = new X();
        q.x = 42;
        Object o = new Object()
        {
            @Override
            public int hashCode()
            {
                if (a == 100) {} //ok
                if (b == 142) {} //@ALWAYS_TRUE
                if (i == 42) {} //@ALWAYS_TRUE
                if (j == 142) {} //@ALWAYS_FALSE
                if (arr[1] == 2) {} //ok
                if (q.x == 42) {} //ok
                return 42;
            }
        };
        if (a == 0) {} //@ALWAYS_TRUE
        if (a == 1) {} //@ALWAYS_FALSE
        o.hashCode();
        if (a == 0) {} //FIXME: //@ALWAYS_TRUE
        if (a == 1) {} //FIXME: //@ALWAYS_FALSE
    }
}
