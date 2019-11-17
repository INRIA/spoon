package fr.inria.spoon.dataflow.testcases;

public class TestCast
{
    private class C1 { public int x; }
    private native void unknownFunc1(C1 c);
    private native void unknownFunc2(Integer arg);
    private native int unknownFunc3();
    private native Integer unknownFunc4();
    private native Integer getInt();
    private native Object getObject();

    void testCast1(Object o, Object o2)
    {
        Float f1 = (Float) o;
        float f2 = (Float) o;
        if ((Float) o == (Float) o2) {}

        Integer i1 = (Integer) o;
        int i2 = (Integer) o;
        if ((Integer) o == (Integer) o2) {}

        if ((Boolean) o) {}

        switch ((Integer) o)
        {
            case 1: break;
            case 2: break;
        }
    }

    void testCast2()
    {
        Integer i0 = new Integer(42);
        Integer i1 = (int) i0;
        if (i1 == 42) {} //@ALWAYS_TRUE
    }

    void testCast3()
    {
        int i = 100;
        if (i == (long)10000000) {} //@ALWAYS_FALSE
    }

    void testCast4(Object o, Object o2)
    {
        Integer i1 = (Integer) o;
        int i2 = (Integer) o;
        if ((Integer) o == (Integer) o2) {}
    }

    void testCast5()
    {
        Integer i = (Integer) 42;
        if (i == null) {} //@ALWAYS_FALSE
        if (i == 42) {} //@ALWAYS_TRUE
    }

    void testCast6()
    {
        int x1 = (Integer) 42;
        if (x1 == 42) {} //@ALWAYS_TRUE

        int x2 = (Integer) new Integer(42);
        if (x2 == 42) {} //@ALWAYS_TRUE

        int x3 = (int) 42;
        if (x3 == 42) {} //@ALWAYS_TRUE
    }

    void testCast7()
    {
        Integer x1 = (int) 42;
        if (x1 == 42) {} //@ALWAYS_TRUE
        if (x1 == null) {} //@ALWAYS_FALSE

        Integer x2 = (int) new Integer(42);
        if (x2 == 42) {} //@ALWAYS_TRUE
        if (x2 == null) {} //@ALWAYS_FALSE

        Integer x3 = (Integer) new Integer(42);
        if (x3 == 42) {} //@ALWAYS_TRUE
        if (x3 == null) {} //@ALWAYS_FALSE
    }

    void testCast8()
    {
        short s = 42;
        int x = (Integer) (int) s;
        if (x == 42) {} //@ALWAYS_TRUE
    }

    void testCast9()
    {
        int x = (int) (Character) 'z';
        if (x == 'z') {} //@ALWAYS_TRUE
    }

    void testCast10()
    {
        int x1 = (int) (Character) (char) 100;
        if (x1 == 100) {} //@ALWAYS_TRUE

        int x2 = (int) (Character) (char) 100000;
        if (x2 == 100000) {} //@ALWAYS_FALSE
    }

    void testCast11()
    {
        long l = 42L;
        if (getInt() > l) {} //ok
        if (((Character) getObject()) > 100000) {} //@ALWAYS_FALSE
    }

    void testCast12()
    {
        Object x = (Integer) new Integer(42);
        if (x == null) {} //@ALWAYS_FALSE
        if ((Integer) x == 42) {} //@ALWAYS_TRUE

        boolean b1 = (Boolean) true;
        if (b1 == true) {} //@ALWAYS_TRUE

        Boolean b2 = (boolean) true;
        if (b2 == true) {} //@ALWAYS_TRUE
    }

    void testCast13()
    {
        int i = 42;
        long l = i;
        float f = l;
        double d = 42.0;
        l = (long) d;
        i = (int) l;
        Object o = "str";
        String str = (String)o;
    }

    void testCast14()
    {
        int x = (char) 100000;
        if (x == 100000) {} //@ALWAYS_FALSE
    }

    void testCasts15()
    {
        Integer f = (Integer) null;
        if (f == null) {} //@ALWAYS_TRUE
    }

    void testCast16(Object o)
    {
        if ((Integer) o == 42)
        {
            unknownFunc2((Integer) o); // Integer is immutable, so the value remains
            if ((Integer) o == 42) {} //@ALWAYS_TRUE
        }
    }

    void testCast17(Object o, C1 c)
    {
        if (c.x == 42)
        {
            if (c.x == 42) {} //@ALWAYS_TRUE
            unknownFunc1(c);
            if (c.x == 42) {} //ok
        }

        if (((C1) o).x == 42)
        {
            if (((C1) o).x == 42) {} //@ALWAYS_TRUE
            unknownFunc1((C1) o);
            if (((C1) o).x == 42) {} //ok
        }
    }

    void testInvocationCast1()
    {
        if ((byte) unknownFunc3() == 300) {} //@ALWAYS_FALSE
        if ((byte) (int) unknownFunc4() == 300) {} //@ALWAYS_FALSE
    }

    void testCompareReferencesCast1()
    {
        int x = 10;
        if ((Integer) x == null) {} //@ALWAYS_FALSE
        if ((Integer) x == 10) {} //@ALWAYS_TRUE
        Integer y = 10;
        if ((int) y == 10) {} //@ALWAYS_TRUE
    }

    void testLiteralsCast1()
    {
        Object x = (Object) "sss";
        if (x == null) {} //@ALWAYS_FALSE

        Object y = (Object) 42;
        if (y == null) {} //@ALWAYS_FALSE
    }

    void testMultipleCasts1(int i, long res)
    {
        i = 1234567;

        res = (byte) i;
        if (res == -121) {} //@ALWAYS_TRUE

        res = (int)(byte) i;
        if (res == -121) {} //@ALWAYS_TRUE

        res = (char)(int)(byte) i;
        if (res == 65415) {} //@ALWAYS_TRUE

        res = (long)(char)(int)(byte) i;
        if (res == 65415) {} //@ALWAYS_TRUE

        res = (int)(char)i;
        if (res == 54919) {} //@ALWAYS_TRUE

        res = (char) i;
        if (res == 54919) {} //@ALWAYS_TRUE

        i = -1234567;

        res = (byte) i;
        if (res == 121) {} //@ALWAYS_TRUE

        res = (long)(char)(int)(byte) i;
        if (res == 121) {} //@ALWAYS_TRUE

        res = (byte)(char) i;
        if (res == 121) {} //@ALWAYS_TRUE
    }

    void testMultipleCasts2()
    {
        int i = 1234567;

        int res = (int)(char)i;
        if (res == 54919) {} //@ALWAYS_TRUE

        res = (int)(char)1234567;
        if (res == 54919) {} //@ALWAYS_TRUE

        res = (int)(char)+1234567;
        if (res == 54919) {} //@ALWAYS_TRUE
    }

    void testBinOpCast1()
    {
        long x = 12345678912345L;
        if ((int)x == 1942903641) {} //@ALWAYS_TRUE
        if ((int)x == 12345678912345L) {} //@ALWAYS_FALSE
    }

    void testFieldsCast1()
    {
        C1 a = new C1();
        a.x = (int) '0';
        if (a.x == 48) {} //@ALWAYS_TRUE
    }

    void testFieldsCast2()
    {
        C1 a = new C1();
        a.x = (char) 100000;
        if (a.x == 34464) {} //@ALWAYS_TRUE
    }

    void testObjectCast1(Object x)
    {
        if ((Integer) x > 10)
        {
            if ((Integer) x > 5) {} //@ALWAYS_TRUE
        }
    }

    void testObjectCast2(Object o)
    {
        if ((Boolean)o)
        {
            if ((Boolean)o) {} //@ALWAYS_TRUE
        }
        if ((Boolean)o == true)
        {
            if ((Boolean)o) {} //@ALWAYS_TRUE
        }
    }

    void testObjectCast3(Integer i, Boolean b)
    {
        b = false;
        i = 32;
        if (((int) i) > 123) {} //@ALWAYS_FALSE
        if ((boolean) b) {} {} //@ALWAYS_FALSE
    }

    void testObjectsCast4()
    {
        Integer x = 1000;
        if (((byte)(int)x == 1000)) {} //@ALWAYS_FALSE
        if (((byte)(int)x == -24)) {} //@ALWAYS_TRUE
    }

    void testObjectCast5(boolean cond)
    {
        Object x = new Integer(42);
        if (cond)
        {
            x = new Integer(1234);
        }
        if ((Integer) x > 10) {} //@ALWAYS_TRUE
    }
}
