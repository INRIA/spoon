package fr.inria.spoon.dataflow.testcases;

public class TestTypes
{
    void testTypesBoundaries1(byte b)
    {
        if (b == 127) {} //ok
        if (b == 128) {} //@ALWAYS_FALSE
        if (b == -127) {} //ok
        if (b == -128) {} //ok
        if (b == -129) {} //@ALWAYS_FALSE
        if (b < 0) {} //ok
        if (b > 200) {} //@ALWAYS_FALSE
        if (b < -200) {} //@ALWAYS_FALSE
    }

    void testTypesBoundaries2(byte b, short s, int i, long l, char c)
    {
        if (b == 127) {} //ok
        if (b == 128) {} //@ALWAYS_FALSE
        if (b > 127) {} //@ALWAYS_FALSE
        if (b == -128) {} //ok
        if (b == -129) {} //@ALWAYS_FALSE
        if (b < 129) {} //@ALWAYS_TRUE
        if (b < -128) {} //@ALWAYS_FALSE
        if (b == 0) {} //ok

        if (s == -32768) {} //ok
        if (s == -32769) {} //@ALWAYS_FALSE
        if (s <= -32769) {} //@ALWAYS_FALSE
        if (s < -32768) {} //@ALWAYS_FALSE
        if (s == 0) {} //ok
        if (s == 32767) {} //ok
        if (s == 32768) {} //@ALWAYS_FALSE
        if (s < 32768) {} //@ALWAYS_TRUE
        if (s > 32767) {} //@ALWAYS_FALSE

        if (i == -2147483648) {} //ok
        if (i == -2147483649L) {} //@ALWAYS_FALSE
        if (i == -3333483649L) //@ALWAYS_FALSE
            if (i < 33L) {} //ok
        if (i > 2147483647) {} //@ALWAYS_FALSE
        if (i > 2147483646) {} //ok
        if (i < -2147483647) {} //ok
        if (i < -2147483648) {} //@ALWAYS_FALSE
        if (i > 10000) {} //ok
        if (i == 0) {} //ok
        if (i == 2147483647) {} //ok

        if (c == 0) {} //ok
        if (c < 0) {} //@ALWAYS_FALSE
        if (c > 65536) {} //@ALWAYS_FALSE
        if (c == 65536) {} //@ALWAYS_FALSE
        if (c == 65535) {} //ok
        if (c == 42000) {} //ok
        if (c == 30000) {} //ok
        if (c == 33000) {} //ok
        if (c == 'a') {} //ok
    }

    void testTypesBoundaries3(byte b, short s, int i, long l, char c)
    {
        b = 42;
        if (b == 42) {} //@ALWAYS_TRUE
        if (42 == b) {} //@ALWAYS_TRUE
        if (b == 42L) {} //@ALWAYS_TRUE
        if (42L == b) {} //@ALWAYS_TRUE
        if (b == 123) {} //@ALWAYS_FALSE
        b = (byte) 42L;
        if (b == 42) {} //@ALWAYS_TRUE
        if (b == 42L) {} //@ALWAYS_TRUE
        if (42L == b) {} //@ALWAYS_TRUE

        if (b == ((byte) 42L)) {} //@ALWAYS_TRUE
        if (b == i) {} //ok
        if (b == s) {} //ok
        if (b == l) {} //ok

        if (42 == 42L) {} //@ALWAYS_TRUE
        if (0b001 == 3) {} //@ALWAYS_FALSE
        if (0b11 == 3) {} //@ALWAYS_TRUE

        if (i == 42) {} //ok
        i = 42;
        s = (short) i;
        l = 42L;
        if (i == 42) {} //@ALWAYS_TRUE
        if (42 == i) {} //@ALWAYS_TRUE
        if (b == i) {} //@ALWAYS_TRUE
        if (i == b) {} //@ALWAYS_TRUE
        if (s == b) {} //@ALWAYS_TRUE
        if (l == s) {} //@ALWAYS_TRUE
        if (l == l) {} //@ALWAYS_TRUE

        b = -124;
        if (b == -124) {} //@ALWAYS_TRUE
        if (-124 == b) {} //@ALWAYS_TRUE

        b = (byte) 128;
        if (b == 127) {} //@ALWAYS_FALSE
        if (b == 0) {} //@ALWAYS_FALSE
        if (b == -128) {} //@ALWAYS_TRUE

        b = (byte) 10000L;
        if (b == 16) {} //@ALWAYS_TRUE
        b = (byte) 10000;
        if (b == 16) {} //@ALWAYS_TRUE

        b = (byte) (short) (int) 10000L;
        if (b == 16) {} //@ALWAYS_TRUE

        i = 500;
        b = (byte) i;
        if (b > 200) {} //@ALWAYS_FALSE
        if (b < -200) {} //@ALWAYS_FALSE
        if (b == -12) {} //@ALWAYS_TRUE

        i = 500;
        s = (byte) i;
        if (s == -12) {} //@ALWAYS_TRUE

        byte bb = -123;
        short ss = (short) bb;
        if (ss == bb) {} //@ALWAYS_TRUE
        if (bb == ss) {} //@ALWAYS_TRUE
        if (ss == -123) {} //@ALWAYS_TRUE
    }

    void testTypesBoundaries4()
    {
        int j;
        j = (char) 100000;
        if (j == 34464) {} //@ALWAYS_TRUE
        if (j == 1696) {} //@ALWAYS_FALSE
        if (j == 100000) {} //@ALWAYS_FALSE
        if (j == -31072) {} //@ALWAYS_FALSE
    }

    void testTypesBoundaries5()
    {
        int j = (char) 100000;
        if (j == 34464) {} //@ALWAYS_TRUE
        if (j == 1696) {} //@ALWAYS_FALSE
        if (j == 100000) {} //@ALWAYS_FALSE
        if (j == -31072) {} //@ALWAYS_FALSE
    }

    void testTypesBoundaries6(byte b, char c, int i, short s, long l)
    {
        if (c == b) {} //ok
        if (c == i) {} //ok

        s = -16;
        c = (char) s;
        if (c == 65520) {} //@ALWAYS_TRUE
        if (c == -16) {} //@ALWAYS_FALSE

        s = 16;
        c = (char) s;
        if (c == 16) {} //@ALWAYS_TRUE

        c = 123;
        b = (byte) c;
        if (b == 123) {} //@ALWAYS_TRUE

        c = 1000;
        b = (byte) c;
        if (b == -24) {} //@ALWAYS_TRUE
        if (c == 1000) {} //@ALWAYS_TRUE
        if (b > 128) {} //@ALWAYS_FALSE
        if (b < -129) {} //@ALWAYS_FALSE

        i = 12345678;
        if (c == i) {} //@ALWAYS_FALSE
        c = (char) i;
        if (c == 24910) {} //@ALWAYS_TRUE
        if (c > 70000) {} //@ALWAYS_FALSE
        if (c < 0) {} //@ALWAYS_FALSE
        if (i == 12345678) {} //@ALWAYS_TRUE

        b = -16;
        c = (char) b;
        if (c == 65520) {} //@ALWAYS_TRUE
        if (c >= 0) {} //@ALWAYS_TRUE
    }

    void testChar1()
    {
        char c = 'x';
        if (c == 120) {} //@ALWAYS_TRUE
        if (c == 'x') {} //@ALWAYS_TRUE

        c = '\u216C';
        if (c == 8556) {} //@ALWAYS_TRUE

        char c1 = '9';
        int i = c1 - '0';
        if (i == 9) {} //@ALWAYS_TRUE
    }

    void testChar2(char c)
    {
        if (c >= 'z')
        {
            if (c < 'b') {} //@ALWAYS_FALSE
        }
        else if (c == 'Z') {} //ok
    }

    void testChar3(int i, int j)
    {
        i = '9' - 48;
        if (i == 9) {} //@ALWAYS_TRUE

        j = -400;
        if (j == 64136) {} //@ALWAYS_FALSE
        if (j == -400) {} //@ALWAYS_TRUE

        j = (char) 100000;
        if (j == 34464) {} //@ALWAYS_TRUE

        j = (char) 100000L;
        if (j == 34464) {} //@ALWAYS_TRUE

        j = (char) 'z';
        if (j == 122) {} //@ALWAYS_TRUE

        j = ((int)'z');
        if (j == 122) {} //@ALWAYS_TRUE

        j = (char)-400;
        if (j == 65136) {} //@ALWAYS_TRUE

        j = (char)-'z';
        if (j == 65414) {} //@ALWAYS_TRUE
    }

    void testChar4(char c)
    {
        if (c == -1) {} //@ALWAYS_FALSE
        if (c == 42000) {} //ok
        if (c == 420000) {} //@ALWAYS_FALSE
    }

    void testChar5(char c)
    {
        short s = (short) c;
        if (s >= 0) {} //ok
        if (s < 0) {} //ok
        if (s > 42000) {} //@ALWAYS_FALSE
        if (s < -42000) {} //@ALWAYS_FALSE
        if (c > 42000) {} //ok
        if (c < 0) {} //@ALWAYS_FALSE
    }

    void testChar6(char x)
    {
        char c0 = 0;
        char c1 = 1000;
        char c2 = (char)-1000;
        if (c1 > c2) {} //@ALWAYS_FALSE
        if (c1 > -1000) {} //@ALWAYS_TRUE
        if (c0 > c1) {} //@ALWAYS_FALSE
        if (x >= c0) {} //@ALWAYS_TRUE
        if (c1 <= -100) {} //@ALWAYS_FALSE
        if (x <= -100) {} //@ALWAYS_FALSE
        if (x + 1 == 100)
        {
            if (x == 200) {} //@ALWAYS_FALSE
        }
    }

    void testChar7(char x, long longg)
    {
        long long1 = 12222222222222222L;
        if (x == longg) {}
        if (long1 == x) {} //@ALWAYS_FALSE
        char c1 = 3;
        long l2 = 3L;
        if (c1 == longg) {}
        if (c1 == 3L) {} //@ALWAYS_TRUE
        if (c1 == l2) {} //@ALWAYS_TRUE
    }

    void testChar8()
    {
        int i = (char) 100000;
        if (i == 100000) {} //@ALWAYS_FALSE
    }

    void testLong1()
    {
        int i = 1234567;
        long res = (long) i;
        if (res == 1234567) {} //@ALWAYS_TRUE
    }

    void testOverflows1()
    {
        byte b = 127;
        int i = b + 1; //b upcasts to int here
        if (i == 128) {} //@ALWAYS_TRUE
    }

    void testOverflows2(byte b)
    {
        if (b > 3)
        {
            if (b + 1 > 3) {} //@ALWAYS_TRUE
        }

        b = 127;

        if (b > 3) //@ALWAYS_TRUE
        {
            if (b + 1 > 3) {} //@ALWAYS_TRUE
        }
    }

    void testOverflows3(short s)
    {
        if (s > 3)
        {
            if (s + 1 > 3) {} //@ALWAYS_TRUE
        }

        s = 0x7fff;

        if (s > 3) //@ALWAYS_TRUE
        {
            if (s + 1 > 3) {} //@ALWAYS_TRUE
        }
    }

    void testOverflows4(int i)
    {
        if (i > 3) //ok
        {
            if (i + 1 > 3) {} //ok
        }

        i = 0x7fffffff;
        if (i > 3) {} //@ALWAYS_TRUE
        {
            if (i + 1 > 3) {} //@ALWAYS_FALSE
        }
    }
}
