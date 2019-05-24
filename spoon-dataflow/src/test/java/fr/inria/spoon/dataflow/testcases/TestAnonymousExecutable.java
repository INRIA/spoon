package fr.inria.spoon.dataflow.testcases;

public class TestAnonymousExecutable
{
    {
        int a = 0;
        if (a == 0) {} //@ALWAYS_TRUE
    }

    static
    {
        int a = 0;
        if (a == 0) {} //@ALWAYS_TRUE
    }
}
