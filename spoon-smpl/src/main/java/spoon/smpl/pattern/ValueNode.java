package spoon.smpl.pattern;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class roughly corresponds to spoon.pattern.internal.node.ConstantNode
 */
public class ValueNode implements PatternNode {
    public ValueNode(Object matchValue, Object srcValue) {
        this.matchValue = matchValue;
        this.srcValue = srcValue;
    }

    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ValueNode) {
            return matchValue.equals(((ValueNode)other).matchValue);
        }
        else {
            return false;
        }
    }

    public final Object matchValue;
    public final Object srcValue;
}
