package spoon.test.enums.testclasses;

public enum EnumWithMembers {
    ONE,
    TWO,
    THREE;

    private static int len = -1;

    public static void f() {
        len = 44;
    }
}
