package spoon.test.literal.testclasses;

public class BasedLiteral {

    int i1 = 42;
    int i2 = 0;
    int i3 = 00;
    int i4 = 042;
    int i5 = 0x42;
    int i6 = 0X42;
    int i7 = 0_1_4_2;
    int i8 = 0b1;
    int i9 = 0b1010;

    long l1 = 42L;
    long l2 = 0L;
    long l3 = 00L;
    long l4 = 042L;
    long l5 = 0x42L;
    long l6 = 0X42L;
    long l7 = 0b0L;
    long l8 = 0b1010L;

    float f1 = 42.42f;
    float f2 = 0042.0f;
    float f3 = 00f;
    float f4 = 0f;
    float f5 = 0.0f;
    float f6 = 0x1.2p7f;
    float f7 = 0X1.2p7f;

    double d1 = 0.0;
    double d2 = 0.;
    double d3 = 042.;

    char c1 = 'c';
    String s1 = "hello";
}
