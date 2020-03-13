package spoon.smpl.pattern;

/**
 * Part of temporary substitute for spoon.pattern
 *
 * This interface roughly corresponds to spoon.pattern.internal.node.RootNode
 */
public interface PatternNode {
    public void accept(PatternNodeVisitor visitor);
}
