package spoon.test.function

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.declaration.CtMethod
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

class FunctionTest {

    @Test
    fun testBuildSimpleFunction() {
        val c = TestBuildUtil.buildClass("spoon.test.function.testclasses","SimpleFunctions")
        val eol = System.lineSeparator()

        var m = c.getMethodByName("f1")

        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertFalse(m.body.isImplicit)
        assertEquals("fun f1() {$eol}", m.asString())

        m = c.getMethodByName("f2")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), m.type)
        assertEquals(1, m.body.statements.size)
        assertFalse(m.body.isImplicit)
        assertEquals("fun f2(): kotlin.Int {${eol}    return 2$eol}", m.asString())

        m = c.getMethodByName("f3")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), m.type)
        assertEquals(1, m.body.statements.size)
        assertTrue(m.body.isImplicit)
        assertEquals("fun f3(): kotlin.Int = 3", m.asString())

        m = c.getMethodByName("f4")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), m.type)
        assertEquals(1, m.body.statements.size)
        assertTrue(m.body.isImplicit)
        assertEquals("fun f4(): kotlin.Int = 3", m.asString())


        m = c.getMethodByName("f5")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertFalse(m.body.isImplicit)
        assertEquals("fun f5(i: kotlin.Int, j: kotlin.Int) {$eol}", m.asString())
    }

    @Test
    fun testBuildFunctionsWithModifiers() {
        val c = TestBuildUtil.buildClass("spoon.test.function.testclasses","FunctionModifiers")
        val eol = System.lineSeparator()
        fun CtMethod<*>.getKtModifiers() = getMetadata(KtMetadataKeys.KT_MODIFIERS) as Set<KtModifierKind>

        var m = c.getMethodByName("f1")

        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertEquals(0, m.parameters.size)
        assertFalse(m.body.isImplicit)
        assertEquals("fun f1() {$eol}", m.asString()) // 'public' is redundant
        assertEquals(setOf(KtModifierKind.FINAL, KtModifierKind.PUBLIC), m.getKtModifiers())

        m = c.getMethodByName("f2")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertEquals(0, m.parameters.size)
        assertEquals(setOf(KtModifierKind.FINAL, KtModifierKind.PRIVATE), m.getKtModifiers())
        assertFalse(m.body.isImplicit)
        assertEquals("private fun f2() {$eol}", m.asString())

        m = c.getMethodByName("f3")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.String"), m.type)
        assertEquals(setOf(KtModifierKind.ABSTRACT, KtModifierKind.PUBLIC), m.getKtModifiers())
        assertEquals(1, m.parameters.size)
        assertNull(m.body)
        assertEquals("abstract fun f3(i: kotlin.Int): kotlin.String", m.asString())

        m = c.getMethodByName("f4")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertEquals(setOf(KtModifierKind.OPEN, KtModifierKind.PUBLIC), m.getKtModifiers())
        assertEquals(0, m.parameters.size)
        assertFalse(m.body.isImplicit)
        assertEquals("open fun f4() {$eol}", m.asString())

        m = c.getMethodByName("f5")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Unit"), m.type)
        assertEquals(0, m.body.statements.size)
        assertEquals(setOf(KtModifierKind.FINAL, KtModifierKind.INTERNAL), m.getKtModifiers())
        assertEquals(0, m.parameters.size)
        assertFalse(m.body.isImplicit)
        assertEquals("internal fun f5() {$eol}", m.asString())

        m = c.getMethodByName("plus")
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Int"), m.type)
        assertEquals(1, m.body.statements.size)
        assertEquals(setOf(KtModifierKind.FINAL, KtModifierKind.PROTECTED, KtModifierKind.OPERATOR,
            KtModifierKind.INFIX, KtModifierKind.INLINE, KtModifierKind.SUSPEND, KtModifierKind.TAILREC), m.getKtModifiers())
        assertEquals(1, m.parameters.size)
        assertFalse(m.body.isImplicit)

        val expectedString =
            "protected tailrec suspend inline infix operator fun plus(i: kotlin.Int): kotlin.Int {$eol    return 0$eol}"

        assertEquals(expectedString, m.asString())
    }
}