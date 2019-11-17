package fr.inria.spoon.dataflow.testcases;

public class TestArray
{
    void testArray1()
    {
        int[] arr = new int[3];
        if (arr == null) {} //@ALWAYS_FALSE
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        if (arr[0] == 1) {} //@ALWAYS_TRUE
        if (arr[1] == 2) {} //@ALWAYS_TRUE
        if (arr[2] == 3) {} //@ALWAYS_TRUE
    }

    void testArray2(int x)
    {
        int[] arr = new int[3];
        int idx = 0;
        arr[idx] = 1;
        arr[1] = x;
        if (arr[0] == 1) {} //@ALWAYS_TRUE
        if (arr[0 + 3 - 2 + 0] == x) {} //@ALWAYS_TRUE
    }

    void testArray3(int[] arr)
    {
        arr[4] = 42;
        if (arr[1 + 1 + 1 + 1] == 42) {} //@ALWAYS_TRUE
    }

    void testArray4(boolean cond)
    {
        int[] arr = new int[2];
        int[] other = null;
        if (arr == null) {} //@ALWAYS_FALSE
        if (arr[0] == 0) {}
        if (cond)
        {
            arr = other;
        }
        if (arr == other) {} //ok
        if (arr == null) {} //ok
    }

    void testArray5()
    {
        int arr[] = new int[2];
        arr[0] = 1;
        arr[1] = 2;
        int other[] = new int[2];
        other[0] = 10;
        other[1] = 20;
        arr = other;
        arr[1] = 142;
        if (arr[0] == 10) {} //@ALWAYS_TRUE
        if (arr[1] == 142) {} //@ALWAYS_TRUE
        if (other[0] == 10) {} //@ALWAYS_TRUE
        if (other[1] == 142) {} //@ALWAYS_TRUE
    }

    void testArray6(boolean cond)
    {
        int arr[] = new int[2];
        arr[0] = 0;
        arr[1] = 0;
        if (cond)
        {
            arr[1] = 5;
        }
        arr[0] = 6;
        if (cond)
        {
            if (arr[0] == 6) {} //@ALWAYS_TRUE
            if (arr[1] == 5) {} //@ALWAYS_TRUE
        }
        if (arr[0] == 6) {} //@ALWAYS_TRUE
        if (arr[1] == 5) {} //ok
    }

    void testArray7()
    {
        char arr1[] = new char[]{'f', 'z', 'x'};
        arr1[0] = 'a';
        if (arr1[0] == 'a') {} //@ALWAYS_TRUE

        Object arr2[] = new Object[3];
        arr2[0] = new Object();
        arr2[1] = null;
        if (arr2[0] == null) {} //@ALWAYS_FALSE
        if (arr2[1] == null) {} //@ALWAYS_TRUE
        if (arr2[2] == null) {}
    }

    void testArray8()
    {
        int mat[][] = new int[2][2];
        mat[0][0] = 1;
        if (mat[0][0] == 1) {} //@ALWAYS_TRUE
        if (mat[0] == null) {} //@ALWAYS_FALSE
        mat[1] = null;
        if (mat[1] == null) {} //@ALWAYS_TRUE
    }

    void testArray9()
    {
        int[][] mat =
        {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        if (mat == null) {} //@ALWAYS_FALSE
        if (mat[0] == null) {} //@ALWAYS_FALSE
        if (mat[0][0] == 1) {} //@ALWAYS_TRUE
        if (mat[2][1] == 8) {} //@ALWAYS_TRUE
    }

    void testArray10()
    {
        int[][][] cube =
        {
            {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
            {{10, 11, 12}, {13, 14, 15}, {16, 17, 18}},
            {{19, 20, 21}, {22, 23, 24}, {25, 26, 27}}
        };
        if (cube[0][0][0] == 1) {} //@ALWAYS_TRUE
        if (cube[1][2][2] == 18) {} //@ALWAYS_TRUE
        if (cube[1][1][1] == 14) {} //@ALWAYS_TRUE
        if (cube[2][1][0] == 22) {} //@ALWAYS_TRUE
        if (cube[0][1][2] == 6) {} //@ALWAYS_TRUE
    }

    void testArray11()
    {
        int[] arr = {0, 0, 0, 0};
        byte i = 3;
        arr[i] = 4;
        arr[(int)3L] = 4;
        if (arr[i] == 4) {} //@ALWAYS_TRUE
        if (arr[(int) 3L] == 4) {} //@ALWAYS_TRUE
        arr[i] = i;
        if (arr[i] == i) {} //@ALWAYS_TRUE
    }

    void testArray12()
    {
        int i = 0;
        int[] arr = {1, 2, 3, 4, 5};
        if (arr[i++] == 1) {} //@ALWAYS_TRUE
        if (arr[i] == 2) {} //@ALWAYS_TRUE
        if (i == 1) {} //@ALWAYS_TRUE
        if (++arr[i] == 3) {} //@ALWAYS_TRUE
    }

    void testArray13()
    {
        int i = 2;
        int[] arr = new int[++i];
        if (i == 3) {} //@ALWAYS_TRUE
    }

    void testArray14()
    {
        int i = 0;
        int[] arr = {1, 2, 3, 4, 5};
        arr[i += 1]++;
        if (arr[0] == 1) {} //@ALWAYS_TRUE
        if (i == 0) {} //@ALWAYS_FALSE
        if (i == 1) {} //@ALWAYS_TRUE
    }

    void testArray15()
    {
        int i = 0;
        int[] arr = {1, 2, 3, 4, 5};
        arr[i++]++;
        if (arr[0] == 2) {} //@ALWAYS_TRUE
        if (i == 0) {} //@ALWAYS_FALSE
        if (i == 1) {} //@ALWAYS_TRUE
    }

    void testArray16()
    {
        int i = 0;
        int[] arr = {1, 2, 3, 4, 5};
        arr[++i]++;
        if (arr[0] == 1) {} //@ALWAYS_TRUE
        if (arr[1] == 3) {} //@ALWAYS_TRUE
        if (i == 0) {} //@ALWAYS_FALSE
        if (i == 1) {} //@ALWAYS_TRUE
    }

    void testArray17()
    {
        int[] arr = {1, 2, 3, 4, 5};
        if (arr['b' - 'a'] == 2) {} //@ALWAYS_TRUE
        byte b = 3;
        if (arr[b] == 4) {} //@ALWAYS_TRUE
    }

    void testArray18(int[] arr)
    {
        if (arr['a'] == 42) {} //ok
        byte b = 3;
        if (arr[b] == 42) {} //ok
    }

    public int[] publicArrayField;
    void testArrayField1()
    {
        publicArrayField[3] = 3;
        if (publicArrayField[3] == 3) {} //@ALWAYS_TRUE

        if (publicArrayField[3] + 1 > 2) {} //@ALWAYS_TRUE

        if (publicArrayField[4] == 5)
        {
            if (publicArrayField[2 + 2] == 5) {} //@ALWAYS_TRUE
        }

        publicArrayField[3]++;
        if (publicArrayField[3] == 4) {} //@ALWAYS_TRUE
    }

    private class DateTime
    {
        int year, month, day, hour, minute, second;
    }
    static boolean isLeapYear(int year)
    {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }
    boolean testOneFamousBug(DateTime time)
    {
        int[] kDaysInMonth = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if (time.year < 1 || time.year > 9999 ||
                time.month < 1 || time.month > 12 ||
                time.day < 1 || time.day > 31 ||
                time.hour < 0 || time.hour > 23 ||
                time.minute < 0 || time.minute > 59 ||
                time.second < 0 || time.second > 59)
        {
            return false;
        }
        if (time.month == 2 && isLeapYear(time.year))
        {
            return time.month <= kDaysInMonth[time.month] + 1; //@ALWAYS_TRUE
        }
        else
        {
            return time.month <= kDaysInMonth[time.month]; //@ALWAYS_TRUE
        }
    }

    void testArrayElementsCast1()
    {
        long arr1[] = {1, 2, 3L, 4L};
        if (arr1[0] == 1) {} //@ALWAYS_TRUE
        if (arr1[1] == 2) {} //@ALWAYS_TRUE
        if (arr1[2] == 3) {} //@ALWAYS_TRUE
        if (arr1[3] == 4) {} //@ALWAYS_TRUE

        long arr2[] = {(byte)1000, (int)2, (char)3, 4};
        if (arr2[0] == 1000) {} //@ALWAYS_FALSE
        if (arr2[1] == 2) {} //@ALWAYS_TRUE
        if (arr2[2] == 3) {} //@ALWAYS_TRUE
        if (arr2[3] == 4) {} //@ALWAYS_TRUE
    }

    void testArrayParam1(int[] arr)
    {
        if (arr == null) {} //ok
        arr[0] = 5;
        if (arr[0] == 5) {} //@ALWAYS_TRUE
        if (arr[1] == 5) {} //ok
    }

    void testArrayOperations1()
    {
        int[] arr1 = {1, 2, 3};
        arr1[0]++;
        arr1[0]++;
        arr1[0]++;
        if (arr1[0] == 4) {} //@ALWAYS_TRUE
    }

    void testArrayOperations2()
    {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {4, 5, 6};
        if (arr1[0] + arr2[1] == 6) {} //@ALWAYS_TRUE
    }

    void testArrayOperations3()
    {
        int[] arr1 = {1, 2, 3};
        arr1[0] += 1;
        if (arr1[0] == 2) {} //@ALWAYS_TRUE
    }

    void testArrayOperations4()
    {
        int[] arr1 = {1, 2, 3};
        if (arr1[0]++ == 1) {}  //@ALWAYS_TRUE
        if (arr1[0] == 2) {} //@ALWAYS_TRUE
        if (++arr1[0] == 3) {} //@ALWAYS_TRUE
        if (arr1[0] == 3) {} //@ALWAYS_TRUE
    }
}
