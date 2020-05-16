package spoon.leafactorci.ViewHolderCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;


public class VariableAssignedInflator extends CaseOfInterest {
    final public CtVariableReference variable; // The variable that is being assigned

    private VariableAssignedInflator(CtVariableReference variable,
                                     DetectionPhaseContext context) {
        super(context);
        this.variable = variable;
    }

    public static VariableAssignedInflator detect(DetectionPhaseContext context) {
        CtExpression assignmentExpression;
        CtVariableReference variableReference;
        if(context.statement instanceof CtVariable) {
            assignmentExpression = ((CtVariable)context.statement).getDefaultExpression();
            variableReference = ((CtVariable)context.statement).getReference();
        } else if (context.statement instanceof CtAssignment) {
            CtAssignment assignment = (CtAssignment)context.statement;
            assignmentExpression = assignment.getAssignment();
            CtExpression assignedExpression = assignment.getAssigned();
            if(!(assignedExpression instanceof CtVariableWrite)) {
                return null;
            }
            variableReference = ((CtVariableWrite) assignedExpression).getVariable();
        } else {
            return null;
        }

        CtInvocation invocation;
        if(assignmentExpression instanceof CtInvocation) {
            invocation = (CtInvocation) assignmentExpression;
        } else {
            return null;
        }

        boolean isInflateCall = invocation.getExecutable().getSimpleName().equals("inflate");
        boolean argumentsMatch = invocation.getArguments().size() == 2;
        if (isInflateCall && argumentsMatch) {
            // Here we know that we are calling method with the same signature
            return new VariableAssignedInflator(variableReference, context);
        }
        return null;
    }

    @Override
    public String toString() {
        return "VariableAssignedInflator{" +
                "variable=" + variable +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}