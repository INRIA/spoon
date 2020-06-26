package spoon.smpl.formula;

/**
 * FormulaVisitor defines the Visitor pattern for Formula elements.
 */
public interface FormulaVisitor {
    void visit(True element);
    void visit(And element);
    void visit(Or element);
    void visit(Not element);
    void visit(Predicate element);
    void visit(ExistsNext element);
    void visit(AllNext element);
    void visit(ExistsUntil element);
    void visit(AllUntil element);
    void visit(ExistsVar element);
    void visit(SetEnv element);
    void visit(SequentialOr element);
    void visit(Optional element);
    void visit(InnerAnd element);
}
