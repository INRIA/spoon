package spoon.smpl.pattern;

import spoon.reflect.declaration.CtElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class roughly corresponds to spoon.pattern.internal.node.ElementNode
 */
public class ElemNode implements PatternNode {
    public ElemNode(CtElement elem) {
        this.elem = elem;
        this.sub = new HashMap<>();
    }

    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ElemNode) {
            return elem.getClass().equals(((ElemNode)other).elem.getClass()) && sub.equals(((ElemNode)other).sub);
        }
        else {
            return false;
        }
    }

    public CtElement elem;
    public Map<String, PatternNode> sub;
}
