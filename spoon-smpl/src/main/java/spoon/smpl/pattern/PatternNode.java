package spoon.smpl.pattern;

/**
 * PatternNode defines the base interface for pattern nodes.
 * <p>
 * This interface roughly corresponds to spoon.pattern.internal.node.RootNode
 */
public interface PatternNode {
	/**
	 * Visitor pattern dispatch.
	 *
	 * @param visitor Visitor to accept
	 */
	void accept(PatternNodeVisitor visitor);
}
