package spoon.test.trycatch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import spoon.reflect.code.CtReturn
import spoon.reflect.code.CtTry
import spoon.reflect.code.CtVariableRead
import spoon.reflect.declaration.CtType
import spoon.reflect.reference.CtCatchVariableReference
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.TestUtils
import spoon.test.getMethodByName

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TryCatchTest {

    private lateinit var multipleCatchClass: CtType<*>

    @BeforeAll
    fun buildMultipleCatch() {
        multipleCatchClass = TestBuildUtil.buildClass("spoon.test.trycatch.testclasses","MultipleCatch")
    }

    @Test
    fun testMultipleCatchOrder() {
        val eol = System.lineSeparator()
        val ctTry = multipleCatchClass.getMethodByName("m").getElements(TypeFilter(CtTry::class.java))[0]

        assertEquals(2, ctTry.catchers.size)
        assertEquals(multipleCatchClass.factory.Type().createReference<Any>("kotlin.RuntimeException"),
            ctTry.catchers[0].parameter.type)
        assertEquals(multipleCatchClass.factory.Type().createReference<Any>("kotlin.Throwable"),
            ctTry.catchers[1].parameter.type)

        assertNotNull(ctTry.finalizer)
        assertEquals(
            "try {} catch (e: kotlin.RuntimeException) {} catch (e: kotlin.Throwable) {} finally {}",
            TestUtils.pp.prettyprint(ctTry).replace(eol,""))
    }

    @Test
    fun testCatchWithoutFinalizer() {
        val eol = System.lineSeparator()
        val ctTry = multipleCatchClass.getMethodByName("noFinally").getElements(TypeFilter(CtTry::class.java))[0]

        assertEquals(1, ctTry.catchers.size)
        assertEquals(multipleCatchClass.factory.Type().createReference<Any>("kotlin.RuntimeException"),
            ctTry.catchers[0].parameter.type)

        assertNull(ctTry.finalizer)
        assertEquals(
            "try {} catch (e: kotlin.RuntimeException) {}",
            TestUtils.pp.prettyprint(ctTry).replace(eol,""))
    }

    @Test
    fun testCatchVariableReference() {
        val ctTry = multipleCatchClass.getMethodByName("catchRef").getElements(TypeFilter(CtTry::class.java))[0]
        assertEquals(1, ctTry.catchers.size)
        val theCatch = ctTry.catchers[0]
        assertEquals(1, theCatch.body.statements.size)
        val read = (theCatch.body.statements[0] as CtReturn<*>).returnedExpression
        assertTrue(read is CtVariableRead<*>)
        val varRef = (read as CtVariableRead<*>).variable
        assertTrue(varRef is CtCatchVariableReference<*>)
        assertEquals(multipleCatchClass.factory.Type().createReference<Any>("kotlin.Throwable"),
            varRef.type)
        assertSame(varRef.type, read.type)
    }
}