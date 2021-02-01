package spoon.smpl.pattern;

/**
 * PatternNodeVisitor defines the visitor pattern interface for pattern nodes.
 */
public interface PatternNodeVisitor {
	/**
	 * Visit an ElemNode.
	 *
	 * @param node Node to visit
	 */
	void visit(ElemNode node);

	/**
	 * Visit a ParamNode.
	 *
	 * @param node Node to visit
	 */
	void visit(ParamNode node);

	/**
	 * Visit a ValueNode.
	 *
	 * @param node Node to visit
	 */
	void visit(ValueNode node);
}
