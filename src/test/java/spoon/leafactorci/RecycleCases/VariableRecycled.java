package spoon.leafactorci.RecycleCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class VariableRecycled extends CaseOfInterest {
    final public List<CtVariableAccess> variableAccesses;

    private VariableRecycled(List<CtVariableAccess> variableAccesses, DetectionPhaseContext context) {
        super(context);
        this.variableAccesses = variableAccesses;
    }

    public static VariableRecycled detect(DetectionPhaseContext context, Map<String, String> opportunities) {
        List<CtVariableAccess> variableAccesses = new ArrayList<>();
        Stack<CtElement> stack = new Stack<>();
        stack.add(context.statement);
        do {
            CtElement current = stack.pop();
            if (current instanceof CtVariableRead && current.getParent() instanceof CtInvocation) {
                CtInvocation ctInvocation = (CtInvocation) current.getParent();
                if(((CtVariableRead) current).getType() == null) {
                    continue;
                }
                String recycleMethodName = opportunities.get(((CtVariableRead) current).getType().getSimpleName());
                if(ctInvocation.getTarget() == current && ctInvocation.getExecutable().getSimpleName().equals(recycleMethodName)) {
                    variableAccesses.add((CtVariableAccess) current);
                }
            }
            stack.addAll(current.getDirectChildren());
        } while(!stack.isEmpty());
        if(variableAccesses.size() == 0) {
            return null;
        }
        return new VariableRecycled(variableAccesses, context);
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