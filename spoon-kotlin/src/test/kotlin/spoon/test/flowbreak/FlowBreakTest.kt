package spoon.test.flowbreak

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import spoon.reflect.code.*
import spoon.reflect.declaration.CtType
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlowBreakTest {
    lateinit var cont: CtType<*>

    @BeforeAll
    fun buildTestClasses() {
        cont = TestBuildUtil.buildClass("spoon.test.flowbreak.testclasses", "Continue")
    }

    @Test
    fun testBuildContinue() {
        var m = cont.getMethodByName("forLoop")
        var loop: CtLoop = m.body.statements[0] as CtForEach
        var ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue", ctContinue.asString())
        assertNull(ctContinue.targetLabel)

        m = cont.getMethodByName("whileLoop")
        loop = m.body.statements[0] as CtWhile
        ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue", ctContinue.asString())
        assertNull(ctContinue.targetLabel)

        m = cont.getMethodByName("doWhile")
        loop = m.body.statements[0] as CtDo
        ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue", ctContinue.asString())
        assertNull(ctContinue.targetLabel)
    }

    @Test
    fun testBuildLabelledContinue() {
        val eol = System.lineSeparator()
        var m = cont.getMethodByName("labelledFor")
        var loop: CtLoop = m.body.statements[0] as CtLoop
        assertNotNull(loop.label)
        var ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue@loop", ctContinue.asString())
        assertNotNull(ctContinue.targetLabel)
        assertEquals("loop@ for (i in 1..10) {$eol    continue@loop$eol}", loop.asString())

        m = cont.getMethodByName("labelledWhile")
        loop = m.body.statements[0] as CtLoop
        assertEquals("loop", loop.label)
        ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue@loop", ctContinue.asString())
        assertNotNull(ctContinue.targetLabel)
        assertEquals("loop@ while (true) {$eol    continue@loop$eol}", loop.asString())

        m = cont.getMethodByName("labelledDoWhile")
        loop = m.body.statements[0] as CtLoop
        assertEquals("loop", loop.label)
        ctContinue = (loop.body as CtBlock<*>).statements[0] as CtContinue
        assertEquals("continue@loop", ctContinue.asString())
        assertNotNull(ctContinue.targetLabel)
        assertEquals("loop@ do {$eol    continue@loop$eol} while (true)", loop.asString())
    }

    @Test
    fun testBuildNestedLabels() {
        var m = cont.getMethodByName("nestedFor")
        var outerLoop: CtLoop = m.body.statements[0] as CtLoop
        var innerLoop = (outerLoop.body as CtBlock<*>).statements[0] as CtLoop
        assertEquals("outer", outerLoop.label)
        assertEquals("inner", innerLoop.label)
        var ctContinue = (innerLoop.body as CtBlock<*>).statements[0] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@outer", ctContinue.asString())
        ctContinue = (innerLoop.body as CtBlock<*>).statements[1] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@inner", ctContinue.asString())

        m = cont.getMethodByName("nestedWhile")
        outerLoop = m.body.statements[0] as CtLoop
        innerLoop = (outerLoop.body as CtBlock<*>).statements[0] as CtLoop
        assertEquals("outer", outerLoop.label)
        assertEquals("inner", innerLoop.label)
        ctContinue = (innerLoop.body as CtBlock<*>).statements[0] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@outer", ctContinue.asString())
        ctContinue = (innerLoop.body as CtBlock<*>).statements[1] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@inner", ctContinue.asString())

        m = cont.getMethodByName("nestedWhile")
        outerLoop = m.body.statements[0] as CtLoop
        innerLoop = (outerLoop.body as CtBlock<*>).statements[0] as CtLoop
        assertEquals("outer", outerLoop.label)
        assertEquals("inner", innerLoop.label)
        ctContinue = (innerLoop.body as CtBlock<*>).statements[0] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@outer", ctContinue.asString())
        ctContinue = (innerLoop.body as CtBlock<*>).statements[1] as CtContinue
        assertNotNull(ctContinue.targetLabel)
        assertEquals("continue@inner", ctContinue.asString())
    }

    @Test
    fun testBuildBreak() {
        val c = TestBuildUtil.buildClass("spoon.test.flowbreak.testclasses", "Break")
        var m = c.getMethodByName("forLoop")

        var outerLoop = m.body.statements[0] as CtLoop
        var ctBreak = (outerLoop.body as CtBlock<*>).statements[0] as CtBreak
        assertNull(ctBreak.targetLabel)

        m = c.getMethodByName("nestedWhile")

        outerLoop = m.body.statements[0] as CtLoop
        val innerLoop = (outerLoop.body as CtBlock<*>).statements[0] as CtLoop
        ctBreak = (innerLoop.body as CtBlock<*>).statements[0] as CtBreak
        assertEquals("inner", ctBreak.targetLabel)
        assertEquals("break@inner", ctBreak.asString())
        ctBreak = (innerLoop.body as CtBlock<*>).statements[1] as CtBreak
        assertEquals("outer", ctBreak.targetLabel)
        assertEquals("break@outer", ctBreak.asString())

    }
}