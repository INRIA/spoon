package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.PropertySetterDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.UnaryOperatorKind

object OperatorHelper {
    private val binaryOperatorOriginSet by lazy { setOf(
        IrStatementOrigin.PLUS,
        IrStatementOrigin.MINUS,
        IrStatementOrigin.MUL,
        IrStatementOrigin.DIV,
        IrStatementOrigin.PERC,
        IrStatementOrigin.RANGE,
        IrStatementOrigin.ELVIS,
        IrStatementOrigin.LT, IrStatementOrigin.GT,
        IrStatementOrigin.LTEQ, IrStatementOrigin.GTEQ,
        IrStatementOrigin.EQEQ, IrStatementOrigin.EQEQEQ,
        IrStatementOrigin.EXCLEQ, IrStatementOrigin.EXCLEQEQ,
        IrStatementOrigin.IN, IrStatementOrigin.NOT_IN,
        IrStatementOrigin.ANDAND, IrStatementOrigin.OROR
    )
    }
    private val unaryOperatorOriginSet by lazy { setOf(
        IrStatementOrigin.UMINUS, IrStatementOrigin.UPLUS, IrStatementOrigin.EXCL
    )
    }

    private fun getReceiver(irCall: IrCall): IrExpression? {
        return irCall.extensionReceiver ?: irCall.dispatchReceiver
    }

    fun isBinaryOperator(origin: IrStatementOrigin?): Boolean =
        origin != null && origin in binaryOperatorOriginSet

    fun originToBinaryOperatorKind(origin: IrStatementOrigin): KtBinaryOperatorKind {
        return when(origin) {
            IrStatementOrigin.PLUS,
            IrStatementOrigin.PLUSEQ    -> KtBinaryOperatorKind.PLUS
            IrStatementOrigin.MINUS,
            IrStatementOrigin.MINUSEQ   -> KtBinaryOperatorKind.MINUS
            IrStatementOrigin.MUL,
            IrStatementOrigin.MULTEQ    -> KtBinaryOperatorKind.MUL
            IrStatementOrigin.DIV,
            IrStatementOrigin.DIVEQ     -> KtBinaryOperatorKind.DIV
            IrStatementOrigin.PERC,
            IrStatementOrigin.PERCEQ    -> KtBinaryOperatorKind.MOD
            IrStatementOrigin.RANGE     -> KtBinaryOperatorKind.RANGE
            IrStatementOrigin.ELVIS     -> KtBinaryOperatorKind.ELVIS
            IrStatementOrigin.LT        -> KtBinaryOperatorKind.LT
            IrStatementOrigin.GT        -> KtBinaryOperatorKind.GT
            IrStatementOrigin.LTEQ      -> KtBinaryOperatorKind.LE
            IrStatementOrigin.GTEQ      -> KtBinaryOperatorKind.GE
            IrStatementOrigin.EQEQ      -> KtBinaryOperatorKind.EQ
            IrStatementOrigin.EQEQEQ    -> KtBinaryOperatorKind.ID
            IrStatementOrigin.EXCLEQ    -> KtBinaryOperatorKind.NE
            IrStatementOrigin.EXCLEQEQ  -> KtBinaryOperatorKind.NID
            IrStatementOrigin.IN        -> KtBinaryOperatorKind.IN
            IrStatementOrigin.NOT_IN    -> KtBinaryOperatorKind.NOT_IN
            IrStatementOrigin.ANDAND    -> KtBinaryOperatorKind.AND
            IrStatementOrigin.OROR      -> KtBinaryOperatorKind.OR
            else -> throw SpoonIrBuildException("Unexpected IR origin for binary operator $origin")
        }
    }

    fun isUnaryOperator(origin: IrStatementOrigin?): Boolean =
        origin != null && origin in unaryOperatorOriginSet

    fun originToUnaryOperatorKind(origin: IrStatementOrigin): UnaryOperatorKind {
        return when(origin) {
            IrStatementOrigin.UPLUS -> UnaryOperatorKind.POS
            IrStatementOrigin.UMINUS -> UnaryOperatorKind.NEG
            IrStatementOrigin.EXCL -> UnaryOperatorKind.NOT
            IrStatementOrigin.POSTFIX_INCR -> UnaryOperatorKind.POSTINC
            IrStatementOrigin.POSTFIX_DECR -> UnaryOperatorKind.POSTDEC
            IrStatementOrigin.PREFIX_INCR -> UnaryOperatorKind.PREINC
            IrStatementOrigin.PREFIX_DECR -> UnaryOperatorKind.PREDEC
            else -> throw SpoonIrBuildException("Unexpected IR origin for unary operator $origin")
        }
    }

    fun getAugmentedAssignmentOperands(irExpr: IrExpression): Pair<IrElement, IrElement> {
        when(irExpr) {
            is IrBlock -> {
                return getAugmentedAssignmentOperands(irExpr.statements.firstIsInstance<IrCall>())
            }
            is IrCall -> { // LHS is local variable of type with opAssign
                val symbol = irExpr.symbol
                if(symbol.descriptor is PropertySetterDescriptor) {
                    return getAugmentedAssignmentOperands(irExpr.getValueArgument(0)!!)
                }
                return getReceiver(irExpr)!! to irExpr.getValueArgument(0)!!
            }
            is IrSetVariable -> {
                return getAugmentedAssignmentOperands(irExpr.value)
            }
        }
        throw SpoonIrBuildException("Unexpected tree structure of augmented assignment")
    }

    fun getOrderedBinaryOperands(lhs: IrExpression, rhs: IrExpression, operatorKind: KtBinaryOperatorKind): Pair<IrExpression,IrExpression> {
        if(operatorKind == KtBinaryOperatorKind.NOT_IN || operatorKind == KtBinaryOperatorKind.IN) return rhs to lhs
        return lhs to rhs
    }

}