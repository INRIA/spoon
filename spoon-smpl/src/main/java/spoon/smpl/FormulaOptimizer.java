package spoon.smpl;

import spoon.smpl.formula.*;

import java.util.List;
import java.util.Stack;

/**
 * FormulaOptimizer is a Formula visitor that traverses a given Formula and produces an
 * optimized Formula.
 *
 * Optimizations:
 * 1) Operation-capturing formulas "And(LHS, ExistsVar("_v", SetEnv("_v", List<Operation>)))"
 *    with empty operation lists are replaced by LHS.
 */
public class FormulaOptimizer implements FormulaVisitor {
    /**
     * Repeatedly optimize a formula until there is no change.
     *
     * @param phi Formula to optimize
     * @return Optimized formula
     */
    public static Formula optimizeFully(Formula phi) {
        if (phi == null) {
            return null;
        }

        String prevStr = "";

        while (!prevStr.equals(phi.toString())) {
            prevStr = phi.toString();

            FormulaOptimizer optimizer = new FormulaOptimizer(phi);
            phi = optimizer.getResult();
        }

        return phi;
    }

    /**
     * Optimize a Formula.
     *
     * @param phi Formula to optimize
     */
    public FormulaOptimizer(Formula phi) {
        if (phi != null) {
            resultStack = new Stack<>();
            phi.accept(this);
        }
    }

    /**
     * Get the optimized Formula.
     *
     * @return Optimized formula
     */
    public Formula getResult() {
        return resultStack.pop();
    }

    /**
     * Optimize Formula elements of type True.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(True element) {
        resultStack.push(element);
    }

    /**
     * Optimize Formula elements of type True.
     *
     * Direct optimizations:
     * 1) (And(T, phi) | And(phi, T)) -> phi
     * 2) Operation-capturing formulas "And(LHS, ExistsVar("_v", SetEnv("_v", List<Operation>)))"
     *    with empty operation lists are replaced by LHS.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(And element) {
        if (element.getLhs() instanceof True) {
            resultStack.push(element.getRhs());
        } else if (element.getRhs() instanceof True) {
            resultStack.push(element.getLhs());
        } else if (element.getRhs() instanceof ExistsVar
            && ((ExistsVar) element.getRhs()).getVarName().equals("_v")
            && ((ExistsVar) element.getRhs()).getInnerElement() instanceof SetEnv
            && ((SetEnv) ((ExistsVar) element.getRhs()).getInnerElement()).getValue() instanceof List
            && ((List<?>) ((SetEnv) ((ExistsVar) element.getRhs()).getInnerElement()).getValue()).size() == 0) {
            resultStack.push(element.getLhs());
        } else {
            element.getRhs().accept(this);
            element.getLhs().accept(this);
            resultStack.push(new And(resultStack.pop(), resultStack.pop()));
        }
    }

    /**
     * Optimize Formula elements of type Or.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(Or element) {
        element.getRhs().accept(this);
        element.getLhs().accept(this);
        resultStack.push(new Or(resultStack.pop(), resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type Not.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(Not element) {
        element.getInnerElement().accept(this);
        resultStack.push(new Not(resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type Predicate.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(Predicate element) {
        resultStack.push(element);
    }

    /**
     * Optimize Formula elements of type ExistsNext.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(ExistsNext element) {
        element.getInnerElement().accept(this);
        resultStack.push(new ExistsNext(resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type AllNext.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(AllNext element) {
        element.getInnerElement().accept(this);
        resultStack.push(new AllNext(resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type ExistsUntil.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(ExistsUntil element) {
        element.getRhs().accept(this);
        element.getLhs().accept(this);
        resultStack.push(new ExistsUntil(resultStack.pop(), resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type AllUntil.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(AllUntil element) {
        element.getRhs().accept(this);
        element.getLhs().accept(this);
        resultStack.push(new AllUntil(resultStack.pop(), resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type ExistsVar.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(ExistsVar element) {
        element.getInnerElement().accept(this);
        resultStack.push(new ExistsVar(element.getVarName(), resultStack.pop()));
    }

    /**
     * Optimize Formula elements of type SetEnv.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(SetEnv element) {
        resultStack.push(element);
    }

    /**
     * Optimize Formula elements of type SequentialOr.
     *
     * Direct optimizations: none.
     *
     * @param element Formula to optimize
     */
    @Override
    public void visit(SequentialOr element) {
        SequentialOr result = new SequentialOr();

        for (Formula formula : element) {
            formula.accept(this);
            result.add(resultStack.pop());
        }

        resultStack.push(result);
    }

    @Override
    public void visit(Optional element) {
        element.getInnerElement().accept(this);
        resultStack.push(new Optional(resultStack.pop()));
    }

    /**
     * Internal stack of results.
     */
    private Stack<Formula> resultStack;
}
