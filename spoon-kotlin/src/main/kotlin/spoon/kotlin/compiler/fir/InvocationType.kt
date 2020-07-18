package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.UnaryOperatorKind

internal sealed class InvocationType {
    data class UNKNOWN(val functionCall: FirFunctionCall): InvocationType()
    data class NORMAL_CALL(val receiver: FirExpression?, val function: FirFunctionCall) : InvocationType()
    data class INFIX_CALL(val lhs: FirElement, val function: FirFunctionCall, val rhs: FirElement) : InvocationType()
    data class BINARY_OPERATOR(val lhs: FirElement, val kind: KtBinaryOperatorKind, val rhs: FirElement, val originalFunction: FirFunctionCall) : InvocationType()
    data class ASSIGNMENT_OPERATOR(val lhs: FirElement, val kind: KtBinaryOperatorKind, val rhs: FirElement, val originalFunction: FirFunctionCall) : InvocationType()
    data class POSTFIX_OPERATOR(val operand: FirElement, val kind: UnaryOperatorKind, val originalFunction: FirFunctionCall) : InvocationType()
    data class PREFIX_OPERATOR(val kind: UnaryOperatorKind, val operand: FirElement, val originalFunction: FirFunctionCall) : InvocationType()
    data class GET_OPERATOR(val receiver: FirExpression, val args: List<FirExpression>, val originalFunction: FirFunctionCall) : InvocationType() // a[x]
    data class SET_OPERATOR(val receiver: FirExpression, val args: List<FirExpression>, val rhs: FirExpression, val originalFunction: FirFunctionCall) : InvocationType() // a[x] = b
}
