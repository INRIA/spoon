package fr.inria.spoon.dataflow.utils;

import fr.inria.spoon.dataflow.memory.Memory;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import spoon.reflect.code.*;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtReference;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public final class CommonUtils
{
    private CommonUtils() {}

    /**
     * Gets target expression value by going from left to right.
     * For example, 'a.b' is calculated as follows: memory[T2.b][memory[T1.a][a]]
     */
    public static IntExpr getTargetValue(Context context, Map<CtReference, Expr> variablesMap, Memory memory, CtExpression<?> target)
    {
        Deque<CtExpression> targets = new ArrayDeque<>();
        while (target instanceof CtTargetedExpression)
        {
            targets.addFirst(target);
            target = ((CtTargetedExpression) target).getTarget();
        }
        targets.addFirst(target);

        // Traverse all targets left to right
        IntExpr targetValue = null;
        for (CtExpression t : targets)
        {
            if (t instanceof CtFieldRead)
            {
                targetValue = (IntExpr) memory.read(((CtFieldRead) t).getVariable(), targetValue);
            }
            else if (t instanceof CtArrayRead)
            {
                CtArrayRead arrayRead = (CtArrayRead) t;
                CtExpression index = arrayRead.getIndexExpression();
                Expr arrayIndex = (Expr) index.getMetadata("value");
                targetValue = (IntExpr) memory.readArray((CtArrayTypeReference) arrayRead.getTarget().getType(), targetValue, arrayIndex);
            }
            else if (t instanceof CtVariableRead)
            {
                targetValue = (IntExpr) variablesMap.get(((CtVariableRead) t).getVariable());
            }
            else if (t instanceof CtTypeAccess)
            {
                targetValue = (IntExpr) variablesMap.get(TypeUtils.getActualType(t));
                if (targetValue == null)
                {
                    targetValue = (IntExpr) context.mkFreshConst("", context.getIntSort());
                    variablesMap.put(TypeUtils.getActualType(t), targetValue);
                }
            }
            else if (t instanceof CtThisAccess)
            {
                targetValue = context.mkInt(Memory.thisPointer());
            }
            else
            {
                // Impure functions and other unknown stuff
                targetValue = (IntExpr) context.mkFreshConst("", context.getIntSort());
            }
        }

        return targetValue;
    }
}
