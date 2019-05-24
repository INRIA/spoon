package fr.inria.spoon.dataflow.utils;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import spoon.reflect.reference.CtTypeReference;

public final class PromotionUtils
{
    private PromotionUtils() {}

    /**
     * Extends bit-vector to the size of int (32 bits).
     */
    private static BitVecExpr extendToInteger(Context context, BitVecExpr bitVector, boolean isSigned)
    {
        if (isSigned)
        {
            return context.mkSignExt(32 - bitVector.getSortSize(), bitVector);
        }
        else
        {
            return context.mkZeroExt(32 - bitVector.getSortSize(), bitVector);
        }
    }

    /**
     * Extends bit-vector to the size of long (64 bits).
     */
    private static BitVecExpr extendToLong(Context context, BitVecExpr bitVector, boolean isSigned)
    {
        if (isSigned)
        {
            return context.mkSignExt(64 - bitVector.getSortSize(), bitVector);
        }
        else
        {
            return context.mkZeroExt(64 - bitVector.getSortSize(), bitVector);
        }
    }

    /**
     * Performs Unary Numeric Promotion:
     * If operand is of type long it remains long.
     * Otherwise, operand is promoted to a value of type int.
     */
    public static Expr promoteNumericValue(Context context, Expr operandValue, CtTypeReference<?> operandType)
    {
        if (!TypeUtils.isLong(operandType))
        {
            operandValue = extendToInteger(context, (BitVecExpr) operandValue, !TypeUtils.isChar(operandType));
        }
        return operandValue;
    }

    /**
     * Performs Binary Numeric Promotion:
     * If either operand is of type long, the other is converted to long.
     * Otherwise, both operands are converted to type int.
     */
    public static Expr[] promoteNumericValues(Context context, Expr leftValue, CtTypeReference<?> leftType, Expr rightValue, CtTypeReference<?> rightType)
    {
        if (TypeUtils.isLong(leftType) || TypeUtils.isLong(rightType))
        {
            leftValue = extendToLong(context, (BitVecExpr) leftValue, !TypeUtils.isChar(leftType));
            rightValue = extendToLong(context, (BitVecExpr) rightValue, !TypeUtils.isChar(rightType));
        }
        else
        {
            leftValue = extendToInteger(context, (BitVecExpr) leftValue, !TypeUtils.isChar(leftType));
            rightValue = extendToInteger(context, (BitVecExpr) rightValue, !TypeUtils.isChar(rightType));
        }
        return new Expr[] { leftValue, rightValue };
    }
}
