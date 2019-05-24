package fr.inria.spoon.dataflow.testcases;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestTry
{
    void testTry1()
    {
        int a = 0;
        int b = 0;
        int c = 0;
        try
        {
            a = 1;
        }
        catch (RuntimeException e)
        {
            a = 2;
            b = 1;
        }
        if (a == 0) {} //ok
        if (a == 1) {} //ok
        if (a == 2) {} //ok
        if (b == 1) {} //ok
        if (c == 0) {} //@ALWAYS_TRUE
    }

    void testTry2()
    {
        int a = 0;
        int c = 0;
        try
        {
            a = 1;
        }
        catch (RuntimeException e)
        {
            a = 2;
        }
        finally
        {
            a = 42;
            c = 42;
        }
        if (a == 0) {} //@ALWAYS_FALSE
        if (a == 1) {} //@ALWAYS_FALSE
        if (a == 2) {} //@ALWAYS_FALSE
        if (a == 42) {} //@ALWAYS_TRUE
        if (c == 42) {} //@ALWAYS_TRUE
    }

    void testTry3()
    {
        int a = 0;
        int b = 0;
        try
        {
            a = 1;
        }
        catch (ArithmeticException e)
        {
            b = 1;
        }
        catch (RuntimeException e)
        {
        }
        if (a == 0) {} //ok
        if (b == 0) {} //ok
    }

    void testTry4()
    {
        int a = 5;
        try
        {
            if (a == 5) {} //@ALWAYS_TRUE
        }
        catch (RuntimeException e)
        {
        }
    }

    void testTryWithResource1(String path)
    {
        int a = 0;
        int b = 0;
        int c = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            if (br == null) {} //@ALWAYS_FALSE
            a = 5;
        }
        catch (FileNotFoundException e)
        {
            b = 5;
        }
        catch (IOException e)
        {
        }
        finally
        {
            b = 55;
            c = 55;
        }
        if (a == 0) {}
        if (a == 5) {}
        if (a == 55) {}
        if (b == 55) {} //@ALWAYS_TRUE
        if (c == 55) {} //@ALWAYS_TRUE
    }

    void testThrow1()
    {
        boolean cond = true;
        if (cond) //@ALWAYS_TRUE
        {
            throw new RuntimeException();
        }
        if (cond) {} //@ALWAYS_FALSE
    }

    void testThrow2()
    {
        boolean cond = true;
        try
        {
            throw new RuntimeException();
        }
        catch (RuntimeException e)
        {
        }
        if (cond) {} //@ALWAYS_TRUE
    }
}
