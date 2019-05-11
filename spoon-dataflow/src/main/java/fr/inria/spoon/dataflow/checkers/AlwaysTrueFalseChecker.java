package fr.inria.spoon.dataflow.checkers;

import fr.inria.spoon.dataflow.misc.ConditionStatus;
import fr.inria.spoon.dataflow.scanners.CheckersScanner;
import fr.inria.spoon.dataflow.utils.TypeUtils;
import fr.inria.spoon.dataflow.warning.Warning;
import fr.inria.spoon.dataflow.warning.WarningKind;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.reference.CtTypeReference;

import static fr.inria.spoon.dataflow.utils.TypeUtils.getActualType;
import static fr.inria.spoon.dataflow.utils.TypeUtils.isCalculable;

/**
 * This checker warns if some expression is always true/false.
 * For example:
 *    int a = 5;
 *    if (a == 5) {} // <= always true warning
 */
public class AlwaysTrueFalseChecker extends AbstractChecker
{
    public AlwaysTrueFalseChecker(CheckersScanner scanner)
    {
        super(scanner);
    }

    private void check(CtExpression<?> expression, boolean isLoopCondition)
    {
        Expr conditionExpr = (Expr) expression.getMetadata("value");

        CtTypeReference<?> expressionType = getActualType(expression);

        // Unboxing conversion
        if (!expressionType.isPrimitive() && isCalculable(expressionType))
        {
            conditionExpr = getMemory().read(expressionType.unbox(), (IntExpr) conditionExpr);
        }

        ConditionStatus conditionStatus = checkCond((BoolExpr) conditionExpr);

        if (conditionStatus == ConditionStatus.ALWAYS_TRUE && !isLoopCondition)
        {
            addWarning(new Warning(expression, WarningKind.ALWAYS_TRUE));
        }
        else if (conditionStatus == ConditionStatus.ALWAYS_FALSE)
        {
            addWarning(new Warning(expression, WarningKind.ALWAYS_FALSE));
        }
    }

    private void checkExpression(CtExpression<?> expression)
    {
        if (expression != null && !(expression instanceof CtLiteral) && TypeUtils.isBoolean(expression.getType()))
        {
            check(expression, false);
        }
    }

    @Override
    public void checkCondition(CtExpression<?> condition, boolean isLoopCondition)
    {
        check(condition, isLoopCondition);
    }

    @Override
    public void checkBinaryOperatorLeft(BinaryOperatorKind kind, CtExpression<?> left)
    {
        if (kind == BinaryOperatorKind.AND || kind == BinaryOperatorKind.OR)
        {
            check(left, false);
        }
    }

    @Override
    public void checkBinaryOperatorRight(BinaryOperatorKind kind, CtExpression<?> right)
    {
        if (kind == BinaryOperatorKind.AND || kind == BinaryOperatorKind.OR)
        {
            check(right, false);
        }
    }

    @Override
    public void checkConditionalThenExpression(CtExpression<?> thenExpression)
    {
        checkExpression(thenExpression);
    }

    @Override
    public void checkConditionalElseExpression(CtExpression<?> elseExpression)
    {
        checkExpression(elseExpression);
    }

    @Override
    public void checkReturnedExpression(CtExpression<?> returnedExpression)
    {
        checkExpression(returnedExpression);
    }

    @Override
    public void checkAssignmentRight(CtExpression<?> right)
    {
        checkExpression(right);
    }
}
