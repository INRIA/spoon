package spoon.test.reflect.printing

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.test.TestBuildUtil


class PrettyPrinterTest {
    private val util = TestBuildUtil

    @Test
    fun testPrettyPrintIdempotent() {
        val c1 = util.buildClass("spoon.test.reflect.printing.testclasses","SingleProperty")
        val c2 = util.buildClass("spoon.test.reflect.printing.testclasses","SingleProperty")
        val adapter = DefaultPrinterAdapter()
        val pp = DefaultKotlinPrettyPrinter(adapter)
        assertEquals(1, adapter.line)
        assertEquals(1, adapter.column)
        assertEquals(0, adapter.indentCount)
        assertTrue(adapter.onNewLine)

        val first1 = pp.prettyprint(c1)
        assertEquals(1, adapter.line)
        assertEquals(1, adapter.column)
        assertEquals(0, adapter.indentCount)
        assertTrue(adapter.onNewLine)

        val first2 = pp.prettyprint(c2)
        assertEquals(1, adapter.line)
        assertEquals(1, adapter.column)
        assertEquals(0, adapter.indentCount)
        assertTrue(adapter.onNewLine)
        assertEquals(first1, first2)

        var s1 = pp.prettyprint(c1)
        assertEquals(first1, s1)

        var s2 = pp.prettyprint(c2)
        assertEquals(first2, s2)

        s1 = pp.prettyprint(c1)
        assertEquals(first1, s1)

        s2 = pp.prettyprint(c2)
        assertEquals(first2, s2)
    }
}