package fr.inria.spoon.dataflow.testcases;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TestLambda
{
    void testLambda1(List<String> list)
    {
        list.forEach(x -> System.out.println(x + "123"));
        list.forEach((String x) -> System.out.println(x));
    }

    void testLambda2()
    {
        int[] arr = {1, 2, 3, 4};
        Stream.of(arr).forEach((x) -> System.out.println(x));
    }

    void testLambda3(List<String> list)
    {
        int a = 5; // effectively final
        list.forEach(x ->
        {
            if (a == 5) { System.out.println(x); }; //@ALWAYS_TRUE
        });
    }

    void testLambda4(List<Integer> list)
    {
        list.forEach(x ->
        {
            if (x == 42)
            {
                if (x == 42) {} //@ALWAYS_TRUE
            }
        });
    }

    void testLambda5(List<Integer> list)
    {
        list.forEach(x ->
        {
            switch (x)
            {
                case 1:
                    break;
                case 2:
                    if (x == 2) {} //@ALWAYS_TRUE
                    break;
                case 3:
                    break;
            }
            if (x == 2) {} //ok
        });
    }

    void testLambda6(List<Integer> list)
    {
        int a = 5; // effectively final
        list.forEach(x ->
        {
            if (a == 5) { System.out.println(x); }; //@ALWAYS_TRUE
            if (x == 0) {} //ok
        });
    }

    void testLambda7()
    {
        final int a = 5;
        Consumer<Integer> consumer = (i) ->
        {
            if (a == 5) //@ALWAYS_TRUE
            {
                return;
            }
            if (a == 5) {} //@ALWAYS_FALSE
            System.out.println(i);
        };
        if (a == 5) {} //@ALWAYS_TRUE
    }

    void testLambda8(List<Integer> list)
    {
        int[] arr = {0};
        list.forEach(e -> arr[0] += e);
        if (arr[0] == 0) {} //FIXME: //@ALWAYS_TRUE
    }

    int z;
    void testLambdaAssignment1()
    {
        z = 0;
        BiFunction<Integer,Integer,Integer> sum = (v1, v2) -> z = v1 + v2;
        if (z == 0) {} //@ALWAYS_TRUE
        if (z == 5) {} //@ALWAYS_FALSE
        sum.apply(1, 2);
        if (z == 0) {} //FIXME: //@ALWAYS_TRUE
        if (z == 5) {} //FIXME: //@ALWAYS_FALSE
    }

    void testLambdaAssignment2()
    {
        z = 0;
        BiFunction<Integer,Integer,Integer> sum = (v1, v2) -> z = v1 + v2;
        if (z == 0) {} //@ALWAYS_TRUE
        if (z == 5) {} //@ALWAYS_FALSE
        z = 1;
        sum.apply(1, 2);
        if (z == 0) {} //FIXME: //@ALWAYS_FALSE
        if (z == 5) {} //FIXME: //@ALWAYS_FALSE
    }

    void testLambdaAssignment3()
    {
        z = 0;
        Consumer<Integer> consumer = (i) ->
        {
            if (z == 0) {} //ok
        };
        consumer.accept(42);
        z = 1;
        consumer.accept(42);
        if (z == 0) {} //FIXME: //@ALWAYS_FALSE
    }
}
