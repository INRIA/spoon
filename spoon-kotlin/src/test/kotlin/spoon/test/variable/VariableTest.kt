package spoon.test.variable

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.CtLocalVariable
import spoon.reflect.code.CtVariableAccess
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.TestUtils
import spoon.test.getMethodByName
import spoon.test.getKtModifiers

class VariableTest {
    @Test
    fun testBuildLocalVariables() {
        val c = TestBuildUtil.buildClass("spoon.test.variable.testclasses","LocalVariables")
        val pp = TestUtils.pp
        val method = c.getMethodByName("m")
        val accesses = method.getElements(TypeFilter(CtVariableAccess::class.java))
        assertEquals(3, accesses.size)

        val localVars = method.getElements(TypeFilter(CtLocalVariable::class.java))
        assertEquals(4, localVars.size)

        var localVar = localVars[0]
        assertEquals(setOf(KtModifierKind.VAL,KtModifierKind.FINAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("val l1: kotlin.Int = 0", pp.prettyprint(localVar))

        localVar = localVars[1]
        assertEquals(setOf(KtModifierKind.VAR,KtModifierKind.FINAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("var l2 = l1", pp.prettyprint(localVar))

        localVar = localVars[2]
        assertEquals(setOf(KtModifierKind.VAL,KtModifierKind.FINAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), localVar.type)
        assertEquals("val l3: kotlin.Int", pp.prettyprint(localVar))

        localVar = localVars[3]
        assertEquals(setOf(KtModifierKind.VAR,KtModifierKind.FINAL), localVar.getKtModifiers())
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Double"), localVar.type)
        assertEquals("var l4: kotlin.Double", pp.prettyprint(localVar))
    }
}