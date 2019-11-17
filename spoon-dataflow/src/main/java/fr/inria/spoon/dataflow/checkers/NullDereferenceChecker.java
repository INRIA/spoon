package fr.inria.spoon.dataflow.checkers;

import fr.inria.spoon.dataflow.memory.Memory;
import fr.inria.spoon.dataflow.misc.ConditionStatus;
import fr.inria.spoon.dataflow.scanners.CheckersScanner;
import fr.inria.spoon.dataflow.warning.Warning;
import fr.inria.spoon.dataflow.warning.WarningKind;
import com.microsoft.z3.Expr;
import spoon.reflect.code.*;

/**
 * This checker warns if null dereference occurs in some expression.
 * For example:
 *    Object x = null;
 *    int z = x.hashCode() // <= null dereference warning
 */
public class NullDereferenceChecker extends AbstractChecker
{
    public NullDereferenceChecker(CheckersScanner scanner)
    {
        super(scanner);
    }

    private void check(CtTargetedExpression<?, ?> targetedExpression)
    {
        CtExpression<?> target = targetedExpression.getTarget();
        if (target != null)
        {
            Expr targetExpr = (Expr) target.getMetadata("value");
            if (targetExpr != null)
            {
                ConditionStatus alwaysNull = checkCond(getContext().mkEq(targetExpr, getContext().mkInt(Memory.nullPointer())));
                if (alwaysNull == ConditionStatus.ALWAYS_TRUE)
                {
                    addWarning(new Warning(targetedExpression, WarningKind.NULL_DEREFERENCE));
                }
            }
        }
    }

    @Override
    public void checkInvocation(CtInvocation<?> invocation)
    {
        check(invocation);
    }

    @Override
    public void checkFieldRead(CtFieldRead<?> fieldRead)
    {
        check(fieldRead);
    }

    @Override
    public void checkFieldWrite(CtFieldWrite<?> fieldWrite)
    {
        check(fieldWrite);
    }

    @Override
    public void checkArrayRead(CtArrayRead<?> arrayRead)
    {
        check(arrayRead);
    }

    @Override
    public void checkArrayWrite(CtArrayWrite<?> arrayWrite)
    {
        check(arrayWrite);
    }
}
