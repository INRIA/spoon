package spoon.test.trycatch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.reflect.code.CtTry
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.TestUtils
import spoon.test.getMethodByName


class TryCatchTest {

    @Test
    fun testMultipleCatchOrder() {
        val eol = System.lineSeparator()
        val c = TestBuildUtil.buildClass("spoon.test.trycatch.testclasses","MultipleCatch")
        val ctTry = c.getMethodByName("m").getElements(TypeFilter(CtTry::class.java))[0]

        assertEquals(2, ctTry.catchers.size)
        assertEquals(c.factory.Type().createReference<Any>("kotlin.RuntimeException"),
            ctTry.catchers[0].parameter.type)
        assertEquals(c.factory.Type().createReference<Any>("kotlin.Throwable"),
            ctTry.catchers[1].parameter.type)

        assertNotNull(ctTry.finalizer)
        assertEquals(
            "try {} catch (e: kotlin.RuntimeException) {} catch (e: kotlin.Throwable) {} finally {}",
            TestUtils.pp.prettyprint(ctTry).replace(eol,""))
    }

    @Test
    fun testCatchWithoutFinalizer() {
        val eol = System.lineSeparator()
        val c = TestBuildUtil.buildClass("spoon.test.trycatch.testclasses","MultipleCatch")
        val ctTry = c.getMethodByName("noFinally").getElements(TypeFilter(CtTry::class.java))[0]

        assertEquals(1, ctTry.catchers.size)
        assertEquals(c.factory.Type().createReference<Any>("kotlin.RuntimeException"),
            ctTry.catchers[0].parameter.type)

        assertNull(ctTry.finalizer)
        assertEquals(
            "try {} catch (e: kotlin.RuntimeException) {}",
            TestUtils.pp.prettyprint(ctTry).replace(eol,""))
    }
}