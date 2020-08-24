package spoon.test.throwstmt

import org.junit.jupiter.api.Test
import spoon.reflect.code.CtThrow
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName
import kotlin.test.assertEquals

class ThrowTest {

    @Test
    fun testBuildThrow() {
        val c = TestBuildUtil.buildClass("spoon.test.throwstmt.testclasses","A")
        val throwStmt = c.getMethodByName("m").body.getElements(TypeFilter(CtThrow::class.java))[0]
        // kotlin.RuntimeException is just type alias for java.lang.RuntimeException
        assertEquals(c.factory.Type().createReference("java.lang.RuntimeException"),
            throwStmt.thrownExpression.type)

        assertEquals("throw java.lang.RuntimeException(\"Error\")", throwStmt.asString())
    }
}