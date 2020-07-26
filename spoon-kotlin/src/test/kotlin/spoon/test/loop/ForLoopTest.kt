package spoon.test.loop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.reflect.declaration.CtType
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

class ForLoopTest {
    private val util = TestBuildUtil
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())
    private val forLoopsClass = util.buildClass("spoon.test.loop.testclasses", "ForLoops")
    private val eol = System.lineSeparator()

    private fun CtType<*>.getLoop(method: String) =
        getMethodByName(method).body.getElements(TypeFilter(CtForEach::class.java))[0] as CtForEach

    @Test
    fun testShadowedVariable() {
        // Contract: Shadowing variable is correctly reference from inside body,
        // outer and inner variable are not the same
        assertEquals(1, forLoopsClass.getMethodByName("shadowedVariable").body.statements.size)

        val outerLoop = forLoopsClass.getLoop("shadowedVariable")
        val outerVar = outerLoop.variable
        val innerLoop = (outerLoop.body as CtBlock<*>).statements[0] as CtForEach
        val innerVar = innerLoop.variable
        assertEquals("i", outerVar.simpleName)
        assertEquals("i", innerVar.simpleName)
        assertNotSame(outerVar, innerVar)

        val assignment = (innerLoop.body as CtBlock<*>).statements[0] as CtAssignment<*,*>
        assertSame(innerVar, (assignment.assignment as CtVariableRead).variable.declaration)

        val indent = "    "
        val expectedString = "for (i in 0..10) {" + eol +
                indent + "for (i in 11..20) {" + eol +
                indent + indent + "x = i" + eol +
                indent + "}" + eol +
                "}"
        assertEquals(expectedString, pp.prettyprint(outerLoop))
    }

    @Test
    fun testEmptyFor() {
        assertEquals(1, forLoopsClass.getMethodByName("emptyFor").body.statements.size)

        val loop = forLoopsClass.getLoop("emptyFor")
        assertEquals("i", loop.variable.simpleName)
        assertEquals("kotlin.Int", loop.variable.type.qualifiedName)
        assertEquals("0..10", pp.prettyprint(loop.expression))
        assertEquals(
            forLoopsClass.factory.Type().createReference<CtTypeReference<Int>>("kotlin.ranges.IntRange"),
            loop.expression.type
        )
        assertEquals(0, (loop.body as CtBlock<*>).statements.size)
        assertFalse(loop.body.isImplicit)
        assertEquals("for (i in 0..10) {}", pp.prettyprint(loop))
    }

    @Test
    fun testImplicitBlock() {
        assertEquals(1, forLoopsClass.getMethodByName("implicitBlockAssignment").body.statements.size)
        assertEquals(1, forLoopsClass.getMethodByName("implicitBlockInvocation").body.statements.size)

        var loop = forLoopsClass.getLoop("implicitBlockAssignment")
        assertTrue(loop.body is CtBlock<*>)
        assertTrue((loop.body as CtBlock<*>).isImplicit)
        assertEquals("x += i", pp.prettyprint(loop.body))

        loop = forLoopsClass.getLoop("implicitBlockInvocation")
        assertTrue(loop.body is CtBlock<*>)
        assertTrue((loop.body as CtBlock<*>).isImplicit)
        assertEquals("println(i)", pp.prettyprint(loop.body))
    }

    @Test
    fun testListIterator() {
        val loop = forLoopsClass.getLoop("forListIterator")
        val list = forLoopsClass.getMethodByName("forListIterator").body.statements[0] as CtLocalVariable<*>

        assertTrue(loop.expression is CtVariableRead<*>)
        val listRef = (loop.expression as CtVariableRead<*>).variable
        assertEquals("e", loop.variable.simpleName)
        assertSame(list, listRef.declaration)

        val expectedString = "for (e in l) {$eol    println(e)$eol}"
        assertEquals(expectedString, pp.prettyprint(loop))
    }

    @Test
    fun testForWithOpenRange() {
        val loop = forLoopsClass.getLoop("forWithOpenRange")
        assertTrue(loop.expression is CtInvocation<*>)
        assertEquals("for (i in 0 until 10) {}", pp.prettyprint(loop))
    }

    @Test
    fun testForWithDescendingRange() {
        val loop = forLoopsClass.getLoop("forWithDescendingRange")
        assertTrue(loop.expression is CtInvocation<*>)
        assertEquals("for (i in 10 downTo 0) {}", pp.prettyprint(loop))
    }

    @Test
    fun testForWithStep() {
        val loop = forLoopsClass.getLoop("forWithStep")
        assertTrue(loop.expression is CtInvocation<*>)
        val invocation = loop.expression as CtInvocation<*>
        assertEquals("IntRange", invocation.target.type.simpleName)
        assertEquals("for (i in (0..10) step 2) {}", pp.prettyprint(loop))
    }

    @Test
    fun testForWithIndices() {
        val loop = forLoopsClass.getLoop("forWithIndices")
        assertTrue(loop.expression is CtVariableRead)
        assertEquals("IntRange", loop.expression.type.simpleName)
        assertEquals("for (i in l.indices) {}", pp.prettyprint(loop))
    }

    @Test
    fun testForWithWithIndex() {
        val loop = forLoopsClass.getLoop("forWithWithIndex")
        assertTrue(loop.expression is CtInvocation<*>)
        assertEquals("kotlin.collections.Iterable<kotlin.collections.IndexedValue<kotlin.Int>>", loop.expression.type.asString())
        assertEquals("for ((i, n) in l.withIndex()) {}", pp.prettyprint(loop))
    }
}