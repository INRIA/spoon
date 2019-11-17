package fr.inria.spoon.dataflow.testcases;

public class TestReferences
{
    void testReferenceCompare1(Object o1, Object o2)
    {
        if (o1 == o2) {} //ok
        o1 = new Object();
        if (o1 == o2) {} //ok (o2 is unknown)
    }

    void testReferencePotentialUnknown(Object o1, Object o2, boolean cond, Object unknown)
    {
        o1 = new Object();
        o2 = new Object();
        if (cond)
        {
            o2 = unknown;
        }
        if (o1 == o2) {} //ok (o2 is possibly unknown)
    }

    void testReferenceCompare2(Object o1, Object o2)
    {
        o1 = new Object();
        o2 = new Object();
        if (o1 == o2) {} //@ALWAYS_FALSE
    }

    void testReferenceCompare3()
    {
        Object o1 = new Object();
        Object o2 = new Object();
        if (o1 == o2) {} //@ALWAYS_FALSE
    }

    void testReferenceCompare4(Object o1)
    {
        if (o1 == null) {} //ok
        o1 = new Object();
        if (o1 == null) {} //@ALWAYS_FALSE
    }

    void testReferenceCompare5()
    {
        if (null == null) {} //@ALWAYS_TRUE
        Object o1 = null;
        if (o1 == null) {} //@ALWAYS_TRUE
    }

    void testReferenceIf1(boolean cond)
    {
        Integer a = new Integer(42);
        if (a == null) {} //@ALWAYS_FALSE
        if (cond)
        {
            if (a == null) {} //@ALWAYS_FALSE
            a = null;
        }
        if (a == null) {} //ok
    }

    void testReferences1(Object a, Object b, Object c, Object d)
    {
        a = new Object();
        b = a;
        c = b;
        d = c;
        if (d == null) {} //@ALWAYS_FALSE
        if (d == a) {} //@ALWAYS_TRUE
    }

    void testReferenceBinOp1()
    {
        Integer a = new Integer(42);
        Integer b = new Integer(32);
        if (a >= b) {} //@ALWAYS_TRUE
        if (a < b) {} //@ALWAYS_FALSE
        if (a == b) {} //@ALWAYS_FALSE

        if (a == (b + 10)) {} //@ALWAYS_TRUE
        if (a / 10 > 10) {} //@ALWAYS_FALSE
    }

    void testReferenceUnary1()
    {
        Integer a = new Integer(42);
        if (-a > 0) {} //@ALWAYS_FALSE

        Integer b = a;
        Integer c = b;
        if (-c < 0) {} //@ALWAYS_TRUE
    }

    void testThis1()
    {
        if (this == null){} //@ALWAYS_FALSE
        TestReferences a = this;
        if (a == null) {} //@ALWAYS_FALSE
    }

    void testStringLiteral1()
    {
        if ("abcdef" == null) {} //@ALWAYS_FALSE
    }

    void testStrings1(String something)
    {
        String a = "one";
        String b = "two";
        String c = "three";
        if ((a + b + c).equals(something)) {}
    }

    class TestThisAccess
    {
        int x;
        void f1()
        {
            this.x = 142;
            if (x == 142) {} //@ALWAYS_TRUE
        }
    }

    class TestConstructor1
    {
        public Integer x;

        TestConstructor1(Integer x)
        {
            this.x = x;
            if (this.x == x) {} //@ALWAYS_TRUE
        }

        void f(Integer x)
        {
            this.x = x;
            if (this.x == x) {} //@ALWAYS_TRUE
        }
    }
}
