package fr.inria.spoon.dataflow.testcases;

public class TestSanity
{
    void testEmpty()
    {
    }

    void f1()
    {
        if (true) {} //@ALWAYS_TRUE
    }

    void f2()
    {
        if (false) {} //@ALWAYS_FALSE
    }
}
