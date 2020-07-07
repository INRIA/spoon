package spoon.smpl.pattern;

import spoon.reflect.declaration.CtElement;

import java.util.*;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class roughly corresponds to spoon.pattern.internal.node.ElementNode
 */
public class ElemNode implements PatternNode {
    public ElemNode(CtElement elem) {
        this(elem, elem.getClass().toString());
    }

    public ElemNode(CtElement elem, String matchStr) {
        this.elem = elem;
        this.matchStr = matchStr;
        this.sub = new HashMap<>();
    }

    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ElemNode) {
            return matchStr.equals(((ElemNode) other).matchStr) && sub.equals(((ElemNode)other).sub);
        }
        else {
            return false;
        }
    }

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

    public final CtElement elem;
    public final String matchStr;
    public final Map<String, PatternNode> sub;
}
