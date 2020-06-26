package spoon.smpl.pattern;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.smpl.SmPLJavaDSL;

public class DotsExtPatternMatcher extends PatternMatcher {
    public DotsExtPatternMatcher(PatternNode pattern) {
        super(pattern);
    }

    @Override
    public void visit(ElemNode otherNode) {
        PatternNode myNode = patternStack.pop();

        if (myNode instanceof ElemNode) {
            ElemNode myElemNode = (ElemNode) myNode;

            if (myElemNode.matchClass != otherNode.matchClass) {
                result = false;
                return;
            }

            if (isInvocationWithDots(myElemNode)) {
                matchInvocationWithDots(myElemNode, otherNode);
                return;
            }
        }

        patternStack.push(myNode);
        super.visit(otherNode);
    }

    private static int numArgs(ElemNode node) {
        return (int) ((ValueNode) node.sub.get("numargs")).srcValue;
    }

    private static PatternNode nthArg(ElemNode node, int n) {
        return node.sub.get("arg" + Integer.toString(n));
    }

    private static boolean isDots(PatternNode node) {
        return node instanceof ElemNode
               && ((ElemNode) node).elem instanceof CtVariableRead<?>
               && ((CtVariableRead<?>) ((ElemNode) node).elem).getVariable().getSimpleName().equals(SmPLJavaDSL.getDotsParameterOrArgumentElementName());
    }

    private static boolean isInvocationWithDots(ElemNode node) {
        if (!(node.elem instanceof CtInvocation<?>) || ((CtInvocation<?>) node.elem).getArguments().size() < 1) {
            return false;
        }

        int numargs = numArgs(node);

        for (int i = 0; i < numargs; ++i) {
            if (isDots(node.sub.get("arg" + Integer.toString(i)))) {
                return true;
            }
        }

        return false;
    }

    private static int firstNonDotsArgIndex(ElemNode invocationNode, int numargs, int start) {
        for (int n = start; n < numargs; ++n) {
            if (!isDots(nthArg(invocationNode, n))) {
                return n;
            }
        }

        return -1;
    }

    private void matchInvocationWithDots(ElemNode myElemNode, ElemNode otherNode) {
        int myNumArgs = numArgs(myElemNode);

        if (myNumArgs == 1) {
            result = true;
            return;
        }

        int otherNumArgs = numArgs(otherNode);

        int i = firstNonDotsArgIndex(myElemNode, myNumArgs, 0);
        boolean activeDots = (i != 0);

        for (int j = 0; j < otherNumArgs; ++j) {
            patternStack.push(nthArg(myElemNode, i));
            nthArg(otherNode, j).accept(this);

            if (result == true) {
                activeDots = (i + 1) < myNumArgs && isDots(nthArg(myElemNode, i + 1));
                i = firstNonDotsArgIndex(myElemNode, myNumArgs, i + 1);

                if (activeDots && i == -1) {
                    return;
                }
            } else if (!activeDots) {
                return;
            }
        }

        if (i != -1) {
            result = false;
            return;
        }
    }
}
