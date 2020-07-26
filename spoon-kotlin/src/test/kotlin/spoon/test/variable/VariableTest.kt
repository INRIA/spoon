package spoon.test.variable

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.CtLocalVariable
import spoon.reflect.code.CtVariableAccess
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.*

class VariableTest {
    @Test
    fun testBuildLocalVariables() {
        val c = TestBuildUtil.buildClass("spoon.test.variable.testclasses","LocalVariables")

        val method = c.getMethodByName("m")
        val accesses = method.getElements(TypeFilter(CtVariableAccess::class.java))
        assertEquals(3, accesses.size)

        val localVars = method.getElements(TypeFilter(CtLocalVariable::class.java))
        assertEquals(4, localVars.size)

        var localVar = localVars[0]
        assertEquals(setOf(KtModifierKind.VAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("val l1: kotlin.Int = 0", localVar.asString())

        localVar = localVars[1]
        assertEquals(setOf(KtModifierKind.VAR), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("var l2 = l1", localVar.asString())

        localVar = localVars[2]
        assertEquals(setOf(KtModifierKind.VAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("val l3: kotlin.Int", localVar.asString())

        localVar = localVars[3]
        assertEquals(setOf(KtModifierKind.VAR), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Double"), localVar.type)
        assertEquals("var l4: kotlin.Double", localVar.asString())
    }

    @Test
    fun testBuildDestructuredVariable() {
        val c = TestBuildUtil.buildClass("spoon.test.variable.testclasses","LocalVariables")

        val method = c.getMethodByName("destructured")

        val localVars = method.getElements(TypeFilter(CtLocalVariable::class.java))
        assertEquals(2, localVars.size)

        var localVar = localVars[0]
    }
}