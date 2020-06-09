package spoon.test.binaryoperator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.*
import spoon.test.TestBuildUtil

class BinaryOperatorTest {
    private val util = TestBuildUtil()
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    private fun CtBinaryOperator<*>.ktKind() = getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND)

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
        * Reason for this test is that FIR has generated code that differs depending on if target class has plusAssign()
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
        val expectedStrings = listOf("x += 1", "x -= 2", "x *= 3", "x /= 4", "x %= 5", "x == HasOnlyAssignOperators(6)")

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
        * Reason for this test is that FIR has generated code that differs depending on if target class has plusAssign()
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
        val expectedStrings = listOf("x += 1", "x -= 2", "x *= 3", "x /= 4", "x %= 5", "x == HasOnlyNormalOperators(6)")

        for(i in 0..4) {
            assertEquals(expectedOperators[i], (statements[i] as CtOperatorAssignment<*,*>).kind)
            assertEquals(expectedStrings[i], pp.prettyprint(statements[i]))
        }
        assertEquals(KtBinaryOperatorKind.EQ, ((statements[5] as CtReturn<*>).returnedExpression as CtBinaryOperator<*>).ktKind())
        assertEquals(expectedStrings[5], pp.prettyprint(statements[5]))
    }
}