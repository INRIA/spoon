package spoon.kotlin.reflect.code

import org.jetbrains.kotlin.fir.expressions.FirOperation
import org.jetbrains.kotlin.fir.expressions.LogicOperationKind
import spoon.reflect.code.BinaryOperatorKind

enum class KtBinaryOperatorKind(val asString: String) {
    /**
     * Logical or.
     */
    OR("||"),
    /**
     * Logical and.
     */
    AND("&&"),
    /**
     * Equality.
     */
    EQ("=="),
    /**
     * Inequality.
     */
    NE("!="),
    /**
     * Identity (reference equality)
     */
    ID("==="),
    /**
     * Not identity (reference inequality)
     */
    NID("!=="),
    /**
     * Lower than comparison.
     */
    LT("<"),
    /**
     * Greater than comparison.
     */
    GT(">"),
    /**
     * Lower or equal comparison.
     */
    LE("<="),
    /**
     * Greater or equal comparison.
     */
    GE(">="),
    /**
     * Addition.
     */
    PLUS("+"),
    /**
     * Subtraction.
     */
    MINUS("-"),
    /**
     * Multiplication.
     */
    MUL("*"),
    /**
     * Division.
     */
    DIV("/"),
    /**
     * Modulo.
     */
    MOD("%"),
    /**
     * Is (type operation)
     */
    IS("is"),
    /**
     * Is not (!is)
     */
    IS_NOT("!is"),
    /**
     * Contains
     */
    IN("in"),
    /**
     * Not contains
     */
    NOT_IN("!in"),
    /**
     * Range ( a..b )
     */
    RANGE(".."),
    /**
     * Elvis (default if null)
     */
    ELVIS("?:");

    override fun toString(): String {
        return asString
    }

    fun toJavaAssignmentOperatorKind(): BinaryOperatorKind = when(this) {
        PLUS -> BinaryOperatorKind.PLUS
        MINUS -> BinaryOperatorKind.MINUS
        MOD -> BinaryOperatorKind.MOD
        MUL -> BinaryOperatorKind.MUL
        DIV -> BinaryOperatorKind.DIV
        else -> throw RuntimeException("Invalid assignment operator: $this")
    }

    fun asToken() = when(this) {
        RANGE -> this.asString
        else -> " $this "
    }

    companion object {
        fun fromJavaOperatorKind(j: BinaryOperatorKind): KtBinaryOperatorKind = when(j) {
            BinaryOperatorKind.PLUS -> PLUS
            BinaryOperatorKind.MINUS -> MINUS
            BinaryOperatorKind.MOD -> MOD
            BinaryOperatorKind.MUL -> MUL
            BinaryOperatorKind.DIV -> DIV
            BinaryOperatorKind.AND -> AND
            BinaryOperatorKind.OR -> OR
            else -> throw RuntimeException("Invalid assignment operator: $this")
        }

        internal fun fromFirOperation(firOperation: FirOperation): KtBinaryOperatorKind = when(firOperation) {
            FirOperation.EQ -> EQ
            FirOperation.NOT_EQ -> NE
            FirOperation.LT -> LT
            FirOperation.GT -> GT
            FirOperation.LT_EQ -> LE
            FirOperation.GT_EQ -> GE
            FirOperation.IS -> IS
            FirOperation.NOT_IS -> IS_NOT
            FirOperation.NOT_IDENTITY -> NID
            FirOperation.IDENTITY -> ID
            else -> throw RuntimeException("Invalid operator for operatorCall: $this")
        }

        internal fun firLogicOperationToJavaBinOp(logicOperationKind: LogicOperationKind): BinaryOperatorKind =
            when(logicOperationKind) {
                LogicOperationKind.AND -> BinaryOperatorKind.AND
                LogicOperationKind.OR -> BinaryOperatorKind.OR
            }

        internal fun firLogicOperationToKtBinOp(logicOperationKind: LogicOperationKind): KtBinaryOperatorKind =
            when(logicOperationKind) {
                LogicOperationKind.AND -> AND
                LogicOperationKind.OR -> OR
            }
    }
}