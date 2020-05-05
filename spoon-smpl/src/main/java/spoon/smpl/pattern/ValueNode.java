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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Val(");

        if (matchValue == null) {
            sb.append("null");
        } else {
            sb.append(matchValue.getClass().getSimpleName());
            sb.append(":");
            sb.append(matchValue.toString());
        }

        sb.append("; ");

        if (srcValue == null) {
            sb.append("null");
        } else {
            sb.append(srcValue.getClass().getSimpleName());
            sb.append(":");
            sb.append(srcValue.toString());
        }

        sb.append(")");
        return sb.toString();
    }

    public final Object matchValue;
    public final Object srcValue;
}
