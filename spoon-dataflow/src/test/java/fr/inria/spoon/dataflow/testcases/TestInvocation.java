package fr.inria.spoon.dataflow.testcases;

public class TestInvocation
{
    private class C1 { int x; }
    private class C2 { int x; native void unknownFunc(); }
    private class C3 { int x; public int getX() { return x; }}
    private class C4 { C3 x; public C3 getX() { return x; }}
    private C4 getC4() { return new C4(); }
    private native int unknownFunc1();
    private native void unknownFunc2(C1 arg);
    private native void unknownFunc3(Integer arg);
    private C1 unknownFunc4() { return new C1(); }
    private native static void unknownFunc5(int i);

    void testInvocation1()
    {
        C1 obj = new C1();
        obj.x = 12;

        unknownFunc2(obj);

        if (obj == null) {} //@ALWAYS_FALSE
        if (obj.x == 12) {} //ok
    }

    void testInvocation2()
    {
        Integer x = new Integer(42);

        unknownFunc3(x); // Integer is immutable, so the value remains

        if (x == null) {} //@ALWAYS_FALSE
        if (x == 42) {} //@ALWAYS_TRUE
    }

    void testInvocation3()
    {
        int x = getC4().getX().getX();
    }

    void testInvocation4()
    {
        if (this.unknownFunc1() == this.unknownFunc1()) {} //ok
    }

    void testInvocation5()
    {
        C2 obj = new C2();
        obj.x = 42;
        if (obj.x == 42) {} //@ALWAYS_TRUE
        obj.unknownFunc();
        if (obj.x == 42) {} //ok
    }

    void testInvocationTarget1()
    {
        int a = unknownFunc4().x;
        unknownFunc4().x = 42;
        if (unknownFunc4().x == 42) {} //ok
    }

    void testInvocationTarget2(int i)
    {
        TestInvocation.unknownFunc5(i);
    }
}
