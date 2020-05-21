package spoon.smpl.pattern;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class is initialized with a given rule pattern that may include parameter nodes,
 * and is able to match the rule pattern against other input patterns that may NOT include
 * parameter nodes.
 */
public class PatternMatcher implements PatternNodeVisitor {
    public PatternMatcher(PatternNode pattern) {
        this.initialPattern = pattern;
        reset();
    }

    public void reset() {
        patternStack = new Stack<>();
        parameters = new HashMap<>();
        result = null;

        patternStack.push(initialPattern);
    }

    public boolean getResult() {
        if (result == null) {
            throw new IllegalStateException();
        }

        return result;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public void visit(ElemNode otherNode) {
        PatternNode myNode = patternStack.pop();

        if (myNode instanceof ElemNode) {
            ElemNode myElemNode = (ElemNode)myNode;

            if (myElemNode.elem.getClass() != otherNode.elem.getClass()) {
                result = false;
                return;
            }

            for (String k : myElemNode.sub.keySet()) {
                if (otherNode.sub.containsKey(k)) {
                    patternStack.push(myElemNode.sub.get(k));
                    otherNode.sub.get(k).accept(this);

                    if (result == false) {
                        return;
                    }
                }
            }

            result = true;
        }
        else if (myNode instanceof ParamNode) {
            result = bindParameter(((ParamNode) myNode).name, otherNode.elem);
        }
        else {
            result = false;
        }
    }

    @Override
    public void visit(ParamNode node) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void visit(ValueNode otherNode) {
        PatternNode myNode = patternStack.pop();

        if (myNode instanceof ValueNode) {
            result = myNode.equals(otherNode);
        }
        else if (myNode instanceof ParamNode) {
            result = bindParameter(((ParamNode) myNode).name, (CtElement) otherNode.srcValue);
        }
        else {
            result = false;
        }
    }

    protected boolean bindParameter(String name, CtElement value) {
        if (!parameters.containsKey(name)) {
            parameters.put(name, value);
            return true;
        } else {
            if (parameters.get(name).equals(value)) {
                return true;
            } else {
                return tryNarrowingParameter(name, (CtElement) parameters.get(name), value);
            }
        }
    }

    protected boolean tryNarrowingParameter(String name, CtElement e1, CtElement e2) {
        if (e1 instanceof CtVariableAccess<?> && e2 instanceof CtVariableAccess<?>) {
            CtVariableAccess<?> va1 = (CtVariableAccess<?>) e1;
            CtVariableAccess<?> va2 = (CtVariableAccess<?>) e2;

            if (va1.getVariable().equals(va2.getVariable())) {
                parameters.put(name, va1.getVariable());
                return true;
            }
        }

        return false;
    }

    protected PatternNode initialPattern;
    protected Stack<PatternNode> patternStack;
    protected Map<String, Object> parameters;
    protected Boolean result;
}
