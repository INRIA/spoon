package spoon.leafactorci.Cases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VariableUsed extends CaseOfInterest {
    final public List<CtVariableAccess> variableAccesses;

    private VariableUsed(List<CtVariableAccess> variableAccesses, DetectionPhaseContext context) {
        super(context);
        this.variableAccesses = variableAccesses;
    }

    public static VariableUsed detect(DetectionPhaseContext context) {
        List<CtVariableAccess> variableAccesses = new ArrayList<>();
        Stack<CtElement> stack = new Stack<>();
        stack.add(context.statement);
        do {
            CtElement current = stack.pop();
            if (current instanceof CtVariableRead) {
                variableAccesses.add((CtVariableAccess) current);
            }
            stack.addAll(current.getDirectChildren());
        } while(!stack.isEmpty());
        if(variableAccesses.size() == 0) {
            return null;
        }
        return new VariableUsed(variableAccesses, context);
    }

    @Override
    public String toString() {
        return "VariableUsed{" +
                "variableAccesses=" + variableAccesses +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}