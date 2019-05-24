package fr.inria.spoon.dataflow.testcases;

public class TestReturn
{
    void testReturn1(boolean c)
    {
        int x = 1;
        if (c)
        {
            x = 2;
            return;
        }

        if (c) {} //@ALWAYS_FALSE
        if (x == 1) {} //@ALWAYS_TRUE
        if (x == 2) {} //@ALWAYS_FALSE
    }

    int testReturn2(boolean c1, boolean c2)
    {
        int x = 1;
        if (c1)
        {
            x = 2;
            if (c2)
            {
                x = 3;
                return 1;
            }
            if (c1) {} //@ALWAYS_TRUE
            if (c2) {} //@ALWAYS_FALSE
        }

        if (x == 1) {} //ok
        if (x == 2) {} //ok
        if (x == 3) {} //@ALWAYS_FALSE

        return 0;
    }

    void testReturn3(boolean c1, boolean c2)
    {
        int x = 1;
        if (c1)
        {
            x = 2;
            if (c2)
            {
                x = 3;
                return;
            }
        }

        if (x == 3) {} //@ALWAYS_FALSE
    }

    void testReturn4(boolean c)
    {
        int x = 1;
        if (c)
        {
            x = 2;
        }
        else
        {
            return;
        }
        if (c) {} //@ALWAYS_TRUE
        if (x == 2) {} //@ALWAYS_TRUE
    }

    void testReturn5(boolean c1, boolean c2)
    {
        if (c1)
        {
            if (c2)
            {
                return;
            }
            if (c2) {} //@ALWAYS_FALSE
        }
        if (c2) {} //ok
    }

    void testReturn6(boolean c1, boolean c2, boolean c3)
    {
        boolean noreturn = false;
        int x = 0;
        if (c1)
        {
            if (c2)
            {
                x = 2;
                if (c3)
                {
                    noreturn = true; /* return */
                }
                else
                {
                    x = 1;
                }
                if (c3 && !noreturn) {} //@ALWAYS_FALSE
            }
            else
            {
                if (c3 && !noreturn) {} //@ALWAYS_TRUE
                if (x == 1 && !noreturn) {} //@ALWAYS_FALSE
                noreturn = true; /* return */
            }
            if (c3 && !noreturn) {} //@ALWAYS_FALSE
        }
        else
        {
            x = 2;
        }
    }

    void testReturn7(boolean c1, boolean c2, boolean c3)
    {
        int x = 0;
        if (c1)
        {
            if (c2)
            {
                x = 2;
                if (c3)
                {
                    return;
                }
                else
                {
                    x = 1;
                }
                if (c3) {} //@ALWAYS_FALSE
            }
            else
            {
                if (c3) {} //ok
                if (x == 1) {} //@ALWAYS_FALSE
                return;
            }
            if (c3) {} //@ALWAYS_FALSE
        }
        else
        {
            x = 2;
        }
    }

    void testReturn8(boolean c1, boolean c2)
    {
        if (c1)
        {
            if (c2)
            {
                return;
            }
            if (!c1) {} //@ALWAYS_FALSE
            if (!c2) {} //@ALWAYS_TRUE
            return;
        }
        if (c1) {} //@ALWAYS_FALSE
        if (!c1) {} //@ALWAYS_TRUE
    }

    void testReturn9(int a, boolean c)
    {
        if (a < 0)
        {
            return;
        }
        if (c)
        {
            return;
        }
        if (a == -1) {} //@ALWAYS_FALSE
        if (c) {} //@ALWAYS_FALSE
    }

    void testReturn10(int a)
    {
        if (a > +0)
        {
            return;
        }
        if (a > -1) {} //ok
        if (a > 0) {} //@ALWAYS_FALSE
    }

    void testReturn11(int a)
    {
        if (a > 0)
        {
            return;
        }
        else
        {
        }
        if (a > -1) {} //ok
        if (a > 0) {} //@ALWAYS_FALSE
    }

    void testReturn12(int a)
    {
        if (a <= 0)
        {
        }
        else
        {
            return;
        }
        if (a > -1) {} //ok
        if (a > 0) {} //@ALWAYS_FALSE
    }

    void testReturn13(int a, boolean c)
    {
        if (a > 0)
        {
            if (c)
            {
                return;
            }
            if (c) {} //@ALWAYS_FALSE
        }
        else
        {
            if (c) {} //ok
        }
        if (a > 0) {} //ok
    }

    void testReturn14(int a)
    {
        if (a < 0)
        {
            return;
        }
        if (a < -2) //@ALWAYS_FALSE
        {
            return;
        }
        if (a > 1) //ok
        {
            return;
        }
        if (a == 7) //@ALWAYS_FALSE
        {
            return;
        }
        if (a == 7) {} //@ALWAYS_FALSE
        if (a == 0) {} //ok
    }

    boolean testReturn15(int a, int b)
    {
        if (a == 0 && b == 0)
        {
            return false;
        }
        if (a == 0) {} //ok
        return true;
    }

    boolean testReturn16(int a, int b)
    {
        if (a == 0 || b == 0)
        {
            return false;
        }
        if (a == 0) {} //@ALWAYS_FALSE
        return true;
    }

    boolean testReturn17(int y)
    {
        int x = 1;
        return x > 5 && y > 2; //@ALWAYS_FALSE
    }

    boolean testReturn18()
    {
        int x = 3;
        return x != 0; //@ALWAYS_TRUE
    }

    boolean testReturn19(boolean c)
    {
        if (c) return true; //ok
        return false; //ok
    }
}
