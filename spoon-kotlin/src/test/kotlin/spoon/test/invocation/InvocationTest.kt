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

    @Test
    fun testNullCheck() {
        val c = TestBuildUtil.buildClass("spoon.test.invocation.testclasses","NullCheck")
        val m = c.getMethodByName("assertNotNull")
        val stmt = m.body.statements[0] as CtInvocation<*>
        val nullCheckFlag = stmt.target.getMetadata(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL) as Boolean?
        assertNotNull(nullCheckFlag)
        assertTrue(nullCheckFlag!!)
        assertEquals("(nullable)!!.compareTo(2)", stmt.asString())
    }

    @Test
    fun testSafeCall() {
        val c = TestBuildUtil.buildClass("spoon.test.invocation.testclasses","NullCheck")
        val m = c.getMethodByName("safeCall")
        val stmt = m.body.statements[0] as CtInvocation<*>
        val safeCall = stmt.getMetadata(KtMetadataKeys.ACCESS_IS_SAFE) as Boolean?
        assertNotNull(safeCall)
        assertTrue(safeCall!!)
        assertEquals("nullable?.compareTo(1)", stmt.asString())
    }

    @Test
    fun testNamedArgs() {
        val c = TestBuildUtil.buildClass("spoon.test.invocation.testclasses","NamedArgs")
        val m = c.getMethodByName("m2")

        fun CtExpression<*>.getName(): String? = getMetadata(KtMetadataKeys.NAMED_ARGUMENT) as String?

        // #1 No names
        var invocation = m.body.statements[0] as CtInvocation<*>
        assertEquals(2, invocation.arguments.size)
        for(a in invocation.arguments) {
            assertNull(a.getName())
        }
        assertEquals("m(1, \"1\")", invocation.asString())

        // #2 Same order as param declaration
        invocation = m.body.statements[1] as CtInvocation<*>
        assertEquals(2, invocation.arguments.size)
        var name = invocation.arguments[0].getName()
        assertNotNull(name)
        assertEquals("i", name)

        name = invocation.arguments[1].getName()
        assertNotNull(name)
        assertEquals("k", name)

        assertEquals("m(i = 2, k = \"2\")", invocation.asString())

        // #3 Different order than param declaration
        invocation = m.body.statements[2] as CtInvocation<*>
        assertEquals(2, invocation.arguments.size)
        name = invocation.arguments[0].getName()
        assertNotNull(name)
        assertEquals("k", name)

        name = invocation.arguments[1].getName()
        assertNotNull(name)
        assertEquals("i", name)

        assertEquals("m(k = \"3\", i = 3)", invocation.asString())

        // #4 Mixed named and unnamed
        invocation = m.body.statements[3] as CtInvocation<*>
        assertEquals(2, invocation.arguments.size)
        name = invocation.arguments[0].getName()
        assertNull(name)

        name = invocation.arguments[1].getName()
        assertNotNull(name)
        assertEquals("k", name)

        assertEquals("m(4, k = \"4\")", invocation.asString())
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