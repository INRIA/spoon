package spoon.smpl.pattern;

/**
 * Part of temporary substitute for spoon.pattern
 */
public interface PatternNodeVisitor {
    public void visit(ElemNode node);
    public void visit(ParamNode node);
    public void visit(ValueNode node);
}
