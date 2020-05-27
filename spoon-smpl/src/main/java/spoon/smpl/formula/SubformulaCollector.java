package spoon.smpl.formula;

import java.util.ArrayList;
import java.util.List;

public class SubformulaCollector implements FormulaVisitor {
    public SubformulaCollector() {
        subformulas = new ArrayList<>();
    }

    public List<Formula> getResult() {
        return subformulas;
    }

    private List<Formula> subformulas;

    @Override
    public void visit(True element) {
        subformulas.add(element);
    }

    @Override
    public void visit(And element) {
        subformulas.add(element);
        element.getLhs().accept(this);
        element.getRhs().accept(this);
    }

    @Override
    public void visit(Or element) {
        subformulas.add(element);
        element.getLhs().accept(this);
        element.getRhs().accept(this);
    }

    @Override
    public void visit(Not element) {
        subformulas.add(element);
        element.getInnerElement().accept(this);
    }

    @Override
    public void visit(Predicate element) {
        subformulas.add(element);
    }

    @Override
    public void visit(ExistsNext element) {
        subformulas.add(element);
        element.getInnerElement().accept(this);
    }

    @Override
    public void visit(AllNext element) {
        subformulas.add(element);
        element.getInnerElement().accept(this);
    }

    @Override
    public void visit(ExistsUntil element) {
        subformulas.add(element);
        element.getLhs().accept(this);
        element.getRhs().accept(this);
    }

    @Override
    public void visit(AllUntil element) {
        subformulas.add(element);
        element.getLhs().accept(this);
        element.getRhs().accept(this);
    }

    @Override
    public void visit(ExistsVar element) {
        subformulas.add(element);
        element.getInnerElement().accept(this);
    }

    @Override
    public void visit(SetEnv element) {
        subformulas.add(element);
    }

    @Override
    public void visit(SequentialOr element) {
        subformulas.add(element);

        for (Formula phi : element) {
            phi.accept(this);
        }
    }

    @Override
    public void visit(Optional element) {
        subformulas.add(element);

        element.getInnerElement().accept(this);
    }
}
