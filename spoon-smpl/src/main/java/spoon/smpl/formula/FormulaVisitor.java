package spoon.smpl.formula;

/**
 * FormulaVisitor defines the Visitor pattern for Formula elements.
 */
public interface FormulaVisitor {
    public void visit(True element);
    public void visit(And element);
    public void visit(Or element);
    public void visit(Not element);
    public void visit(Predicate element);
    public void visit(ExistsNext element);
    public void visit(AllNext element);
    public void visit(ExistsUntil element);
    public void visit(AllUntil element);
    public void visit(ExistsVar element);
    public void visit(SetEnv element);
    public void visit(SequentialOr element);
    public void visit(Optional element);
}
