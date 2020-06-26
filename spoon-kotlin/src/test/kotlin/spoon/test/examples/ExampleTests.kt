package spoon.test.examples

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import spoon.kotlin.compiler.normalizeLineBreaks
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.declaration.CtClass
import spoon.test.TestBuildUtil
import java.io.File
import kotlin.test.assertEquals

class ExampleTests {

    val util = TestBuildUtil
    var pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter(LINE_SEPARATOR = "\n"))

    @BeforeEach
    fun resetPP() {
        pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter(LINE_SEPARATOR = "\n"))
    }

    @Test
    fun testExample1() {
        val c = util.buildClass("spoon.test.examples.testclasses","Example1")
        pp.visitCtClass(c as CtClass<*>)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example1.txt").
            readText().normalizeLineBreaks(), pp.result)

        pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter(LINE_SEPARATOR = "\r\n"))
        pp.visitCtClass(c)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example1.txt").
        readText().normalizeLineBreaks().replace("\n","\r\n"), pp.result)
    }

    @Test
    fun testExample2() {
        val c = util.buildClass("spoon.test.examples.testclasses","Example2")
        pp.visitCtClass(c as CtClass<*>)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example2.txt").
        readText().normalizeLineBreaks(), pp.result)

        pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter(LINE_SEPARATOR = "\r\n"))
        pp.visitCtClass(c)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example2.txt").
        readText().normalizeLineBreaks().replace("\n","\r\n"), pp.result)
    }

    @Test
    fun testExample3() {
        val c = util.buildClass("spoon.test.examples.testclasses","Example3")
        pp.visitCtClass(c as CtClass<*>)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example3.txt").
        readText().normalizeLineBreaks(), pp.result)

        pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter(LINE_SEPARATOR = "\r\n"))
        pp.visitCtClass(c)

        assertEquals(File("./src/test/resources/ExamplesPPOutput/Example3.txt").
        readText().normalizeLineBreaks().replace("\n","\r\n"), pp.result)
    }
}
