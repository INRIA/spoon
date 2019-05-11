package fr.inria.spoon.dataflow.testcases;

public class TestGenerics
{
    <T extends Integer> void testGeneric1(T t)
    {
        if (t == null)
        {
            if (t == null) {} //@ALWAYS_TRUE
        }
    }
}
