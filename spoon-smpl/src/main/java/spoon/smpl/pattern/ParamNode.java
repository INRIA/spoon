package spoon.smpl.pattern;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This class roughly corresponds to spoon.pattern.internal.node.ParameterNode
 */
public class ParamNode implements PatternNode {
    public ParamNode(String name) {
        this.name = name;
    }

    @Override
    public void accept(PatternNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ParamNode) {
            return name.equals(((ParamNode)other).name);
        }
        else {
            return false;
        }
    }

    public String name;
}
