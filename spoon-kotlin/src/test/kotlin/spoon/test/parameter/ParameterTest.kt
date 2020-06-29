package spoon.test.parameter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtMethod
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName


class ParameterTest {

    @Test
    fun testBuildVarargParams() {
        val c = TestBuildUtil.buildClass("spoon.test.parameter.testclasses","VarargParam")

        val expectedTypes = listOf("Char", "Boolean", "Byte", "Short", "Int", "Long", "Float", "Double", "String")
        for(expectedType in expectedTypes) {
            val m = c.getMethodByName("${expectedType.toLowerCase()}Vararg")
            val params = m.parameters
            assertEquals(1, params.size)
            val p = params[0]

            assertTrue(p.isVarArgs)
            assertEquals("kotlin.$expectedType", p.type.qualifiedName)
            assertEquals("vararg p: kotlin.$expectedType", p.asString())
            assertEquals(0, m.body.statements.size)
            m.setBody<CtMethod<*>>(null)
            assertEquals("fun ${expectedType.toLowerCase()}Vararg(vararg p: kotlin.$expectedType)", m.asString())
        }

        var m = c.getMethodByName("userTypeVararg")
        var params = m.parameters
        assertEquals(1, params.size)
        val p = params[0]
        val classA = c.factory.Package().get("spoon.test.parameter.testclasses").getType<CtClass<*>>("A").reference
        assertEquals(classA, p.type)
        assertTrue(p.isVarArgs)
        assertEquals("vararg p: spoon.test.parameter.testclasses.A", p.asString())
        assertEquals(0, m.body.statements.size)
        m.setBody<CtMethod<*>>(null)
        assertEquals("fun userTypeVararg(vararg p: spoon.test.parameter.testclasses.A)", m.asString())

        m = c.getMethodByName("m")
        params = m.parameters
        assertEquals(2, params.size)
        assertFalse(params[0].isVarArgs)
        assertTrue(params[1].isVarArgs)
        assertEquals("vararg a: kotlin.Char", params[1].asString())
        assertEquals(0, m.body.statements.size)
        m.setBody<CtMethod<*>>(null)
        assertEquals("fun m(i: kotlin.Int, vararg a: kotlin.Char)", m.asString())

        m = c.getMethodByName("m2")
        params = m.parameters
        assertEquals(2, params.size)
        assertTrue(params[0].isVarArgs)
        assertFalse(params[1].isVarArgs)
        assertEquals("vararg i: spoon.test.parameter.testclasses.A", params[0].asString())
        assertEquals(0, m.body.statements.size)
        m.setBody<CtMethod<*>>(null)
        assertEquals("fun m2(vararg i: spoon.test.parameter.testclasses.A, a: kotlin.Char)", m.asString())
    }
}