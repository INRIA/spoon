package spoon.test.unaryoperator

import org.junit.jupiter.api.Test
import spoon.kotlin.reflect.visitor.printing.DefaultKotlinPrettyPrinter
import spoon.kotlin.reflect.visitor.printing.DefaultPrinterAdapter
import spoon.reflect.code.CtExpression
import spoon.reflect.code.CtUnaryOperator
import spoon.test.TestBuildUtil
import spoon.test.unaryoperator.testclasses.HasAllUnaryOperators
import org.junit.jupiter.api.Assertions.*

class UnaryOperatorTest {
    private val util = TestBuildUtil
    private val pp = DefaultKotlinPrettyPrinter(DefaultPrinterAdapter())

    @Test
    fun testAllUnaryOperators() {
        val c = util.buildClass("spoon.test.unaryoperator.testclasses", "AllUnaryOperators")
        val allOperatorStatements = c.methods.toList()[0].body.statements
        val operandType = c.factory.Type().createReference(HasAllUnaryOperators::class.java)
        assertEquals(7, allOperatorStatements.size)

        var s = allOperatorStatements[0] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("+h", pp.prettyprint(s))

        s = allOperatorStatements[1] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("-h", pp.prettyprint(s))

        s = allOperatorStatements[2] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Boolean", s.type.qualifiedName)
        assertEquals("!h", pp.prettyprint(s))

        s = allOperatorStatements[3] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("++h", pp.prettyprint(s))

        s = allOperatorStatements[4] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("--h", pp.prettyprint(s))

        s = allOperatorStatements[5] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("h++", pp.prettyprint(s))

        s = allOperatorStatements[6] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals(operandType, s.type)
        assertEquals("h--", pp.prettyprint(s))
    }

    @Test
    fun testIntOperators() {
        val c = util.buildClass("spoon.test.unaryoperator.testclasses", "AllUnaryOperators")
        val allOperatorStatements = c.methods.toList()[1].body.statements
        assertEquals(6, allOperatorStatements.size)

        var s = allOperatorStatements[0] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("+i", pp.prettyprint(s))

        s = allOperatorStatements[1] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("-i", pp.prettyprint(s))

        s = allOperatorStatements[2] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("++i", pp.prettyprint(s))

        s = allOperatorStatements[3] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("--i", pp.prettyprint(s))

        s = allOperatorStatements[4] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("i--", pp.prettyprint(s))

        s = allOperatorStatements[5] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Int", s.type.qualifiedName)
        assertEquals("i++", pp.prettyprint(s))
    }

    @Test
    fun testNotOperator() {
        val c = util.buildClass("spoon.test.unaryoperator.testclasses", "AllUnaryOperators")
        val allOperatorStatements = c.methods.toList()[2].body.statements
        assertEquals(2, allOperatorStatements.size)

        var s = allOperatorStatements[0] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Boolean", s.type.qualifiedName)
        assertEquals("!b", pp.prettyprint(s))

        s = allOperatorStatements[1] as CtExpression<*>
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Boolean", s.type.qualifiedName)
        assertEquals("!(!b)", pp.prettyprint(s))

        s = (s as CtUnaryOperator<*>).operand
        assertTrue(s is CtUnaryOperator<*>)
        assertEquals("kotlin.Boolean", s.type.qualifiedName)
        assertEquals("(!b)", pp.prettyprint(s))
    }
}