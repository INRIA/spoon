package spoon.leafactorci.ViewHolderCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtVariableReference;


public class ConvertViewReuseWithTernary extends CaseOfInterest {
    final public CtVariableReference reassignedVariable; // The variable that is being reassigned
    final public CtVariableRead checkedVariable; // The variable that is being checked for null
    final public CtVariableRead assignedVariable; // The variable that is returned if the checkedVariable is not null

    private ConvertViewReuseWithTernary(CtVariableReference reassignedVariable,
                                        CtVariableRead checkedVariable,
                                        CtVariableRead assignedVariable,
                                        DetectionPhaseContext context) {
        super(context);
        this.reassignedVariable = reassignedVariable;
        this.checkedVariable = checkedVariable;
        this.assignedVariable = assignedVariable;
    }

    public static ConvertViewReuseWithTernary detect(DetectionPhaseContext context) {
        if (!(context.statement instanceof CtAssignment)) {
            return null;
        }
        CtAssignment assignment = (CtAssignment)context.statement;
        CtExpression assignedExpression = assignment.getAssigned();
        CtExpression assignmentExpression = assignment.getAssignment();

        if(!(assignmentExpression instanceof CtConditional)) {
            return null;
        }

        CtConditional conditional = (CtConditional) assignmentExpression;
        CtExpression condition = conditional.getCondition();
        CtExpression thenExpression = conditional.getThenExpression();
        CtExpression elseExpression = conditional.getElseExpression();

//        String argumentName = ((CtParameter) Objects.requireNonNull(RefactoringRule.getClosestMethodParent(context.statement))
//                .getParameters().get(1)).getSimpleName();

        if(!(condition instanceof CtBinaryOperator)) {
            return null;
        }

        CtBinaryOperator binaryOperator = (CtBinaryOperator)condition;
        BinaryOperatorKind kind = binaryOperator.getKind();
        CtExpression leftHandOperand = binaryOperator.getLeftHandOperand();
        CtExpression rightHandOperand = binaryOperator.getRightHandOperand();

        CtVariableRead conditionVariableRead;
        if(leftHandOperand instanceof CtVariableRead && rightHandOperand instanceof CtLiteral
                && rightHandOperand.toString().equals("null")) {
            conditionVariableRead = (CtVariableRead) leftHandOperand;
        } else if(rightHandOperand instanceof CtVariableRead && leftHandOperand instanceof CtLiteral
                && leftHandOperand.toString().equals("null")) {
            conditionVariableRead = (CtVariableRead) rightHandOperand;
        } else {
            return null;
        }

        CtInvocation invocation;
        CtVariableRead variableRead;
        if(thenExpression instanceof CtInvocation && elseExpression instanceof CtVariableRead && kind == BinaryOperatorKind.EQ) {
            invocation = (CtInvocation) thenExpression;
            variableRead = (CtVariableRead) elseExpression;
        } else if(elseExpression instanceof CtInvocation && thenExpression instanceof CtVariableRead && kind == BinaryOperatorKind.NE) {
            invocation = (CtInvocation) elseExpression;
            variableRead = (CtVariableRead) thenExpression;
        } else {
            return null;
        }

        boolean isInflateCall = invocation.getExecutable().getSimpleName().equals("inflate");
        boolean takesTwoArguments = invocation.getArguments().size() == 2;
        if (isInflateCall && takesTwoArguments && assignedExpression instanceof CtVariableWrite) {
            // Here we know that we are calling method with the same signature
            CtVariableWrite variableWrite = (CtVariableWrite) assignedExpression;
            CtParameter secondParameter = (CtParameter) context.getClosestMethodParent().getParameters().get(1);
            boolean assignedToConvertView = variableWrite.getVariable().getSimpleName().equals(secondParameter.getSimpleName());
            if (assignedToConvertView) {
                return new ConvertViewReuseWithTernary(variableWrite.getVariable(), conditionVariableRead, variableRead, context);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ConvertViewReuseWithTernary{" +
                "reasignedVariable=" + reassignedVariable +
                ", checkedVariable=" + checkedVariable +
                ", assignedVariable=" + assignedVariable +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}