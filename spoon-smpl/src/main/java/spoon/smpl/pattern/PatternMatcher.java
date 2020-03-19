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
        this(pattern, false);
    }

    public PatternMatcher(PatternNode pattern, boolean debug) {
        this.initialPattern = pattern;
        this.debug = debug;
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

    public Map<String, PatternNode> getParameters() {
        return parameters;
    }

    @Override
    public void visit(ElemNode otherNode) {
        print("match ElemNode " + otherNode.elem.toString());
        PatternNode myNode = patternStack.pop();

        if (myNode instanceof ElemNode) {
            ElemNode myElemNode = (ElemNode)myNode;

            if (myElemNode.elem.getClass() != otherNode.elem.getClass()) {
                print(myElemNode.elem.getClass().toString() + " != " + otherNode.elem.getClass().toString());
                result = false;
                return;
            }

            for (String k : myElemNode.sub.keySet()) {
                if (otherNode.sub.containsKey(k)) {
                    print("queue sub " + k);
                    patternStack.push(myElemNode.sub.get(k));
                    otherNode.sub.get(k).accept(this);

                    if (result == false) {
                        print("fail on " + k);
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
        print("match ValueNode " + otherNode.matchValue.toString());
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
        print(Boolean.toString(result));
    }

    private boolean bindParameter(String name, PatternNode value) {
        if (!parameters.containsKey(name)) {
            parameters.put(name, value);
            return true;
        } else {
            return parameters.get(name).equals(value);
        }
    }

    private void print(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    private PatternNode initialPattern;
    private Stack<PatternNode> patternStack;
    private Map<String, PatternNode> parameters;
    private Boolean result;
    private boolean debug;
}
