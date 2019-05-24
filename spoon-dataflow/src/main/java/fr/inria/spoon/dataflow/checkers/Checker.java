package fr.inria.spoon.dataflow.checkers;

import spoon.reflect.code.*;

public interface Checker
{
    void checkCondition(CtExpression<?> condition, boolean isLoopCondition);

    void checkBinaryOperatorLeft(BinaryOperatorKind kind, CtExpression<?> left);

    void checkBinaryOperatorRight(BinaryOperatorKind kind, CtExpression<?> right);

    void checkBinaryOperatorResult(CtBinaryOperator<?> operator);

    void checkConditionalThenExpression(CtExpression<?> thenExpression);

    void checkConditionalElseExpression(CtExpression<?> elseExpression);

    void checkConditionalResult(CtConditional<?> conditional);

    void checkReturnedExpression(CtExpression<?> returnedExpression);

    void checkAssignmentLeft(CtExpression<?> left);

    void checkAssignmentRight(CtExpression<?> right);

    void checkAssignmentResult(CtAssignment<?, ?> assignment);

    void checkInvocation(CtInvocation<?> invocation);

    void checkFieldRead(CtFieldRead<?> fieldRead);

    void checkFieldWrite(CtFieldWrite<?> fieldWrite);

    void checkArrayRead(CtArrayRead<?> arrayRead);

    void checkArrayWrite(CtArrayWrite<?> arrayWrite);
}
