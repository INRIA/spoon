package spoon.test.binaryoperator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.reflect.declaration.CtType
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.visitor.filter.TypeFilter
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName

class BinaryOperatorTest {
    private val util = TestBuildUtil
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    private fun CtBinaryOperator<*>.ktKind() = getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND)
    private fun CtType<*>.getInitializer(s: String): CtExpression<*> = getField(s).defaultExpression

    @Test
    fun basicComparisonsTest() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","BasicComparisons")

        val comparisons = (c.methods.toList()[0].body.statements as List<CtReturn<*>>).map { it.returnedExpression as CtBinaryOperator<*> }
        assertEquals(8, comparisons.size)

        val ops = listOf(KtBinaryOperatorKind.LT, KtBinaryOperatorKind.GT, KtBinaryOperatorKind.EQ, KtBinaryOperatorKind.NE,
            KtBinaryOperatorKind.LE, KtBinaryOperatorKind.GE, KtBinaryOperatorKind.ID, KtBinaryOperatorKind.NID)
        val strings = listOf("x < y", "y > x", "1 == 2", "2 != 1", "x <= 2",  "3 >= y", "x === y", "x !== y")
        for(i in 0..7) {
            assertEquals(ops[i], comparisons[i].ktKind())
            assertEquals(strings[i], pp.prettyprint(comparisons[i]))
        }
    }

    @Test
    fun operatorComparison() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","OperatorComparison")
        val expressions = c.getMethodByName("m").body.statements
        assertEquals(3, expressions.size)
        val binOps = expressions.map { (it as CtReturn<*>).returnedExpression } as List<CtBinaryOperator<*>>

        assertEquals(KtBinaryOperatorKind.LT, binOps[0].ktKind())
        assertEquals(KtBinaryOperatorKind.EQ, binOps[1].ktKind())
        assertEquals(KtBinaryOperatorKind.GT, binOps[2].ktKind())

        assertTrue(binOps.all { it.leftHandOperand is CtVariableRead<*> && it.rightHandOperand is CtVariableRead<*> })
        assertEquals("x < y", binOps[0].asString())
        assertEquals("x == y", binOps[1].asString())
        assertEquals("x > y", binOps[2].asString())
    }

    @Test
    fun inOperatorTest() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","InOperator")

        val statements = c.methods.toList()[0].body.statements
        assertEquals(3, statements.size)

        val i1 = (statements[0] as CtReturn<*>).returnedExpression as CtBinaryOperator<*>
        val i2 = (statements[1] as CtReturn<*>).returnedExpression as CtBinaryOperator<*>

        assertEquals(KtBinaryOperatorKind.IN, i1.ktKind())
        assertEquals(KtBinaryOperatorKind.NOT_IN, i2.ktKind())
        assertTrue(statements[2] is CtInvocation<*>)

        assertEquals("1 in l", pp.prettyprint(i1))
        assertEquals("2 !in l", pp.prettyprint(i2))

        // Explicit "l.contains(x)" should not be translated to "x in l"
        assertEquals("l.contains(3)", pp.prettyprint(statements[2]))
    }

    @Test
    fun testExplicitAssignments() {
        /*
        * Assignment operators are used on classes with explicit opAssign functions (ex. plusAssign for +=)
        * Reason for this test is that IR has generated code that differs depending on if target class has plusAssign()
        * or just plus() member functions.
        *
        * a has plusAssign: a += other >translates to> { a.plusAssign(other) } (this test)
        * a has plus: a += other >translates to> { a = a.plus(other) } (testImplicitAssignments)
         */
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","ExplicitAssignmentOperators")

        val statements = (c.methods.toList()[0].body.statements)
        assertEquals(6, statements.size)

        val expectedOperators = listOf(BinaryOperatorKind.PLUS, BinaryOperatorKind.MINUS, BinaryOperatorKind.MUL, BinaryOperatorKind.DIV,
            BinaryOperatorKind.MOD)
        val expectedStrings = listOf("x += 1", "x -= 2", "x *= 3", "x /= 4", "x %= 5",
            "x == spoon.test.binaryoperator.testclasses.HasOnlyAssignOperators(6)")

        for(i in 0..4) {
            assertEquals(expectedOperators[i], (statements[i] as CtOperatorAssignment<*,*>).kind)
            assertEquals(expectedStrings[i], pp.prettyprint(statements[i]))
        }
        assertEquals(KtBinaryOperatorKind.EQ, ((statements[5] as CtReturn<*>).returnedExpression as CtBinaryOperator<*>).ktKind())
        assertEquals(expectedStrings[5], pp.prettyprint(statements[5]))
    }

    @Test
    fun testImplicitAssignments() {
        /*
        * Assignment operators are used on classes with implicit opAssign functions (ex. plusAssign for +=)
        * Reason for this test is that IR has generated code that differs depending on if target class has plusAssign()
        * or just plus() member functions.
        *
        * a has plusAssign: a += other >translates to> { a.plusAssign(other) } (testExplicitAssignments)
        * a has plus: a += other >translates to> { a = a.plus(other) } (this test)
         */
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","ImplicitAssignmentOperators")

        val statements = (c.methods.toList()[0].body.statements)
        assertEquals(6, statements.size)

        val expectedOperators = listOf(BinaryOperatorKind.PLUS, BinaryOperatorKind.MINUS, BinaryOperatorKind.MUL, BinaryOperatorKind.DIV,
            BinaryOperatorKind.MOD)
        val expectedStrings = listOf("x += 1", "x -= 2", "x *= 3", "x /= 4", "x %= 5",
            "x == spoon.test.binaryoperator.testclasses.HasOnlyNormalOperators(6)")

        for(i in 0..4) {
            assertEquals(expectedOperators[i], (statements[i] as CtOperatorAssignment<*,*>).kind)
            assertEquals(expectedStrings[i], pp.prettyprint(statements[i]))
        }
        assertEquals(KtBinaryOperatorKind.EQ, ((statements[5] as CtReturn<*>).returnedExpression as CtBinaryOperator<*>).ktKind())
        assertEquals(expectedStrings[5], pp.prettyprint(statements[5]))
    }

    @Test
    fun testTypeOperators() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","TypeOperators")

        assertEquals(8, c.fields.size)

        val pkg = "spoon.test.binaryoperator.testclasses"

        var propertyInitializer = c.getInitializer("y")
        assertEquals("${pkg}.Derived", pp.prettyprint(propertyInitializer.type))
        assertEquals(1, propertyInitializer.typeCasts.size)
        assertEquals("${pkg}.Base?", pp.prettyprint(propertyInitializer.typeCasts[0]))
        assertEquals("(x as $pkg.Base?)", pp.prettyprint(propertyInitializer))

        propertyInitializer = c.getInitializer("ySafe")
        assertEquals("${pkg}.Derived", pp.prettyprint(propertyInitializer.type))
        assertEquals(1, propertyInitializer.typeCasts.size)
        assertEquals("$pkg.Base", pp.prettyprint(propertyInitializer.typeCasts[0]))
        assertEquals("(x as? $pkg.Base)", pp.prettyprint(propertyInitializer))

        propertyInitializer = c.getInitializer("z")
        assertEquals("${pkg}.Derived", pp.prettyprint(propertyInitializer.type))
        assertEquals(2, propertyInitializer.typeCasts.size)
        assertEquals("$pkg.Base", pp.prettyprint(propertyInitializer.typeCasts[0]))
        assertEquals("$pkg.Derived", pp.prettyprint(propertyInitializer.typeCasts[1]))
        assertEquals("(x as $pkg.Base as $pkg.Derived)", pp.prettyprint(propertyInitializer))

        propertyInitializer = c.getInitializer("zSafe")
        assertEquals("${pkg}.Derived", pp.prettyprint(propertyInitializer.type))
        assertEquals(2, propertyInitializer.typeCasts.size)
        assertEquals("$pkg.Base", pp.prettyprint(propertyInitializer.typeCasts[0]))
        assertEquals("$pkg.Derived", pp.prettyprint(propertyInitializer.typeCasts[1]))
        assertEquals( "(x as? $pkg.Base as? $pkg.Derived)", pp.prettyprint(propertyInitializer))

        propertyInitializer = c.getInitializer("base")
        assertEquals("${pkg}.Derived", propertyInitializer.type.qualifiedName)
        assertEquals( "x", pp.prettyprint(propertyInitializer))

        var isOperator = c.getInitializer("i") as CtBinaryOperator<*>
        assertEquals("kotlin.Boolean", isOperator.type.qualifiedName)
        assertEquals("$pkg.Base", pp.prettyprint((isOperator.rightHandOperand as CtTypeAccess<*>).accessedType))
        assertEquals( "x is $pkg.Base", pp.prettyprint(isOperator))

        isOperator = c.getInitializer("i2") as CtBinaryOperator<*>
        assertEquals("kotlin.Boolean", isOperator.type.qualifiedName)
        assertEquals("$pkg.Base?", pp.prettyprint((isOperator.rightHandOperand as CtTypeAccess<*>).accessedType))
        assertEquals( "x !is $pkg.Base?", pp.prettyprint(isOperator))
    }

    @Test
    fun testElvisOperator() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","ElvisOperator")

        var op = c.getInitializer("b1") as CtBinaryOperator<*>
        assertEquals(c.factory.Type().createReference<CtTypeReference<Int>>("kotlin.Int"), op.type)
        assertEquals(KtBinaryOperatorKind.ELVIS, op.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND))
        assertEquals("b ?: 0", pp.prettyprint(op))

        op = c.getInitializer("b2") as CtBinaryOperator<*>
        assertEquals(c.factory.Type().createReference<CtTypeReference<Int>>("kotlin.Int"), op.type)
        assertEquals(KtBinaryOperatorKind.ELVIS, op.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND))
        assertEquals("b ?: (if (b == null) 1 else b1)", pp.prettyprint(op))
    }

    @Test
    fun testRangeOperator() {
        val c = util.buildClass("spoon.test.binaryoperator.testclasses","RangeOperator")

        val op = c.getInitializer("r") as CtBinaryOperator<*>
        assertEquals(c.factory.Type().createReference<CtTypeReference<Int>>("kotlin.ranges.IntRange"), op.type)
        assertEquals(KtBinaryOperatorKind.RANGE, op.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND))
        assertEquals("1..3", pp.prettyprint(op))
    }


    @Test
    fun testGetAndSetOperator() {
        val c = TestBuildUtil.buildClass("spoon.test.binaryoperator.testclasses","GetSetOperators")
        val m = c.getMethodByName("m")

        fun CtArrayAccess<*,*>.getIndexArgs() = getMetadata(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS) as List<CtExpression<*>>

        assertEquals(2, m.body.statements.size)
        val setCalls = m.getElements(TypeFilter(CtArrayWrite::class.java)) as List<CtArrayWrite<*>>
        assertEquals(1, setCalls.size)
        assertEquals("kotlin.String", setCalls[0].type.qualifiedName)
        assertEquals("kotlin.Int", setCalls[0].getIndexArgs()[0].type.qualifiedName)


        val getCalls = m.getElements(TypeFilter(CtArrayRead::class.java)) as List<CtArrayRead<*>>
        assertEquals(2, getCalls.size)
        assertEquals("kotlin.String", getCalls[0].type.qualifiedName)
        assertEquals("kotlin.Int", getCalls[0].getIndexArgs()[0].type.qualifiedName)
        assertEquals("kotlin.String", getCalls[1].type.qualifiedName)
        assertEquals("kotlin.Int", getCalls[1].getIndexArgs()[0].type.qualifiedName)

        assertEquals("l[0]", getCalls[0].asString())
        assertEquals("l[1]", setCalls[0].asString())
        assertEquals("l[2]", getCalls[1].asString())
    }

    @Test
    fun testBuildGetSetWithMultiParams() {
        val c = TestBuildUtil.buildClass("spoon.test.binaryoperator.testclasses","GetSetOperators")
        val m = c.getMethodByName("multiParam")

        // explicit set() and get() should be invocation
        val getSetInvocations = m.getElements(TypeFilter(CtInvocation::class.java))
        assertEquals(2, getSetInvocations.size)
        assertEquals("list.get(0, \"1\")", getSetInvocations[0].asString())
        assertEquals("list.set(1, \"2\", 15)", getSetInvocations[1].asString())

        val setCalls = m.getElements(TypeFilter(CtArrayWrite::class.java)) as List<CtArrayWrite<*>>
        assertEquals(1, setCalls.size)
        val getCalls = m.getElements(TypeFilter(CtArrayRead::class.java)) as List<CtArrayRead<*>>
        assertEquals(1, getCalls.size)

        val setArgs = setCalls[0].getMetadata(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS) as List<CtExpression<*>>
        assertEquals(2, setArgs.size)
        assertEquals("kotlin.Int", setArgs[0].type.qualifiedName)
        assertEquals("kotlin.String", setArgs[1].type.qualifiedName)

        val getArgs = getCalls[0].getMetadata(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS) as List<CtExpression<*>>
        assertEquals(2, getArgs.size)
        assertEquals("kotlin.Int", getArgs[0].type.qualifiedName)
        assertEquals("kotlin.String", getArgs[1].type.qualifiedName)

        assertEquals("list[2, \"3\"]", getCalls[0].asString())
        assertEquals("list[3, \"4\"]", setCalls[0].asString())
    }

    @Test
    fun testBuildLogicalOperators() {
        val c = TestBuildUtil.buildClass("spoon.test.binaryoperator.testclasses","LogicalOperators")
        val m = c.getMethodByName("m")
        assertTrue(m.body.statements.all { it is CtReturn<*> })
        val statements = m.body.statements.map { (it as CtReturn<*>).returnedExpression as CtBinaryOperator<*> }

        val x = c.getField("x")
        val y = c.getField("y")
        val z = c.getField("z")

        assertTrue(statements.all { it.type.qualifiedName == "kotlin.Boolean" })

        // x && y
        var op = statements[0]
        assertTrue(op.leftHandOperand is CtFieldRead<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        var lhsVar = op.leftHandOperand as CtFieldRead<*>
        var rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(x, lhsVar.variable.declaration)
        assertSame(y, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("x && y", op.asString())

        // y && x
        op = statements[1]
        assertTrue(op.leftHandOperand is CtFieldRead<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        lhsVar = op.leftHandOperand as CtFieldRead<*>
        rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(y, lhsVar.variable.declaration)
        assertSame(x, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("y && x", op.asString())

        // x && y && z
        op = statements[2]
        assertTrue(op.leftHandOperand is CtBinaryOperator<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        var innerOp = op.leftHandOperand as CtBinaryOperator<*>
        assertEquals("kotlin.Boolean", innerOp.type.qualifiedName)
        assertTrue(innerOp.leftHandOperand is CtFieldRead<*>)
        assertTrue(innerOp.rightHandOperand is CtFieldRead<*>)

        lhsVar = innerOp.leftHandOperand as CtFieldRead<*>
        var midVar = innerOp.rightHandOperand as CtFieldRead<*>
        rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(x, lhsVar.variable.declaration)
        assertSame(y, midVar.variable.declaration)
        assertSame(z, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", midVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("(x && y) && z", op.asString())

        // x && y
        op = statements[3]
        assertTrue(op.leftHandOperand is CtFieldRead<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        lhsVar = op.leftHandOperand as CtFieldRead<*>
        rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(x, lhsVar.variable.declaration)
        assertSame(y, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("x || y", op.asString())

        // y && x
        op = statements[4]
        assertTrue(op.leftHandOperand is CtFieldRead<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        lhsVar = op.leftHandOperand as CtFieldRead<*>
        rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(y, lhsVar.variable.declaration)
        assertSame(x, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("y || x", op.asString())

        // x || y || z
        op = statements[5]
        assertTrue(op.leftHandOperand is CtBinaryOperator<*>)
        assertTrue(op.rightHandOperand is CtFieldRead<*>)

        innerOp = op.leftHandOperand as CtBinaryOperator<*>
        assertEquals("kotlin.Boolean", innerOp.type.qualifiedName)
        assertTrue(innerOp.leftHandOperand is CtFieldRead<*>)
        assertTrue(innerOp.rightHandOperand is CtFieldRead<*>)

        lhsVar = innerOp.leftHandOperand as CtFieldRead<*>
        midVar = innerOp.rightHandOperand as CtFieldRead<*>
        rhsVar = op.rightHandOperand as CtFieldRead<*>

        assertSame(x, lhsVar.variable.declaration)
        assertSame(y, midVar.variable.declaration)
        assertSame(z, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", midVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("(x || y) || z", op.asString())

        // x && (y || z)
        op = statements[6]
        assertTrue(op.leftHandOperand is CtFieldRead<*>)
        assertTrue(op.rightHandOperand is CtBinaryOperator<*>)

        innerOp = op.rightHandOperand as CtBinaryOperator<*>
        assertEquals("kotlin.Boolean", innerOp.type.qualifiedName)
        assertTrue(innerOp.leftHandOperand is CtFieldRead<*>)
        assertTrue(innerOp.rightHandOperand is CtFieldRead<*>)

        lhsVar = op.leftHandOperand as CtFieldRead<*>
        midVar = innerOp.leftHandOperand as CtFieldRead<*>
        rhsVar = innerOp.rightHandOperand as CtFieldRead<*>

        assertSame(x, lhsVar.variable.declaration)
        assertSame(y, midVar.variable.declaration)
        assertSame(z, rhsVar.variable.declaration)

        assertEquals("kotlin.Boolean", lhsVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", midVar.type.qualifiedName)
        assertEquals("kotlin.Boolean", rhsVar.type.qualifiedName)
        assertEquals("x && (y || z)", op.asString())
    }

    @Test
    fun testOverriddenEquals() {
        val pkg = "spoon.test.binaryoperator.testclasses"
        val f = TestBuildUtil.buildFile(pkg, "OverriddenEquals")
        val withOp = f.Package().get(pkg).getType<CtType<*>>("OverriddenOperatorEquals").getMethodByName("equals")
        val withoutOp = f.Package().get(pkg).getType<CtType<*>>("OverriddenNonOperatorEquals").getMethodByName("equals")

        var modifiers = withOp.getMetadata(KtMetadataKeys.KT_MODIFIERS) as Set<KtModifierKind>
        assertTrue(KtModifierKind.OPERATOR in modifiers)

        modifiers = withoutOp.getMetadata(KtMetadataKeys.KT_MODIFIERS) as Set<KtModifierKind>
        assertTrue(KtModifierKind.OPERATOR !in modifiers)
    }
}