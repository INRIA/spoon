package fr.inria.spoon.dataflow.testcases;

public class TestNullDereference
{
    private class C1 { int x; }

    void testNullDereference1(String s)
    {
        s = null;
        int x = s.length(); //@NULL_DEREFERENCE
    }

    void testNullDereference2()
    {
        String s = null;
        int x = s.length(); //@NULL_DEREFERENCE
    }

    void testNullDereference3(String s)
    {
        int x = s.length(); //ok
    }

    void testNullDereference4(String s)
    {
        if (s == null)
        {
            int x = s.length(); //@NULL_DEREFERENCE
        }
    }

    void testNullDereference5()
    {
        C1 a = null;
        int z = a.x; //@NULL_DEREFERENCE
    }

    void testNullDereference6()
    {
        C1 a = null;
        a.x = 5; //@NULL_DEREFERENCE
    }

    void testNullDereference7(C1 a)
    {
        if (a == null && a.x == 0) {} //@NULL_DEREFERENCE
    }

    void testNullDereference8()
    {
        int[] arr = null;
        arr[0] = 5; //@NULL_DEREFERENCE
    }

    void testNullDereference9()
    {
        int[] arr = null;
        int z = arr[0]; //@NULL_DEREFERENCE
    }

    void testNullDereference10(boolean cond)
    {
        Object x = null;
        if (cond)
        {
            x = new Object();
        }
        if (cond)
        {
            int z = x.hashCode(); //ok
        }
    }

    void testNullDereference11(boolean cond)
    {
        Object x = new Object();
        if (cond)
        {
            x = null;
        }
        if (cond)
        {
            int z = x.hashCode(); //@NULL_DEREFERENCE
        }
    }
}
