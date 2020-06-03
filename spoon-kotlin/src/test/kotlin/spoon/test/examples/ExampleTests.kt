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

    val util = TestBuildUtil()
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
    }
}
