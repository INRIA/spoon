package spoon.smpl;

import spoon.smpl.formula.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * ModelChecker implements the CTL model-checking algorithm.
 */
public class ModelChecker implements FormulaVisitor {
    /**
     * Create a new ModelChecker.
     * @param model The Model on which formulas should be checked
     */
    public ModelChecker(Model model) {
        this.model = model;
        this.resultStack = new Stack<>();
    }

    /**
     * Retrieve the most recently computed result.
     * @return Set of states that satisfied the most recently given formula
     * @throws java.util.EmptyStackException
     */
    public Set<Integer> getResult() {
        return resultStack.pop();
    }

    /**
     * Computes the set of states that satisfy True, i.e all states.
     * @param element
     */
    @Override
    public void visit(True element) {
        resultStack.push(new HashSet<Integer>(model.getStates()));
    }

    /**
     * Computes the set of states that satisfy "p And q".
     * @param element
     */
    @Override
    public void visit(And element) {
        element.getLhs().accept(this);
        element.getRhs().accept(this);

        Set<Integer> rightResult = resultStack.pop();
        Set<Integer> leftResult = resultStack.pop();

        leftResult.retainAll(rightResult);
        resultStack.push(leftResult);
    }

    /**
     * Computes the set of states that satisfy "p Or q".
     * @param element
     */
    @Override
    public void visit(Or element) {
        element.getLhs().accept(this);
        element.getRhs().accept(this);

        Set<Integer> rightResult = resultStack.pop();
        Set<Integer> leftResult = resultStack.pop();

        leftResult.addAll(rightResult);
        resultStack.push(leftResult);
    }

    /**
     * Computes the set of states that satisfy "not p".
     * @param element
     */
    @Override
    public void visit(Neg element) {
        element.getInnerElement().accept(this);
        Set<Integer> innerResult = resultStack.pop();
        Set<Integer> result = new HashSet<>(model.getStates());
        result.removeAll(innerResult);
        resultStack.push(result);
    }

    /**
     * Computes the set of states that satisfy "p", for some predicate p.
     * @param element
     */
    @Override
    public void visit(Predicate element) {
        Set<Integer> result = new HashSet<>();

        for (int s : model.getStates()) {
            for (Label label : model.getLabels(s)) {
                if (label.matches(element)) {
                    result.add(s);
                    break;
                }
            }
        }

        resultStack.push(result);
    }

    /**
     * Computes the set of states that satisfy "EX p".
     * @param element
     */
    @Override
    public void visit(ExistsNext element) {
        element.getInnerElement().accept(this);
        resultStack.push(ModelChecker.preExists(model, resultStack.pop()));
    }

    /**
     * Computes the set of states that satisfy "AX p".
     * @param element
     */
    @Override
    public void visit(AllNext element) {
        element.getInnerElement().accept(this);
        resultStack.push(ModelChecker.preAll(model, resultStack.pop()));
    }

    /**
     * Computes the set of states that satisfy "E[p U q]".
     * @param element
     */
    @Override
    public void visit(ExistsUntil element) {
        // find the states that satisfy X in E[X U Y]
        element.getLhs().accept(this);
        Set<Integer> satphi = resultStack.pop();

        // find the states that satisfy Y in E[X U Y], these also satisfy E[X U Y] for any X
        element.getRhs().accept(this);
        Set<Integer> result = resultStack.pop();

        while (true) {
            // find the states that can transition into a state known to satisfy E[X U Y]
            Set<Integer> pre = ModelChecker.preExists(model, result);

            // compute the intersection with states that satisfy X
            pre.retainAll(satphi);

            // extend the set of states known to satisfy E[X U Y], until there is no change
            if (!result.addAll(pre)) {
                break;
            }
        }

        resultStack.push(result);
    }

    /**
     * Computes the set of states that satisfy "A[p U q]".
     * @param element
     */
    @Override
    public void visit(AllUntil element) {
        // find the states that satisfy X in A[X U Y]
        element.getLhs().accept(this);
        Set<Integer> satphi = resultStack.pop();

        // find the states that satisfy Y in A[X U Y], these also satisfy A[X U Y] for any X
        element.getRhs().accept(this);
        Set<Integer> result = resultStack.pop();

        while (true) {
            // find the states that can ONLY transition into a state known to satisfy A[X U Y]
            Set<Integer> pre = ModelChecker.preAll(model, result);

            // compute the intersection with states that satisfy X
            pre.retainAll(satphi);

            // extend the set of states known to satisfy A[X U Y], until there is no change
            if (!result.addAll(pre)) {
                break;
            }
        }

        resultStack.push(result);
    }

    /**
     * Computes the set of states that have some successor in a given set of target states, i.e
     * the states that CAN transition into the set of target states.
     * @param model Model
     * @param targetStates Set of target states
     * @return Set of states with successor in target states
     */
    public static Set<Integer> preExists(Model model, Set<Integer> targetStates) {
        Set<Integer> result = new HashSet<>();

        for (int state : model.getStates()) {
            for (int successor : model.getSuccessors(state)) {
                if (targetStates.contains(successor)) {
                    result.add(state);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Computes the set of states that have ALL successors in a given set of target states, i.e
     * the states that can ONLY transition into the set of target states.
     * @param model Model
     * @param targetStates Set of target states
     * @return Set of states with all successors in target states
     */
    public static Set<Integer> preAll(Model model, Set<Integer> targetStates) {
        Set<Integer> result = new HashSet<>();

        for (int state : model.getStates()) {
            boolean addIt = true;

            for (int successor : model.getSuccessors(state)) {
                if (!targetStates.contains(successor)) {
                    addIt = false;
                    break;
                }
            }

            if (addIt) {
                result.add(state);
            }
        }

        return result;
    }

    /**
     * Checks if a given model is valid. A model is valid if all states have
     * at least one successor.
     *
     * @param model Model to validate
     * @return True if model is valid, false otherwise
     */
    public static boolean isValid(Model model) {
        try {
            for (int s : model.getStates()) {
                if (model.getSuccessors(s).size() < 1) {
                    // invalid because a state had zero successors
                    return false;
                }

                // check that the labeling function works (empty result is ok)
                if (model.getLabels(s).size() + model.getSuccessors(s).size() < 1) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * The Model to check formulas on.
     */
    private Model model;

    /**
     * The stack of results.
     */
    private Stack<Set<Integer>> resultStack;
}
