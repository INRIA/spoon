package fr.inria.spoon.dataflow.testcases;

public class TestIf
{
    void testIf1(boolean a, boolean b)
    {
        if (a)
        {
            b = true;
            if (b) {} //@ALWAYS_TRUE
        }
        if (b) {} //ok
    }

    void testIf2(boolean a, boolean b)
    {
        if (a)
        {
            b = true;
        }
        if (b) {} //ok
    }

    void testIf3(boolean a, boolean b, boolean c)
    {
        a = false;
        b = true;
        c = !(a && b || false); //@ALWAYS_FALSE //@ALWAYS_TRUE
        if (c) {} //@ALWAYS_TRUE
    }

    void testIf4(boolean a, boolean c1, boolean c2, boolean c3)
    {
        a = false;
        if (c1)
        {
            a = false;
            if (c2)
            {
                a = false;
                if (c3)
                {
                    a = false;
                }
            }
        }
        if (!a) {} //@ALWAYS_TRUE
    }

    void testIf5(boolean a, boolean c1, boolean c2, boolean c3)
    {
        a = false;
        if (c1)
        {
            a = false;
            if (c2)
            {
                a = true;
                if (c3)
                {
                    a = false;
                }
                a = false;
            }
        }
        if (!a) {} //@ALWAYS_TRUE
    }

    void testIf6(boolean a, boolean c1, boolean c2, boolean c3)
    {
        a = false;
        if (c1)
        {
            a = false;
            if (c2)
            {
                a = true;
                if (c3)
                {
                    a = false;
                }
            }
        }
        if (!a) {} //ok
    }

    void testIf7()
    {
        if (true) {} //some comment //@ALWAYS_TRUE
    }

    void testIf8(boolean a)
    {
        if (a)
        {
            if (a) {} //@ALWAYS_TRUE
        }
    }

    void testIf9(boolean a)
    {
        if (a)
        {
            if (a) //@ALWAYS_TRUE
            {
                if (a) {} //@ALWAYS_TRUE
            }
        }
    }

    void testIf10(boolean c, boolean p)
    {
        p = false;
        if (c)
        {
            p = true;
        }

        if (c)
        {
            if (p) {} //@ALWAYS_TRUE
        }
    }

    void testIf11(boolean c, boolean a)
    {
        a = false;
        if (c)
        {
            a = false;
            if (c) {} //@ALWAYS_TRUE
        }
        if (a) {} //@ALWAYS_FALSE
    }

    void testIf12(boolean c, boolean a)
    {
        a = false;
        if (c)
        {
            a = c; //@ALWAYS_TRUE
            if (a) {} //@ALWAYS_TRUE
            if (c) {} //@ALWAYS_TRUE
        }

        if (c)
        {
            if (a) {} //@ALWAYS_TRUE
        }
    }

    void testIf13()
    {
        if (true) //@ALWAYS_TRUE
        {
            if (true) {} //@ALWAYS_TRUE
        }
        if (true) {} //@ALWAYS_TRUE
    }

    void testIf14(boolean a)
    {
        if (a)
        {
            if (false) {} //@ALWAYS_FALSE
        }
    }

    void testIf15(boolean a, boolean b, boolean p)
    {
        p = false;
        if (!a && b)
        {
            p = true;
        }

        if (a)
        {
            if (p) {} //@ALWAYS_FALSE
            if (!p) {} //@ALWAYS_TRUE
        }
    }

    void testIf16(boolean a, boolean b, boolean c)
    {
        a = false;
        b = true;

        if (c)
        {
            a = false;
            b = false;
        }
        else
        {
            b = false;
        }

        if (a) {} //@ALWAYS_FALSE
        if (b) {} //@ALWAYS_FALSE
    }

    void testIf17(boolean a, boolean b, boolean c)
    {
        a = false;
        b = true;

        if (c)
        {
            a = false;
        }
        else
        {
            b = false;
        }

        if (a) {} //@ALWAYS_FALSE
        if (b) {} //ok
    }

    void testIf18(boolean a, boolean b, boolean c)
    {
        a = false;
        b = false;
        if (c)
        {
        }
        else
        {
            b = false;
            a = true;
        }

        if (a) {} //ok
        if (b) {} //@ALWAYS_FALSE
    }

    void testIf19(boolean c, boolean p)
    {
        p = false;
        if (c)
        {
            p = true;
        }

        if (c)
        {
            if (p) {} //@ALWAYS_TRUE
        }
    }

    void testIf20(boolean c, boolean p)
    {
        if (c)
        {
            p = true;
        }

        if (c)
        {
            if (p) {} //@ALWAYS_TRUE
        }
    }

    void testIf21(boolean a, boolean b, boolean c)
    {
        a = false;
        b = false;
        if (c)
        {
        }
        else
        {
            b = false;
            a = true;
        }

        if (a) {} //ok
        if (b) {} //@ALWAYS_FALSE
    }

    void testIf22(boolean c, boolean a)
    {
        a = false;
        if (c)
        {
        }
        else
        {
            a = false;
        }
        if (a) {} //@ALWAYS_FALSE
    }

    void testIf23(boolean cond, boolean a, boolean b)
    {
        a = false;
        if (cond)
        {
            b = true;
            b = false;
            a = b; //@ALWAYS_FALSE
        }
        else
        {
            a = false;
        }
        if (a) {} //@ALWAYS_FALSE
    }

    void testIf24(boolean a, boolean c)
    {
        if (!a)
        {
        }
        else
        {
            if (c)
            {
                if (a) {} //@ALWAYS_TRUE
            }
        }
        if (a) {} //ok
    }

    void testIf25(boolean b, boolean c)
    {
        if (b)
        {
            c = b; //@ALWAYS_TRUE
            if (c) {} //@ALWAYS_TRUE
        }
        else
        {
            if (c) {} //ok
        }
    }

    void testIf26(boolean b, boolean c)
    {
        if (!!b)
        {
            c = b; //@ALWAYS_TRUE
            if (c) {} //@ALWAYS_TRUE
            if (b) {} //@ALWAYS_TRUE
        }
        else
        {
            if (c) {} //ok
            if (b) {} //@ALWAYS_FALSE
        }
    }

    void testIf27(boolean a, boolean b)
    {
        if (a)
        {
            a = b;
            if (a) {} //ok
            if (b) {} //ok
        }
    }

    void testIf28(boolean b, boolean c)
    {
        if (b)
        {
            c = b; //@ALWAYS_TRUE
            if (c) {} //@ALWAYS_TRUE
        }
        else
        {
            if (c) {} //ok
        }
    }

    void testIf29(boolean c, boolean a)
    {
        if (c)
        {
            a = c; //@ALWAYS_TRUE
            if (a) {} //@ALWAYS_TRUE
        }
        if (c) {}
    }

    void testIf30(boolean a, boolean b)
    {
        if (a)
        {
            a = true;
            if (a) //@ALWAYS_TRUE
            {
                a = b;
                if (a) {} //ok
            }
        }
    }

    void testIf31(boolean a, boolean b)
    {
        if (a)
        {
            a = false;
            if (a) //@ALWAYS_FALSE
            {
                a = b;
                if (a) {} //should be quiet here
            }
        }
    }

    void testIf32(boolean a, boolean c)
    {
        if (a)
        {
            if (c)
            {
                a = false;
            }
        }
        else
        {
        }
        if (a) {} //ok
    }

    void testIf33(boolean a, boolean c, boolean d)
    {
        if (d)
        {
            if (c)
            {
                a = false;
            }
        }
        else
        {
        }
        if (a) {} //ok
    }

    void testIf34(boolean a, boolean c, boolean d)
    {
        if (d)
        {
            if (c)
            {
                a = false;
            }
        }
        else
        {
            a = false;
        }
        if (a) {} //ok
    }

    void testIf35(boolean a, boolean b, boolean c, boolean d)
    {
        if (a)
        {
            b = false;
            if (c)
            {
                a = false;
            }
            else
            {
                b = true;
                if (d)
                {
                    a = true;
                    b = false;
                    d = false;
                }
            }
            if (a) {}
            if (b) {}
            if (c) {}
            if (d) {}
        }
        else
        {
            if (d)
            {
                c = true;
                d = false;
                if (a) {} //@ALWAYS_FALSE
                if (b)
                {
                    b = false;
                }
                else
                {
                    a = true;
                }
            }
        }
        if (a) {}
        if (b) {}
        if (c) {}
        if (d) {}
    }

    void testIf36(boolean a, boolean b, boolean c, boolean d)
    {
        if (a)
        {
            b = true;
            c = true;
        }
        else
        {
            d = false;
            if (c)
            {
                a = true;
            }
        }

        if (d) {}
        if (d && a) {} //@ALWAYS_TRUE
        if (d && a && !c) {} //@ALWAYS_FALSE //@ALWAYS_TRUE

        if (b)
        {
            if (c)
            {
                a = true;
            }
            else if (d) //@ALWAYS_FALSE
            {
                b = true;
            }
        }
    }

    void testIf37()
    {
        boolean x = false;
        if (x) {} //@ALWAYS_FALSE
    }

    void testIf38(boolean c)
    {
        if (c)
        {
            boolean x = false;
            if (x) {} //@ALWAYS_FALSE
        }
        else
        {
            boolean x = true;
            if (x) {} //@ALWAYS_TRUE
        }
    }

    void testIf39(boolean c)
    {
        if (c)
        {
            boolean x = false;
        }
        else
        {
            boolean x = false;
        }
        boolean x = c;
        if (x) {} //ok
    }

    void testIf40(boolean x, boolean c)
    {
        if (c)
        {
            x = false;
        }
        else
        {
            x = false;
        }
        x = c;
        if (x) {} //ok
    }

    void testIf41(boolean c)
    {
        boolean x;
        if (c)
        {
            x = false;
        }
        else
        {
            x = false;
        }
        if (x) {} //@ALWAYS_FALSE

        boolean y;
        if (c)
        {
            y = false;
        }
        else
        {
            y = true;
        }
        if (y) {} //ok
    }

    void testIf42(boolean a)
    {
        if (a)
        {
            boolean b = a; //@ALWAYS_TRUE
            a = b; //@ALWAYS_TRUE
            a = !b; //@ALWAYS_FALSE
            if (b) {} //@ALWAYS_TRUE
        }
        if (a) {} //@ALWAYS_FALSE
    }

    void testIf43(boolean b)
    {
        if (b == true) {} //ok
        if (b == false) {} //ok
        if (b == true && b == false) {} //@ALWAYS_FALSE
        if (b != false || b != true) {} //@ALWAYS_TRUE
    }

    void testIf44(boolean a, boolean b)
    {
        if (a || b)
        {
            if (a) {}
            if (b) {}
        }
        else
        {
            if (a) {} //@ALWAYS_FALSE
            if (b) {} //@ALWAYS_FALSE
        }
    }

    void testIf45(boolean a, boolean b)
    {
        if (a == true) { a = false; }
        if (a == true) {} //@ALWAYS_FALSE

        if (b == false) { b = true; }
        if (b == true) {} //@ALWAYS_TRUE
    }

    void testIf46(boolean a, boolean c)
    {
        a = false;
        c = true;
        if (c) //@ALWAYS_TRUE
        {
            if (a == false) //@ALWAYS_TRUE
            {
                a = true;
                if (a == true) {} //@ALWAYS_TRUE
            }
        }
        if (a == true) {} //@ALWAYS_TRUE
    }
}
