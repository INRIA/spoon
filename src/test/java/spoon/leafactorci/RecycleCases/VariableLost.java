package spoon.leafactorci.RecycleCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.leafactorci.engine.RefactoringRule;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class VariableLost extends CaseOfInterest {
    final public List<CtVariableAccess> variableAccesses;

    private VariableLost(List<CtVariableAccess> variableAccesses, DetectionPhaseContext context) {
        super(context);
        this.variableAccesses = variableAccesses;
    }

    private static boolean isInsideLambda(CtElement current, DetectionPhaseContext context) {
        CtLambda result = RefactoringRule.getClosestTypeParent(current, CtLambda.class, Arrays.asList(new CtElement[] {context.block}));
        return result != null;
    }

    private static boolean isInsideReturn(CtElement current, DetectionPhaseContext context) {
        CtReturn result = RefactoringRule.getClosestTypeParent(current, CtReturn.class, Arrays.asList(new CtElement[] {context.block}));
        return result != null && result.getReturnedExpression() == current;
    }

    private static boolean isInsideInvocation(CtElement current, DetectionPhaseContext context) {
        CtInvocation result = RefactoringRule.getClosestTypeParent(current, CtInvocation.class, Arrays.asList(new CtElement[] {context.block}));
        return result != null && result.getArguments().contains(current);
    }

    private static boolean isInsideBlock(CtElement current, DetectionPhaseContext context) {
        CtBlock result = RefactoringRule.getClosestTypeParent(current, CtBlock.class, Arrays.asList(new CtElement[] {context.block}));
        return result != null;
    }

    public static VariableLost detect(DetectionPhaseContext context) {
        List<CtVariableAccess> variableAccesses = new ArrayList<>();
        Stack<CtElement> stack = new Stack<>();
        stack.add(context.statement);
        do {
            CtElement current = stack.pop();
            if (current instanceof CtVariableRead) {
                boolean wasLost = isInsideLambda(current, context)
                        || isInsideReturn(current, context)
                        || isInsideInvocation(current, context);

                if(current.getParent() instanceof CtLocalVariable
                        && ((CtLocalVariable) current.getParent()).getDefaultExpression() == current) {
                    wasLost = true;
                }

                if(wasLost) {
                    variableAccesses.add((CtVariableAccess) current);
                }
            }
            if (current instanceof CtVariableWrite && current.getParent() instanceof CtAssignment
                    && isInsideBlock(current, context)) {
                variableAccesses.add((CtVariableAccess) current);
            }
            stack.addAll(current.getDirectChildren());
        } while(!stack.isEmpty());
        if(variableAccesses.size() == 0) {
            return null;
        }
        return new VariableLost(variableAccesses, context);
    }

    @Override
    public String toString() {
        return "VariableLost{" +
                "variableAccesses=" + variableAccesses +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}