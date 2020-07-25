package spoon.test.ifthenelse

import spoon.kotlin.reflect.KtStatementExpression
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.CtIf
import spoon.reflect.code.CtLocalVariable
import spoon.reflect.code.CtStatement
import spoon.reflect.reference.CtTypeReference
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

class IfTest {

    @Test
    fun testBuildIfs() {
        val c = TestBuildUtil.buildClass("spoon.test.ifthenelse.testclasses","SimpleIfs")
        val m = c.getMethodByName("m")
        val statements = m.body.statements

        var line = statements[0]
        assertTrue(line is CtIf)
        var ctIf = line as CtIf
        var type = ctIf.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as? CtTypeReference<*>?
        assertNotNull(ctIf.condition)
        assertNotNull(ctIf.getThenStatement())
        assertNotNull(ctIf.getElseStatement())
        assertNotNull(type)
        assertEquals("kotlin.Boolean", ctIf.condition.type.qualifiedName)
        assertEquals("kotlin.Unit", type!!.qualifiedName)
        assertEquals("x == y", ctIf.condition.asString())
        assertEquals("println(x)", ctIf.getThenStatement<CtStatement>().asString())
        assertEquals("println(y)", ctIf.getElseStatement<CtStatement>().asString())
        assertEquals("if (x == y) println(x) else println(y)", ctIf.asString())

        line = statements[1]
        ctIf = line as CtIf
        type = ctIf.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as? CtTypeReference<*>?
        assertNotNull(ctIf.condition)
        assertNotNull(ctIf.getThenStatement())
        assertNull(ctIf.getElseStatement())
        assertNotNull(type)
        assertEquals("kotlin.Boolean", ctIf.condition.type.qualifiedName)
        assertEquals("kotlin.Unit", type!!.qualifiedName)
        assertEquals("x == y", ctIf.condition.asString())
        assertEquals("println(x)", ctIf.getThenStatement<CtStatement>().asString())
        assertEquals("if (x == y) println(x)", ctIf.asString())

        var expressionLine = (statements[2] as CtLocalVariable<*>).defaultExpression
        assertTrue(expressionLine is KtStatementExpression<*>)
        ctIf = (expressionLine as KtStatementExpression<*>).statement as CtIf
        type = expressionLine.type
        assertNotNull(ctIf.condition)
        assertNotNull(ctIf.getThenStatement())
        assertNotNull(ctIf.getElseStatement())
        assertNotNull(type)
        assertEquals("kotlin.Boolean", ctIf.condition.type.qualifiedName)
        assertEquals("kotlin.String", type!!.qualifiedName)
        assertEquals("x == 2", ctIf.condition.asString())
        assertEquals("\"Yes\"", ctIf.getThenStatement<CtStatement>().asString())
        assertEquals("\"No\"", ctIf.getElseStatement<CtStatement>().asString())
        assertEquals("if (x == 2) \"Yes\" else \"No\"", ctIf.asString())

        expressionLine = (statements[3] as CtLocalVariable<*>).defaultExpression
        assertTrue(expressionLine is KtStatementExpression<*>)
        ctIf = (expressionLine as KtStatementExpression<*>).statement as CtIf
        type = expressionLine.type
        assertNotNull(ctIf.condition)
        assertNotNull(ctIf.getThenStatement())
        assertNotNull(ctIf.getElseStatement())
        assertNotNull(type)
        val eol = System.lineSeparator()
        assertEquals("kotlin.Boolean", ctIf.condition.type.qualifiedName)
        assertEquals("kotlin.Int", type!!.qualifiedName)
        assertEquals("2 == x", ctIf.condition.asString())
        assertEquals("{$eol    2$eol}", ctIf.getThenStatement<CtStatement>().asString())
        assertEquals("3", ctIf.getElseStatement<CtStatement>().asString())
        assertEquals("if (2 == x) {$eol    2$eol} else 3", ctIf.asString())
    }
}