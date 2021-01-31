package spoon.smpl.pattern;

import spoon.reflect.declaration.CtElement;

import java.util.*;

/**
 * ElemNode is a pattern node corresponding to a metamodel element.
 *
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ElementNode
 */
public class ElemNode implements PatternNode {
    /**
     * Create a new ElemNode holding a given element that matches on the combination of the literal
     * pretty-printed String representation of the element together with any sub-patterns.
     *
     * @param elem Element to use
     */
    public ElemNode(CtElement elem) {
        this(elem, elem.getClass().toString());
    }

    /**
     * Create a new ElemNode holding a given element that matches on the combination of a given
     * literal String together with any sub-patterns.
     *
     * @param elem Element to use
     * @param matchStr Literal String to match
     */
    public ElemNode(CtElement elem, String matchStr) {
        this.elem = elem;
        this.matchStr = matchStr;
        this.sub = new HashMap<>();
    }

    /**
     * Visitor pattern dispatch.
     *
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Compare this pattern node to a given pattern node.
     *
     * @param other Pattern node to compare against
     * @return True if nodes match, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ElemNode) {
            return matchStr.equals(((ElemNode) other).matchStr) && sub.equals(((ElemNode) other).sub);
        }
        else {
            return false;
        }
    }

    /**
     * Get a String representation of this pattern node.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Elem(");
        sb.append(matchStr);
        sb.append(", ");

        List<String> subKeys = new ArrayList<>(sub.keySet());
        Collections.sort(subKeys);

        for (String key : subKeys) {
            sb.append(key);
            sb.append("=");
            sb.append(sub.get(key).toString());
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        sb.append(")");

        return sb.toString();
    }

    /**
     * Held metamodel element.
     */
    public final CtElement elem;

    /**
     * Literal String to match against other ElemNodes.
     */
    public final String matchStr;

    /**
     * Sub-patterns.
     */
    public final Map<String, PatternNode> sub;
}
