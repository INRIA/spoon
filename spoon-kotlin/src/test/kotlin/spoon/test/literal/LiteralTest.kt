package spoon.test.literal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.LiteralBase
import spoon.reflect.declaration.CtType
import spoon.test.TestBuildUtil
import spoon.test.literal.testclasses.Tacos


class LiteralTest {

    val util = TestBuildUtil

    private fun CtType<*>.createRef(t: Class<*>) =
        this.factory.Type().createReference(t)

    private fun CtType<*>.getBase(s: String): LiteralBase? = getLiteral(s).base
    private fun CtType<*>.getLiteral(s: String): CtLiteral<*> = (getField(s).defaultExpression as CtLiteral<*>)

    private val hex = LiteralBase.HEXADECIMAL
    private val dec = LiteralBase.DECIMAL
    private val bin = LiteralBase.BINARY

    @Test
    fun testBuildLiteral() {
        val ctType = util.buildClass("spoon.test.literal.testclasses","Tacos") as CtType<Tacos>

        var literal = ctType.getLiteral("a")
        assertEquals(0, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)

        literal = ctType.getLiteral("b")
        assertEquals(0x1.toByte(), literal.value)
        assertEquals("kotlin.Byte", literal.type.qualifiedName)

        literal = ctType.getLiteral("c")
        assertEquals(2f, literal.value)
        assertEquals("kotlin.Float", literal.type.qualifiedName)

        literal = ctType.getLiteral("d")
        assertEquals(3L, literal.value)
        assertEquals("kotlin.Long", literal.type.qualifiedName)

        literal = ctType.getLiteral("e")
        assertEquals(4.0, literal.value)
        assertEquals("kotlin.Double", literal.type.qualifiedName)

        literal = ctType.getLiteral("f")
        assertEquals('5', literal.value)
        assertEquals("kotlin.Char", literal.type.qualifiedName)

        literal = ctType.getLiteral("g")
        assertEquals("6", literal.value)
        assertEquals("kotlin.String", literal.type.qualifiedName)

        literal = ctType.getLiteral("h")
        assertEquals(null, literal.value)
        assertEquals(ctType.factory.Type().NULL_TYPE, literal.type)

        literal = ctType.getLiteral("i")
        assertEquals(true, literal.value)
        assertEquals("kotlin.Boolean", literal.type.qualifiedName)

        literal = ctType.getLiteral("j")
        assertEquals(false, literal.value)
        assertEquals("kotlin.Boolean", literal.type.qualifiedName)

        literal = ctType.getLiteral("k")
        assertEquals(6, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
    }

    @Test
    fun testBuildBasedByte() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        var literal = c.getLiteral("b1")
        assertEquals(1.toByte(), literal.value)
        assertEquals("kotlin.Byte", literal.type.qualifiedName)

        literal = c.getLiteral("b2")
        assertEquals(2.toByte(), literal.value)
        assertEquals("kotlin.Byte", literal.type.qualifiedName)

        literal = c.getLiteral("b3")
        assertEquals(3.toByte(), literal.value)
        assertEquals("kotlin.Byte", literal.type.qualifiedName)

        literal = c.getLiteral("b4")
        assertEquals(0x1f.toByte(), literal.value)
        assertEquals("kotlin.Byte", literal.type.qualifiedName)
    }

    @Test
    fun testBuildBasedInt() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        var literal = c.getLiteral("i1")
        assertEquals(42, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(dec, literal.base)

        literal = c.getLiteral("i2")
        assertEquals(0, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(dec, literal.base)

        literal = c.getLiteral("i3")
        assertEquals(0x42, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(hex, literal.base)

        literal = c.getLiteral("i4")
        assertEquals(0x43, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(hex, literal.base)

        literal = c.getLiteral("i5")
        assertEquals(142, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(dec, literal.base)

        literal = c.getLiteral("i6")
        assertEquals(1, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(bin, literal.base)

        literal = c.getLiteral("i7")
        assertEquals(0b1010, literal.value)
        assertEquals("kotlin.Int", literal.type.qualifiedName)
        assertEquals(bin, literal.base)
    }

    @Test
    fun testBuildBasedLong() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")
        val bases = listOf(dec,dec,hex,hex,hex,bin,bin)
        val values = listOf<Long>(42,42,0x42,0x43,0xa,1,0b1010)
        var literal: CtLiteral<*>
        for(i in 1..7) {
            literal = c.getLiteral("l$i")
            assertEquals(values[i-1], literal.value)
            assertEquals("kotlin.Long", literal.type.qualifiedName)
            assertEquals(bases[i-1], literal.base)
        }
    }

    @Test
    fun testBuildFloat() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        val values = listOf<Float>(42.42f,42.43f,0.4f)
        var literal: CtLiteral<*>
        for(i in 1..3) {
            literal = c.getLiteral("f$i")
            assertEquals(values[i-1], literal.value)
            assertEquals("kotlin.Float", literal.type.qualifiedName)
            assertEquals(dec, literal.base)
        }
    }

    @Test
    fun testBuildDouble() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        var literal: CtLiteral<*> = c.getLiteral("d1")
        assertEquals(0.6, literal.value)
        assertEquals("kotlin.Double", literal.type.qualifiedName)
        assertEquals(dec, literal.base)

        literal= c.getLiteral("d2")
        assertEquals(0.3, literal.value)
        assertEquals("kotlin.Double", literal.type.qualifiedName)
        assertEquals(dec, literal.base)
    }

    @Test
    fun testBuildChar() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        val literal: CtLiteral<*> = c.getLiteral("c1")
        assertEquals('c', literal.value)
        assertEquals("kotlin.Char", literal.type.qualifiedName)
        assertNull(literal.base)
    }

    @Test
    fun testBuildString() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        val literal: CtLiteral<*> = c.getLiteral("s1")
        assertEquals("hello", literal.value)
        assertEquals("kotlin.String", literal.type.qualifiedName)
        assertNull(literal.base)
    }

    @Test
    fun testBuildShort() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        val values = listOf<Short>(11,12,13)
        val bases = listOf(dec,hex,bin)
        var literal: CtLiteral<*>
        for(i in 1..3) {
            literal = c.getLiteral("sh$i")
            assertEquals(values[i-1], literal.value)
            assertEquals("kotlin.Short", literal.type.qualifiedName)
            assertEquals(bases[i-1], literal.base)
        }
    }

    @Test
    fun testLiteralBase() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")

        assertEquals(dec, c.getBase("l1"))
        assertEquals(dec, c.getBase("l2"))
        assertEquals(hex, c.getBase("l3"))
        assertEquals(hex, c.getBase("l4"))
        assertEquals(hex, c.getBase("l5"))
        assertEquals(bin, c.getBase("l6"))
        assertEquals(bin, c.getBase("l7"))

        assertEquals(dec, c.getBase("f1"))
        assertEquals(dec, c.getBase("f2"))
        assertEquals(dec, c.getBase("f3"))

        assertEquals(dec, c.getBase("d1"))
        assertEquals(dec, c.getBase("d2"))

        assertNull(c.getBase("c1"))
        assertNull(c.getBase("s1"))

        assertEquals(dec, c.getBase("b1"))
        assertEquals(hex, c.getBase("b2"))
        assertEquals(bin, c.getBase("b3"))

        assertEquals(dec, c.getBase("sh1"))
        assertEquals(hex, c.getBase("sh2"))
        assertEquals(bin, c.getBase("sh3"))
    }

    @Test
    fun testLiteralBasePrinter() {
        val c = util.buildClass("spoon.test.literal.testclasses","BasedLiteral")
        val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

        // Ints
        var valueStrings = listOf("42","0","0x42","0x43","142","0b1","0b1010")
        for(i in 1..7) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("i$i")))
        }
        c.getLiteral("i1").setBase<Nothing>(hex)
        assertEquals("0x2a", pp.prettyprint(c.getLiteral("i1")))
        c.getLiteral("i1").setBase<Nothing>(bin)
        assertEquals("0b101010", pp.prettyprint(c.getLiteral("i1")))

        valueStrings = listOf("42L","42L","0x42L","0x43L","0xaL","0b1L","0b1010L")
        for(i in 1..7) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("l$i")))
        }

        valueStrings = listOf("42.42F", "42.43F", "0.4F")
        for(i in 1..3) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("f$i")))
        }

        valueStrings = listOf("0.6", "0.3")
        for(i in 1..2) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("d$i")))
        }

        assertEquals("'c'", pp.prettyprint(c.getLiteral("c1")))
        assertEquals("\"hello\"", pp.prettyprint(c.getLiteral("s1")))

        valueStrings = listOf("1", "0x2", "0b11", "0x1f")
        for(i in 1..4) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("b$i")))
        }

        valueStrings = listOf("11", "0xc", "0b1101")
        for(i in 1..3) {
            assertEquals(valueStrings[i-1], pp.prettyprint(c.getLiteral("sh$i")))
        }
    }

    @Test
    fun testEscapedLiteralChar() {
        val c = util.buildClass("spoon.test.literal.testclasses","EscapedLiteral")

        fun CtType<*>.getChar(s: String) = (this.getField(s).defaultExpression as CtLiteral<*>).value as Char

        assertEquals('\u0001', c.getChar("c1"))
        assertEquals('\u0002', c.getChar("c2"))
        assertEquals('\t', c.getChar("c3"))
        assertEquals('"', c.getChar("c4"))
        assertEquals('$', c.getChar("c5"))
    }
}