package spoon.test.loop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.reflect.declaration.CtType
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil

class WhileLoopTest {
    private val util = TestBuildUtil
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())
    private val whileLoopsClass = util.buildClass("spoon.test.loop.testclasses", "WhileLoops")
    private val eol = System.lineSeparator()

    private fun CtType<*>.getMethodByName(method: String) = getMethodsByName(method)[0]

    private fun CtType<*>.getLoop(method: String) =
        getMethodByName(method).body.getElements(TypeFilter(CtLoop::class.java))[0] as CtLoop

    @Test
    fun testEmptyWhile() {
        assertEquals(1, whileLoopsClass.getMethodByName("emptyWhile").body.statements.size)

        val loop = whileLoopsClass.getLoop("emptyWhile") as CtWhile
        assertEquals("kotlin.Boolean", loop.loopingExpression.type.qualifiedName)
        assertEquals(0, (loop.body as CtBlock<*>).statements.size)
        assertFalse(loop.body.isImplicit)
        assertEquals("while (i < 10) {}", pp.prettyprint(loop))
    }

    @Test
    fun testWhileWithInc() {
        assertEquals(1, whileLoopsClass.getMethodByName("whileWithInc").body.statements.size)

        val loop = whileLoopsClass.getLoop("whileWithInc") as CtWhile
        assertEquals(1, (loop.body as CtBlock<*>).statements.size)
        assertFalse(loop.body.isImplicit)
        assertEquals("while (i < 10) {$eol    i++$eol}", pp.prettyprint(loop))
    }

    @Test
    fun testEmptyDoWhile() {
        assertEquals(1, whileLoopsClass.getMethodByName("emptyDoWhile").body.statements.size)

        val loop = whileLoopsClass.getLoop("emptyDoWhile") as CtDo
        assertEquals(0, (loop.body as CtBlock<*>).statements.size)
        assertFalse(loop.body.isImplicit)
        assertEquals("do {} while (true)", pp.prettyprint(loop))
    }

    @Test
    fun testImplicitWhileBody() {
        assertEquals(1, whileLoopsClass.getMethodByName("implicitWhileBody").body.statements.size)

        val loop = whileLoopsClass.getLoop("implicitWhileBody") as CtWhile
        assertEquals(1, (loop.body as CtBlock<*>).statements.size)
        assertTrue(loop.body.isImplicit)
        assertEquals("while (i < 10) i += 2", pp.prettyprint(loop))
    }

    @Test
    fun testImplicitDoWhileBody() {
        assertEquals(1, whileLoopsClass.getMethodByName("implicitDoWhileBody").body.statements.size)

        val loop = whileLoopsClass.getLoop("implicitDoWhileBody") as CtDo
        assertEquals(1, (loop.body as CtBlock<*>).statements.size)
        assertTrue(loop.body.isImplicit)
        assertEquals("do i += 2 while (i < 10)", pp.prettyprint(loop))
    }
}