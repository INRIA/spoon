package noclasspath;

import java.util.HashMap;
import java.util.Map;

public class LiteralInForEach {
    private static Map<String, UnknownClass> map = new HashMap<>();

    public static void main(final String[] args) {
        booleanLiteral();
        byteLiteral();
        shortLiteral();
        charLiteral();
        intLiteral();
        floatLiteral();
        longLiteral();
        doubleLiteral();
        stringLiteral();
    }

    private static void booleanLiteral() {
        map.forEach((key, value) -> {
            boolean b0 = true;
            boolean b1 = false;
        });
    }

    private static void byteLiteral() {
        map.forEach((key, value) -> {
            byte b = 3;
        });
    }

    private static void shortLiteral() {
        map.forEach((key, value) -> {
            short s = 5;
        });
    }

    private static void charLiteral() {
        map.forEach((key, value) -> {
            char c0 = 'c';
            char c1 = 2;
        });
    }

    private static void intLiteral() {
        map.forEach((key, value) -> {
            int i = 7;
        });
    }

    private static void floatLiteral() {
        map.forEach((key, value) -> {
            float f0 = 9f;
            float f1 = 9.0f;
        });
    }

    private static void longLiteral() {
        map.forEach((key, value) -> {
            long l0 = 11L;
            long l1 = 11l;
        });
    }

    private static void doubleLiteral() {
        map.forEach((key, value) -> {
            double d0 = 13d;
            double d1 = 13D;
            double d2 = 13.0d;
            double d3 = 13.0D;
            double d4 = 13.0;
        });
    }

    private static void stringLiteral() {
        map.forEach((key, value) -> {
            String s = "spoon";
        });
    }
}
