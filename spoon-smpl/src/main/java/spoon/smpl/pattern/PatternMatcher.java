package spoon.smpl.pattern;

import org.apache.commons.lang3.NotImplementedException;

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
        Map<String, Object> result = new HashMap<>();

        for (String key : parameters.keySet()) {
            PatternNode node = parameters.get(key);

            if (node instanceof ValueNode) {
                result.put(key, ((ValueNode) node).srcValue);
            } else if (node instanceof ElemNode) {
                result.put(key, ((ElemNode) node).elem);
            } else {
                throw new IllegalStateException("This should be unreachable");
            }
        }

        return result;
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
            result = bindParameter(((ParamNode)myNode).name, otherNode);
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
            result = (((ValueNode)myNode).equals(otherNode));
        }
        else if (myNode instanceof ParamNode) {
            result = bindParameter(((ParamNode)myNode).name, otherNode);
        }
        else {
            result = false;
        }
    }

    private boolean bindParameter(String name, PatternNode value) {
        if (!parameters.containsKey(name)) {
            parameters.put(name, value);
            return true;
        } else {
            return parameters.get(name).equals(value);
        }
    }

    private PatternNode initialPattern;
    private Stack<PatternNode> patternStack;
    private Map<String, PatternNode> parameters;
    private Boolean result;
}
