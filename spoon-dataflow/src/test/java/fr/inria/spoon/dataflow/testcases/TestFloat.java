package fr.inria.spoon.dataflow.testcases;

public class TestFloat
{
    void testFloat1(float f1, float f2)
    {
        int x = (int) f1;
        if (x == 32) //ok
        {
            if (x == 32) {} //@ALWAYS_TRUE
        }

        int y = 32;
        y = (int) (f1 + f2);
        if (y == 32) {} //ok
    }

    void testFloat2(float f1)
    {
        f1 = 33.3f;
        if (f1 == 33) {} //ok (we do not calculate floats)
    }

    void testFloat3(float f1)
    {
        if (f1 != f1) {} //ok (aka isNaN)
        if (f1 == f1) {} //ok
    }

    void testFloatCast1(float f1)
    {
        char x = (char) f1;
        if (x >= 0) {} //@ALWAYS_TRUE
        if (x > 70000) {} //@ALWAYS_FALSE
    }

    void testFloatCast2(float f1, float f2)
    {
        float f3 = (float) (f1 + (float) f2);
        if (f3 == 42.0f) {} //ok
    }

    void testFloatCast3(float f1)
    {
        char x = 5;
        char y = (char)(x + (int) f1);
        if (y == -1) {} //@ALWAYS_FALSE
    }

    void testFloat4(int i, float f)
    {
        if (i + f == 300) {} //ok
        if (i / f == 300) {} //ok
    }

    void testFloat5()
    {
        float f = 13.3f;
        if (f == 13.3f) {}
        Float f2 = 13.3f;
        if (f2 == null) {} //@ALWAYS_FALSE
        if (f2 == 13.3f) {}
    }

    void testFloat6(boolean cond)
    {
        float f1;
        if (cond)
        {
            f1 = 33.3f;
        }
        else
        {
            f1 = 22.2f;
        }
        if (f1 > 3) {}
    }

    void testFloat7()
    {
        byte n = (byte) 12.3;
        Integer i1 = (int) 12.3f;
        Float f1 = 12.3f;
        Float f2 = (float) 12;
        Float f3 = (float) 12.3;
        float f4 = 10.0f;
        Float f5 = f4;
    }

    void testFloat8()
    {
        Float a = 5.0f;
        Float b = 5.0f;
        Float c = 5.0f;
        if (a + b + c == 15.0f) {}
    }

    void testFloat9()
    {
        float a = 0.0f;
        float b = 1;
        Float c = 2.0f;
        float d = 3;
        if (a + b + c + d == 6) {}
    }

    void testFloat10()
    {
        char c = (char) 142.0f;
        if (c > -1) {} //@ALWAYS_TRUE
    }

    void testFloat11()
    {
        float[] arr = new float[3];
        arr[0] = 2.0f;
        if (arr[0] == 2.0f) {}
    }

    void testFloatUnary(float f1, float f2)
    {
        float f3 = -f1;
        if (f3 == f2) {} //ok
        float f4 = -((float) 42);
    }

    void testFloatIte(boolean cond)
    {
        float f = 3.33f;
        if (cond)
        {
            f = 4.44f;
        }
        else
        {
            f = 5.55f;
        }
    }
}
