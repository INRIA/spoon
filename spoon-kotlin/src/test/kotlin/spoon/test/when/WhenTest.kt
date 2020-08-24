package spoon.test.`when`

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.*
import spoon.reflect.declaration.CtType
import spoon.test.TestBuildUtil
import spoon.test.asString
import spoon.test.getMethodByName
import kotlin.test.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenTest {
    lateinit var simpleWhen: CtType<*>

    @BeforeAll
    fun init() {
        simpleWhen = TestBuildUtil.buildClass("spoon.test.when.testclasses", "SimpleWhen")
    }

    @Test
    fun testBuildWhenStatement() {
        val whenStatement = simpleWhen.getMethodByName("whenStatement").body.statements[0]
        assertTrue(whenStatement is CtSwitch<*>)

        val subject = whenStatement.selector
        assertTrue(subject is CtFieldRead<*>)
        assertEquals("kotlin.Int", subject.type.qualifiedName)
        assertEquals("x", subject.asString())

        assertEquals(
            """when (x) {
                |    1, 2 -> {}
                |    in l, is kotlin.Number -> {}
                |    !in l, !is kotlin.Number -> {}
                |}
            """.trimMargin(),
            whenStatement.asString().replace(System.lineSeparator(), "\n")
        )
    }

    @Test
    fun testBuildBranchCondition() {
        val whenStatement = simpleWhen.getMethodByName("whenStatement").body.statements[0]
        assertTrue(whenStatement is CtSwitch<*>)
        assertEquals(3, whenStatement.cases.size)
        val branch1 = whenStatement.cases[0]
        assertEquals(2, branch1.caseExpressions.size)
        assertTrue(branch1.caseExpressions.all { it is CtLiteral<*> && it.type.qualifiedName == "kotlin.Int" })
        assertEquals("1, 2 -> {}", branch1.asString())

        val branch2 = whenStatement.cases[1]
        assertEquals(2, branch2.caseExpressions.size)
        var cond = branch2.caseExpressions[0]
        assertTrue(cond is CtBinaryOperator<*>)
        assertEquals(KtBinaryOperatorKind.IN, cond.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind)
        assertTrue(cond.leftHandOperand.isImplicit)

        cond = branch2.caseExpressions[1]
        assertTrue(cond is CtBinaryOperator<*>)
        assertEquals(KtBinaryOperatorKind.IS, cond.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind)
        assertTrue(cond.leftHandOperand.isImplicit)

        assertEquals("in l, is kotlin.Number -> {}", branch2.asString())

        val branch3 = whenStatement.cases[2]
        assertEquals(2, branch2.caseExpressions.size)
        cond = branch3.caseExpressions[0]
        assertTrue(cond is CtBinaryOperator<*>)
        assertEquals(KtBinaryOperatorKind.NOT_IN, cond.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind)
        assertTrue(cond.leftHandOperand.isImplicit)

        cond = branch3.caseExpressions[1]
        assertTrue(cond is CtBinaryOperator<*>)
        assertEquals(KtBinaryOperatorKind.IS_NOT, cond.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind)
        assertTrue(cond.leftHandOperand.isImplicit)

        assertEquals("!in l, !is kotlin.Number -> {}", branch3.asString())
    }

    @Test
    fun testBuildWhenExpression() {
        val whenResult = simpleWhen.getMethodByName("whenExpression").body.statements[0]
        assertTrue(whenResult is CtLocalVariable<*>)
        val whenExpression = whenResult.defaultExpression
        assertTrue(whenExpression is CtSwitchExpression<*,*>)
        assertEquals(2, whenExpression.cases.size)
        val branch1 = whenExpression.cases[0]
        assertEquals(2, branch1.caseExpressions.size)
        assertEquals("1, 2 -> 3", branch1.asString())
        val branch2 = whenExpression.cases[1]
        assertEquals(0, branch2.caseExpressions.size)
        val eol = System.lineSeparator()
        assertEquals("else -> {$eol    99$eol}", branch2.asString())

        val subject = whenExpression.selector
        assertTrue(subject is CtFieldRead<*>)
        assertEquals("kotlin.Int", subject.type.qualifiedName)
        assertEquals("x", subject.asString())

        assertEquals(
            """val whenResult = when (x) {
                |    1, 2 -> 3
                |    else -> {
                |        99
                |    }
                |}
            """.trimMargin(),
            whenResult.asString().replace(System.lineSeparator(), "\n")
        )
    }

    @Test
    fun testBuildWhenWithoutSubject() {
        val whenStmt = simpleWhen.getMethodByName("whenWithoutSubject").body.statements[0]
        assertTrue(whenStmt is CtSwitch<*>)
        assertNull(whenStmt.selector)

        assertEquals(2, whenStmt.cases.size)
        val branch1 = whenStmt.cases[0]
        assertEquals(1, branch1.caseExpressions.size)
        val cond = branch1.caseExpressions[0]
        assertTrue(cond is CtBinaryOperator<*>)
        assertEquals(KtBinaryOperatorKind.OR, cond.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind)
        assertTrue(cond.leftHandOperand is CtBinaryOperator<*>)
        assertTrue(cond.rightHandOperand is CtBinaryOperator<*>)
        val branch2 = whenStmt.cases[1]
        assertEquals(0, branch2.caseExpressions.size)

        assertEquals("(x < 10) || (x == 14) -> {}", branch1.asString())
    }

    @Test
    fun testBuildEmptyWhen() {
        val whenStmt = simpleWhen.getMethodByName("emptyWhen").body.statements[0]
        assertTrue(whenStmt is CtAbstractSwitch<*>)
        assertNull(whenStmt.selector)
        assertEquals(0, whenStmt.cases.size)
        assertTrue(whenStmt.asString().matches("when [{]\\s*[}]".toRegex()))
    }

    @Test
    fun testBuildEmptyWhenWithSubject() {
        val whenStmt = simpleWhen.getMethodByName("emptyWhenWithLocalSubject").body.statements[0]
        assertTrue(whenStmt is CtAbstractSwitch<*>)
        assertNotNull(whenStmt.selector)
        assertEquals(0, whenStmt.cases.size)
        assertTrue(whenStmt.asString().matches("when [(]val x = 0[)] [{]\\s*[}]".toRegex()))
    }

    @Test
    fun testBuildNestedWhen() {
        val whenStmt = simpleWhen.getMethodByName("nestedWhens").body.statements[0]
        assertTrue(whenStmt is CtAbstractSwitch<*>)
        assertEquals(1, whenStmt.cases.size)
        val outerSubject = (whenStmt.selector as KtStatementExpression).statement
        assertTrue(outerSubject is CtLocalVariable<*>)
        val innerWhen = (whenStmt.cases[0].statements[0] as CtBlock<*>).statements[0]
        assertTrue(innerWhen is CtAbstractSwitch<*>)
        assertEquals(1, innerWhen.cases.size)
        val innerBranch = innerWhen.cases[0]
        assertEquals(3, innerBranch.caseExpressions.size)
        assertEquals("is kotlin.String", innerBranch.caseExpressions[0].asString())
        assertEquals("in listOf(\"\")", innerBranch.caseExpressions[1].asString())
        assertEquals("outer.toString()", innerBranch.caseExpressions[2].asString())
    }

    @Test
    fun testBuildWhenWithBooleanSubject() {
        val whenStmt = simpleWhen.getMethodByName("whenWithBooleanSubject").body.statements[0]
        assertTrue(whenStmt is CtAbstractSwitch<*>)
        val conditions = whenStmt.cases[0].caseExpressions
        assertEquals("in listOf(false)", conditions[0].asString())
        assertEquals("(b in listOf(false)) || (b is kotlin.Any)", conditions[1].asString())
        assertEquals("is kotlin.Any", conditions[2].asString())
    }

    @Test
    fun testExhaustiveWhenHasNoElseBranch() {
        // Contract: An exhaustive when should not have an else branch
        val c = TestBuildUtil.buildClass("spoon.test.when.testclasses", "ExhaustiveWhen")
        val whenStmt = c.getMethodByName("m").body.statements[0]
        assertTrue(whenStmt is CtAbstractSwitch<*>)
        assertEquals(2, whenStmt.cases.size)
        assertTrue(whenStmt.cases[0].caseExpressions.isNotEmpty())
        assertTrue(whenStmt.cases[1].caseExpressions.isNotEmpty())
    }
}