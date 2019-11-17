package fr.inria.spoon.dataflow.testcases;

import java.util.ArrayList;
import java.util.List;

public class TestLoops
{
    void testWhile1()
    {
        int a = 0;
        int b = 0;
        int c = 0;
        while (a < 10)
        {
            b = c + a;
            a++;
        }
        if (b == 0) {}
        if (b == 9) {}
        if (c == 0) {} //@ALWAYS_TRUE
        if (a >= 10) {} //@ALWAYS_TRUE
        if (a == 5) {} //@ALWAYS_FALSE
    }

    void testWhile2()
    {
        int a = 0;
        int c = 5;
        while (a < 10)
        {
            if (a == 5) {}
            if (c == 5) {} //@ALWAYS_TRUE
            a++;
        }
        if (c == 5) {} //@ALWAYS_TRUE
    }

    void testWhile3(boolean cond)
    {
        int a = 0;
        int b = 0;
        while (a < 5)
        {
            if (cond)
            {
                b++;
            }
            a++;
        }
        if (a == 3) {} //@ALWAYS_FALSE
        if (a == 123) {}
        if (b == 123) {}
    }

    void testWhile4()
    {
        int b = 0;
        int a = 3;
        while (b < 10)
        {
            if (a == 3) //ok
            {
                a = 5;
            }
            b++;
        }
        if (a == 5) {}
    }

    void testWhile5(boolean cond)
    {
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        while (a < 5)
        {
            if (a == 0) {}
            if (cond) { d = d + 1; }

            b = 5;
            if (cond)
            {
                d = 55;
            }

            if (a < 0) {}
            if (a >= 5) {} //@ALWAYS_FALSE
            e += 2;
            a++;
        }
        if (b == 5) {}
        if (c == 0) {} //@ALWAYS_TRUE
        if (e == 123) {} //ok
    }

    void testWhile7(boolean cond)
    {
        int a = 0;
        int b = 0;
        while (b < 10)
        {
            if (a == 1)
            {
                a = 1;
            }
            if (a == 0)
            {
                a = 1;
            }
            if (a == 1) {}
            b++;
        }
        if (a == 1) {}
    }

    void testWhile8(boolean cond)
    {
        int a = 0;
        while (a < 10)
        {
            if (a == 3)
            {
                if (a == 3) {} //@ALWAYS_TRUE
            }
            a++;
        }
    }

    void testWhile9(boolean cond)
    {
        int a = 0;
        int b = 0;
        int c = 0;
        while (a < 10)
        {
            b = 10;
            a++;

            if (b == 0) {} //@ALWAYS_FALSE
            if (a == 2) {}
            if (c == 0) {} //@ALWAYS_TRUE
        }
        if (a == 0) {} //@ALWAYS_FALSE
        if (b == 0) {}
        if (c == 0) {} //@ALWAYS_TRUE
    }

    void testWhile10()
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            if (a == 5)
            {
                b = 10;
            }
            else if (a == 6)
            {
                b = 20;
            }
            else if (a == 7)
            {
                b = 30;
            }
            a++;
            if (b == 10) {}
            if (b == 40) {}
        }
        if (a == 5) {} //@ALWAYS_FALSE
        if (b == 0 || b == 10 || b == 20 || b == 30) {}
    }

    void testWhile11()
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            b = 55;
            a++;
        }
        if (b == 55) {}
    }

    void testWhile12(int a)
    {
        int b = 0;
        while (a < 10)
        {
            if (a == 2) { b = 10; }
            b = 55;
            a++;
        }
        if (b == 55) {} //ok
    }

    void testWhile13()
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            if (a == 2 || a == 3)
            {
                b = 10;
            }
            a++;
            if (b == 10) {} //ok
            if (b == 0) {} //ok
        }
        if (b == 10) {} //ok
        if (b == 0) {} //ok
    }

    void testWhile14()
    {
        int a = 0;
        while (a < 12)
        {
            a += 5;
        }
        if (a >= 12) {} //@ALWAYS_TRUE
    }

    void testWhile15()
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            if (a == 0)
            {
                b++;
            }
            a++;
            if (b == 2) {}
        }
        if (b == 2) {}
    }

    void testWhile16()
    {
        int a = 0;
        while (a < 12)
        {
            a++;
        }
        if (a >= 11) {} //@ALWAYS_TRUE
        if (a >= 12) {} //@ALWAYS_TRUE
        if (a == 12) {}
        if (a >= 13) {}
        if (true) {} //@ALWAYS_TRUE
    }

    void testWhile17(boolean cond)
    {
        int a = 0;
        while (a < 10)
        {
            if (cond)
            {
                return;
            }
            a++;
            if (cond) {} //@ALWAYS_FALSE
        }
        if (a == 10) {}
        if (a >= 10) {} //@ALWAYS_TRUE
    }

    void testWhile18()
    {
        int a = 0;
        while (a < 10) {}
        if (a == 0) {} //@ALWAYS_FALSE
    }

    void testWhile19(boolean cond)
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            if (cond)
            {
                b = 42;
                break;
            }
            a++;
            b++;
        }
        if (a == 0) {} //ok
        if (b == 42) {} //ok
    }

    void testWhile20(int a, int b)
    {
        while (a < 10)
        {
            b = 10;
            a++;
        }
        if (a >= 10) {} //@ALWAYS_TRUE
        if (b == 10) {} //ok
    }

    void testWhile21(int a, int b, boolean cond)
    {
        while (a < 10)
        {
            if (cond)
            {
                b = 20;
                break;
            }
            b = 10;
            a++;
        }
        if (a >= 10) {} //ok
        if (b == 10) {} //ok
        if (b == 20) {} //ok
        if (true) {} //@ALWAYS_TRUE
    }

    void testWhile22(int a, int b, boolean cond)
    {
        a = 0;
        b = 0;
        while (a < 10)
        {
            if (a  < 40) {} //@ALWAYS_TRUE
            if (cond)
            {
                b = 20;
                break;
            }
            b = 10;
            a++;
        }
        if (a >= 10) {} //ok
        if (b == 10) {} //ok
        if (b == 20) {} //ok
        if (true) {} //@ALWAYS_TRUE
    }

    void testWhile23(boolean cond)
    {
        int i = 0;
        Object x = new Object();
        Object y = x;
        if (x == y) {} //@ALWAYS_TRUE
        while (cond)
        {
            if (i == 42)
            {
                x = new Object();
            }
            if (i == 100)
            {
                cond = false;
            }
            i++;
        }
        if (cond) {} //@ALWAYS_FALSE
        if (x == y) {} //ok
    }

    void testWhile24(boolean cond)
    {
        int x = 1;
        while (x == 1)
        {
            if (cond)
            {
                return;
            }
        }
        if (cond) {} //@ALWAYS_FALSE
    }

    void testWhile25(boolean cond)
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            while (b < 10)
            {
                b++;
            }
            if (b >= 10) {} //@ALWAYS_TRUE
            a++;
        }
        if (a >= 10) {} //@ALWAYS_TRUE
        if (b >= 10) {}
    }

    void testWhile26(boolean cond)
    {
        int a = 0;
        int b = 0;
        while (a < 10)
        {
            while (b < 10)
            {
                if (cond)
                {
                    break;
                }
                b++;
            }
            a++;
        }
        if (a >= 10) {} //@ALWAYS_TRUE
        if (b >= 10) {} //ok
    }

    void testWhile27()
    {
        int a = 0;
        while(true)
        {
            if (a == 100)
            {
                break;
            }
            a++;
        }
        if (true) {} //@ALWAYS_TRUE

        boolean z = false;
        while (z) {} //@ALWAYS_FALSE
        if (z) {} //@ALWAYS_FALSE
    }

    class X14 { native X14 next(); }
    void testWhile28()
    {
        X14 x = new X14();
        int a = 0;
        while (x != null)
        {
            x = x.next();
            a++;
        }
        if (x == null) {} //@ALWAYS_TRUE
        if (a == 100) {} //ok
    }

    void testWhile29(X15 y)
    {
        X15 x = new X15();
        while (x != y)
        {
            x.f();
        }
        if (x == y) {} //@ALWAYS_TRUE
    }

    class X15 { int a; native void f(); }
    void testWhile30(X15 y)
    {
        X15 x = new X15();
        while (x.a != y.a)
        {
            x.f();
        }
        if (x.a == y.a) {} //@ALWAYS_TRUE
        if (x == null) {} //@ALWAYS_FALSE
    }

    void testWhile31(X15 y)
    {
        X15 x = new X15();
        x.a = 42;
        while (x.a != y.a)
        {
            x.f();
        }
        if (x.a == 42) {} //ok
        if (x.a == 142) {} //ok
        if (x.a == y.a) {} //@ALWAYS_TRUE
        if (x == null) {} //@ALWAYS_FALSE
    }

    void testWhile32(X15 y, int z)
    {
        X15 x = new X15();
        x.a = 42;
        while (x.a != y.a)
        {
            x.a = z;
            x.f();
        }
        if (x.a == 42) {} //ok
        if (x.a == 142) {} //ok
        if (x.a == y.a) {} //@ALWAYS_TRUE
        if (x == null) {} //@ALWAYS_FALSE
    }

    void testWhile33(X15 y, boolean c1, boolean c2, boolean c3, int i1)
    {
        X15 x = new X15();
        while (c1)
        {
            while (c2)
            {
                if (i1 == 5)
                {
                    break;
                }
                if (i1 == 5) {} //@ALWAYS_FALSE
                if (i1 > 10) {} //ok
                while (i1 > 10)
                {
                    i1++;
                }
                if (i1 > 10) {} //@ALWAYS_FALSE
            }
            if (i1 == 5) {} //ok

            if (c2) {}
            if (c3)
            {
                x = null;
                break;
            }
            if (c3) {} //@ALWAYS_FALSE
        }
        if (c1) {}
        if (c3) {} //ok
        if (x == null) {} //ok
    }

    void testWhile34(boolean cond)
    {
        int i = 0;
        while (i < 10)
        {
            if (cond) { continue; }
            i++;
        }
        if (i < 10) {} //@ALWAYS_FALSE
    }

    void testWhile35(boolean cond)
    {
        int i = 0;
        while (i < 10)
        {
            if (cond) { break; }
            i++;
        }
        if (i < 10) {} //ok
    }

    void testWhile36(boolean cond1, boolean cond2)
    {
        int i = 0;
        while(i < 10)
        {
            if (cond1) { break; }
            if (cond1) {} //@ALWAYS_FALSE
            if (cond2) { continue; }
            i++;
        }
        if (i < 10) {} //ok
    }

    void testWhile37(boolean cond)
    {
        int i = 0;
        while (i < 10)
        {
            if (cond) { continue; }
            if (cond) {} //@ALWAYS_FALSE
            i++;
        }
    }

    class TestWhile38
    {
        public int x;
        public void f()
        {
            int i = 0;
            this.x = 5;
            while (i < 10)
            {
                this.x++;
                i++;
            }
            if (i >= 10) {} //@ALWAYS_TRUE
            if (this.x == 15) {} //ok
        }
    }

    void testWhile39()
    {
        boolean a = true;
        while (a) {} //ok

        boolean b = false;
        while (b) {} //@ALWAYS_FALSE
    }

    void testWhile40()
    {
        int i = 0;
        while (i > 10) //@ALWAYS_FALSE
        {
            i--;
        }
    }

    void testWhile41(boolean cond, int i)
    {
        if (cond)
        {
            while (i < 10)
            {
                i++;
            }
            if (i == 3) {} //@ALWAYS_FALSE
        }
        if (i == 3) {} //ok
    }

    void testDo1(int i)
    {
        do
        {
            if (i < 10) {} //ok
            if (i >= 10) {} //ok
            i++;
        }
        while (i < 10);

        if (i < 10) {} //@ALWAYS_FALSE
        if (i >= 10) {} //@ALWAYS_TRUE
    }

    void testDo2()
    {
        int i = 0;
        do
        {
            if (i == 5) {} //ok
            i++;
        }
        while (i < 10);
    }

    void testDo3()
    {
        int a = 0;
        int c = 5;
        do
        {
            if (c == 5) {} //@ALWAYS_TRUE
            a++;
        }
        while (a != 5);
        if (c == 5) {} //@ALWAYS_TRUE
        if (a == 5) {} //@ALWAYS_TRUE
    }

    void testDo4()
    {
        int a = 5;
        do
        {
            a = 10;
        }
        while (a < 8);
        if (a < 8) {} //@ALWAYS_FALSE
    }

    void testDo5()
    {
        boolean a = false;
        do
        {
        }
        while (a); //@ALWAYS_FALSE
    }

    void testDo6()
    {
        int i = 0;
        do
        {
        }
        while (i > 10); //@ALWAYS_FALSE
    }

    void testDo7()
    {
        int i = 0;
        do
        {
            i += 100;
        }
        while (i > 10);
    }

    void testFor1()
    {
        int i = 42;
        for (i = 0; i < 10; i++)
        {
            if (i < 10) {} //@ALWAYS_TRUE
            if (i >= 10) {} //@ALWAYS_FALSE
        }
        if (i >= 10) {} //@ALWAYS_TRUE
    }

    void testFor2(int i, int j)
    {
        for (i = 0, j = 42; i < 10; i++)
        {
            if (i == 5) {} //ok
            if (i == 50) {} //@ALWAYS_FALSE
            if (j == 42) {} //@ALWAYS_TRUE
        }
        if (j == 42) {} //@ALWAYS_TRUE
    }

    void testFor3()
    {
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                if (i == 0) {} //ok
                if (j == 0) {} //ok
            }
        }
    }

    void testFor4()
    {
        for (int i = 0; i < 10; i++)
        {
            if (i == 0) {}
        }

        for (int j = 0; j < 10; j++)
        {
            if (j == 0) {}
        }
    }

    void testFor5()
    {
        int c = 42;
        int i = 0;
        for (; i < 10; i++)
        {
            if (c == 42) {} //@ALWAYS_TRUE
            if (i == 5) {} //ok
        }
        if (c == 42) {} //@ALWAYS_TRUE
        if (i >= 10) {} //@ALWAYS_TRUE
    }

    void testFor6()
    {
        for (int i = 0, j = 1; i < 10 && j < 3; j++) //@ALWAYS_TRUE
        {
            if (i == 0) {} //@ALWAYS_TRUE
            if (j == 0) {} //ok
        }
    }

    void testFor7()
    {
        for(;;) {}
    }

    void testFor8()
    {
        for (int i = 0; i > 10; i--) {} //@ALWAYS_FALSE

        int j = 0;
        for (; j > 10; j--) {} //@ALWAYS_FALSE

        int k = 0;
        for (; k > 10; ) {} //@ALWAYS_FALSE
    }

    void testForeach1(List<String> lst)
    {
        for (String s : lst)
        {
        }
    }

    void testForeach2(List<String> lst)
    {
        lst = new ArrayList<>();
        int x = 0;
        for (String s : lst)
        {
            if (s == "1234") {}
            if (s == null) {}
            x += s.length();
        }
    }

    void testForeach3(List<Integer> lst)
    {
        int x = 0;
        for (int i : lst)
        {
            x += i;
        }
        for (Integer i : lst)
        {
            x += i;
        }
    }

    void testForeach4(List<Object> lst, Object other)
    {
        for (Object o : lst)
        {
            if (o == other) {}
            if (o == null) {}
        }
    }
}
