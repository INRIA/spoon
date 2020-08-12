package spoon.test.targeted

import org.junit.jupiter.api.Test
import spoon.test.TestBuildUtil
import org.junit.jupiter.api.Assertions.*
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.reflect.declaration.CtMethod
import spoon.reflect.reference.CtFieldReference
import spoon.reflect.visitor.filter.NamedElementFilter
import spoon.test.asString
import spoon.test.getMethodByName

class TargetedExpressionTest {

    val util = TestBuildUtil
    val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    @Test
    fun testSuperAccess() {
        val c = util.buildClass("spoon.test.targeted.testclasses","SubClass")
        val method = c.getMethod<String>("toString")

        assertEquals("return super.toString()", pp.prettyprint(method.body.statements[0]))
        assertEquals("val x2 = super.x", pp.prettyprint(c.fields[1]))
        val s = (c.fields[1].defaultExpression as CtFieldRead<*>)

        assertTrue(s.target is CtSuperAccess<*>)
        assertEquals(c.superclass, s.variable.declaringType)
    }

    @Test
    fun testSimpleThisAccess() {
        val c = util.buildClass("spoon.test.targeted.testclasses","SimpleThisAccess")
        val f = c.getElements(NamedElementFilter(CtMethod::class.java,"f"))[0]
        val s1 = f.body.statements[0] as CtAssignment<*,*>
        val s2 = f.body.statements[1] as CtAssignment<*,*>

        assertEquals("this.x = x", pp.prettyprint(s1))
        assertEquals("this.y = y", pp.prettyprint(s2))

        assertTrue((s1.getAssigned() is CtFieldWrite<*>))
        val fieldWrite = (s1.getAssigned() as CtFieldWrite<*>)

        assertTrue(fieldWrite.target is CtThisAccess)
        assertEquals("spoon.test.targeted.testclasses.SimpleThisAccess",
            (fieldWrite.target as CtThisAccess).type.qualifiedName)

        assertTrue(fieldWrite.variable is CtFieldReference<*>)
        val fieldRef = fieldWrite.variable as CtFieldReference<*>
        assertEquals("spoon.test.targeted.testclasses.SimpleThisAccess#x", fieldRef.qualifiedName)

    }

    @Test
    fun testJavaStaticAccess() {
        val c = util.buildClass("spoon.test.targeted.testclasses","JavaStaticAccess")
        val invocation = c.getMethodByName("m").body.statements[0] as CtInvocation<*>
        val executable = invocation.executable
        val target = invocation.target as CtTypeAccess<*>

        assertEquals("java.util.Objects", target.accessedType.qualifiedName)
        assertEquals("java.util.Objects", executable.declaringType.qualifiedName)
        assertEquals("java.util.Objects.isNull(null)", invocation.asString())
    }
}