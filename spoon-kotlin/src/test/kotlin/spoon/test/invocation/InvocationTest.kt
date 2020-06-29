package spoon.test.invocation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.*
import spoon.reflect.declaration.CtClass
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName


class InvocationTest {

    @Test
    fun testTargetNullForTopLevelFunction() {
        val f = TestBuildUtil.buildFile("spoon.test.invocation.testclasses", "Foo")
        val c = f.Package().get("spoon.test.invocation.testclasses").getType<CtClass<*>>("Foo")
        val m = c.getMethodByName("foo")

        val invocation = m.body.statements[0] as CtInvocation<*>

        assertNull(invocation.target)
        assertEquals("topLevel()", invocation.asString())
    }

    @Test
    fun testInvokeTargetChain() {
        val f = TestBuildUtil.buildFile("spoon.test.invocation.testclasses", "Foo")
        val c = f.Package().get("spoon.test.invocation.testclasses").getType<CtClass<*>>("Bar")
        val foo = f.Package().get("spoon.test.invocation.testclasses").getType<CtClass<*>>("Foo")
        val m = c.getMethodByName("bar")
        val fooField = c.getField("foo")

        var invocation = m.body.statements[0] as CtInvocation<*>
        assertEquals("kotlin.Unit", invocation.type.qualifiedName)
        assertTrue(invocation.target is CtThisAccess<*>)
        assertTrue(invocation.target.isImplicit)
        assertEquals("bar()", invocation.asString())

        invocation = m.body.statements[1] as CtInvocation<*>
        var fieldRead = invocation.target as CtFieldRead<*>
        assertEquals(fooField.reference, fieldRead.variable)
        assertFalse(invocation.target.isImplicit)
        assertEquals("foo.f1()", invocation.asString())

        invocation = m.body.statements[2] as CtInvocation<*>
        val f2Exec = invocation.executable
        fieldRead = invocation.target as CtFieldRead<*>
        assertEquals(fooField.reference, fieldRead.variable)
        assertFalse(invocation.target.isImplicit)
        assertEquals("foo.f2(1)", invocation.asString())

        invocation = m.body.statements[3] as CtInvocation<*>
        fieldRead = invocation.target as CtFieldRead<*>
        assertEquals(fooField.reference, fieldRead.variable)
        assertFalse(invocation.target.isImplicit)
        assertEquals("foo.f2(2).foo.foo()", invocation.asString())

        invocation = fieldRead.target as CtInvocation<*>
        assertEquals(f2Exec, invocation.executable)

        fieldRead = invocation.target as CtFieldRead<*>
        assertEquals(fooField.reference, fieldRead.variable)
        assertFalse(invocation.target.isImplicit)
        assertEquals("foo.f2(2)", invocation.asString())
    }

    private fun CtInvocation<*>.getInvokeOperatorFlag() = getMetadata(KtMetadataKeys.INVOKE_AS_OPERATOR) as? Boolean? ?: false

    @Test
    fun testInvokeOperator() {
        val c = TestBuildUtil.buildClass("spoon.test.invocation.testclasses","Invoker")
        val classWithInvokeOperator = c.factory.Package().get("spoon.test.invocation.testclasses").
            getType<CtClass<*>>("ClassWithInvokeOperator")
        val m = c.getMethodByName("m")

        // Non-operator invoke shan't be built as operator
        var invocation = m.body.statements[0] as CtInvocation<*>
        var thisTarget = invocation.target as CtThisAccess<*>
        assertEquals(c.reference, thisTarget.type)
        assertEquals(c.reference, (thisTarget.target as CtTypeAccess<*>).accessedType)
        assertFalse(invocation.target.isImplicit)
        assertFalse(invocation.getInvokeOperatorFlag())
        assertEquals("this.invoke()", invocation.asString())

        invocation = m.body.statements[1] as CtInvocation<*>
        thisTarget = invocation.target as CtThisAccess<*>
        assertEquals(c.reference, thisTarget.type)
        assertEquals(c.reference, (thisTarget.target as CtTypeAccess<*>).accessedType)
        assertTrue(invocation.target.isImplicit)
        assertFalse(invocation.getInvokeOperatorFlag())
        assertEquals("invoke()", invocation.asString())

        invocation = m.body.statements[2] as CtInvocation<*>
        var fieldTarget = invocation.target as CtFieldRead<*>
        assertEquals(classWithInvokeOperator.reference, fieldTarget.type)
        assertEquals(0, invocation.arguments.size)
        assertFalse(invocation.target.isImplicit)
        assertTrue(invocation.getInvokeOperatorFlag())
        assertEquals("classWithInvokeOperator()", invocation.asString())

        invocation = m.body.statements[3] as CtInvocation<*>
        fieldTarget = invocation.target as CtFieldRead<*>
        assertEquals(classWithInvokeOperator.reference, fieldTarget.type)
        assertEquals(1, invocation.arguments.size)
        assertFalse(invocation.target.isImplicit)
        assertTrue(invocation.getInvokeOperatorFlag())
        assertEquals("classWithInvokeOperator(1)", invocation.asString())

        invocation = m.body.statements[4] as CtInvocation<*>
        fieldTarget = invocation.target as CtFieldRead<*>
        assertEquals(classWithInvokeOperator.reference, fieldTarget.type)
        assertEquals(0, invocation.arguments.size)
        assertFalse(invocation.target.isImplicit)
        assertFalse(invocation.getInvokeOperatorFlag())
        assertEquals("classWithInvokeOperator.invoke()", invocation.asString())

        invocation = m.body.statements[5] as CtInvocation<*>
        fieldTarget = invocation.target as CtFieldRead<*>
        assertEquals(classWithInvokeOperator.reference, fieldTarget.type)
        assertEquals(1, invocation.arguments.size)
        assertFalse(invocation.target.isImplicit)
        assertFalse(invocation.getInvokeOperatorFlag())
        assertEquals("classWithInvokeOperator.invoke(2)", invocation.asString())
    }

    @Test
    fun testInvokeOperatorOnEntityNamedInvoke() {
        val c = TestBuildUtil.buildClass("spoon.test.invocation.testclasses","Invoker")
        val m = c.getMethodByName("edgeCases")
        val memberInvoke = c.getMethodByName("invoke")
        val invokeOperators = c.factory.Package().get("spoon.test.invocation.testclasses").
            getType<CtClass<*>>("ClassWithInvokeOperator").getMethodsByName("invoke")

        var invocation = m.body.statements[0] as CtInvocation<*>
        val target = invocation.target as CtInvocation<*>
        assertSame(memberInvoke, target.executable.declaration)
        assertSame(invokeOperators[0], invocation.executable.declaration)
        assertTrue(invocation.asString().matches("(invoke[(][)].invoke[(][)])|(invoke[(][)][(][)])".toRegex()))

        val localVar = m.body.statements[1] as CtLocalVariable<*>
        invocation = m.body.statements[2] as CtInvocation<*>
        var localTarget = (invocation.target as CtVariableRead<*>).variable.declaration
        assertSame(localVar, localTarget)
        assertSame(invokeOperators[0], invocation.executable.declaration)
        assertTrue(invocation.getInvokeOperatorFlag())
        assertEquals("invoke()", invocation.asString())


        invocation = m.body.statements[3] as CtInvocation<*>
        localTarget = (invocation.target as CtVariableRead<*>).variable.declaration
        assertSame(localVar, localTarget)
        assertSame(invokeOperators[1], invocation.executable.declaration)
        assertTrue(invocation.getInvokeOperatorFlag())
        assertEquals("invoke(1)", invocation.asString())

        invocation = m.body.statements[4] as CtInvocation<*>
        localTarget = (invocation.target as CtVariableRead<*>).variable.declaration
        assertSame(localVar, localTarget)
        assertSame(invokeOperators[0], invocation.executable.declaration)
        assertFalse(invocation.getInvokeOperatorFlag())
        assertEquals("invoke.invoke()", invocation.asString())
    }
}