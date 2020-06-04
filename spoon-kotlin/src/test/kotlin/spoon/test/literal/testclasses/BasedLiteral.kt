package spoon.test.literal.testclasses

class BasedLiteral {
    val i1 = 42
    val i2 = 0
    val i3 = 0x42
    val i4 = 0X43
    val i5 = 1_4_2
    val i6 = 0b1
    val i7 = 0b1010

    val l1: Long = 42
    val l2 = 42L
    val l3 = 0x42L
    val l4 = 0X43L
    val l5: Long = 0xa
    val l6: Long = 0b01
    val l7 = 0b1010L

    val f1: Float = 42.42F
    val f2 = 42.43f
    val f3 = .4f

    val d1 = 0.6
    val d2 = .3

    val c1 = 'c'

    val s1 = "hello"

    val b1: Byte = 1
    val b2: Byte = 0x2
    val b3: Byte = 0b11
    val b4: Byte = 0x1f

    val sh1: Short = 11
    val sh2: Short = 0xc
    val sh3: Short = 0b1101
}