package spoon.smpl.pattern;

import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.MetavariableConstraint;

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
    public PatternMatcher(PatternNode pattern, Map<String, MetavariableConstraint> metavars) {
        this.initialPattern = pattern;
        this.metavars = metavars;
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
        throw new NotImplementedException("Not supported");
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

    private boolean bindParameter(String name, CtElement value) {
        Object binding = metavars.get(name).apply(value);

        if (binding == null) {
            return false;
        }

        if (!parameters.containsKey(name)) {
            parameters.put(name, binding);
            return true;
        } else {
            return parameters.get(name).equals(binding);
        }
    }

    private PatternNode initialPattern;
    private Map<String, MetavariableConstraint> metavars;
    private Stack<PatternNode> patternStack;
    private Map<String, Object> parameters;
    private Boolean result;
}
