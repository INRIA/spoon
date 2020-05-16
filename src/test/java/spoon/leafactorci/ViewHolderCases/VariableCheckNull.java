package spoon.leafactorci.ViewHolderCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.*;
import spoon.reflect.reference.CtVariableReference;


public class VariableCheckNull extends CaseOfInterest {
    final public CtVariableReference variable; // The variable that is being assigned
    final public CtIf ifStmt;

    private VariableCheckNull(CtVariableReference variable, CtIf ifStmt,
                              DetectionPhaseContext context) {
        super(context);
        this.variable = variable;
        this.ifStmt = ifStmt;
    }

    public static VariableCheckNull detect(DetectionPhaseContext context) {
        if(context.statement instanceof CtIf) {
            CtExpression condition = ((CtIf) context.statement).getCondition();
            if(condition instanceof CtBinaryOperator) {
                CtExpression leftExpression = ((CtBinaryOperator) condition).getLeftHandOperand();
                CtExpression rightExpression = ((CtBinaryOperator) condition).getRightHandOperand();
                BinaryOperatorKind kind = ((CtBinaryOperator) condition).getKind();

                if(kind == BinaryOperatorKind.EQ && leftExpression instanceof CtVariableRead && rightExpression instanceof CtLiteral
                        && rightExpression.toString().equals("null")) {
                    return new VariableCheckNull(((CtVariableRead) leftExpression).getVariable(), (CtIf)context.statement, context);
                } else if(kind == BinaryOperatorKind.EQ && rightExpression instanceof CtVariableRead && leftExpression instanceof CtLiteral
                        && leftExpression.toString().equals("null")) {
                    return new VariableCheckNull(((CtVariableRead) rightExpression).getVariable(), (CtIf)context.statement, context);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "VariableCheckNull{" +
                "variable=" + variable +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}