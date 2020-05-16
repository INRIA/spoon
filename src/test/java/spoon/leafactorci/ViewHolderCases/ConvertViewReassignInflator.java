package spoon.leafactorci.ViewHolderCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtVariableReference;

public class ConvertViewReassignInflator extends CaseOfInterest {
    final public CtVariableReference variable;
    final public CtAssignment assignment;

    private ConvertViewReassignInflator(CtVariableReference variable, CtAssignment assignment, DetectionPhaseContext context) {
        super(context);
        this.variable = variable;
        this.assignment = assignment;
    }

    public static ConvertViewReassignInflator detect(DetectionPhaseContext context) {
        if (!(context.statement instanceof CtAssignment)) {
            return null;
        }
        CtAssignment assignment = (CtAssignment)context.statement;
        CtExpression assignedExpression = assignment.getAssigned();
        CtExpression assignmentExpression = assignment.getAssignment();

        if(!(assignmentExpression instanceof CtInvocation)) {
            return null;
        }
        CtInvocation invocation = (CtInvocation) assignmentExpression;

        boolean isInflateCall = invocation.getExecutable().getSimpleName().equals("inflate");
        boolean takesTwoArguments = invocation.getArguments().size() == 2;
        if (isInflateCall && takesTwoArguments && assignedExpression instanceof CtVariableWrite) {
            // Here we know that we are calling method with the same signature
            CtVariableWrite variableWrite = (CtVariableWrite) assignedExpression;
            CtParameter secondParameter = (CtParameter) context.getClosestMethodParent().getParameters().get(1);
            boolean assignedToConvertView = variableWrite.getVariable().getSimpleName().equals(secondParameter.getSimpleName());
            if (assignedToConvertView) {
                return new ConvertViewReassignInflator(
                        variableWrite.getVariable(),
                        assignment,
                        context);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ConvertViewReassignInflator{" +
                "variable=" + variable +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}