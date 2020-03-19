package spoon.smpl;

import spoon.smpl.formula.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Map;

/**
 * ModelChecker implements the CTL model-checking algorithm.
 */
public class ModelChecker implements FormulaVisitor {
    /**
     * A Result is a state-environment pair in which some formula holds.
     */
    public static class Result {
        public Result(int state, Environment environment) {
            this.state = state;
            this.environment = environment;
        }

        public int getState() {
            return state;
        }

        public Environment getEnvironment() {
            return environment;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("(").append(state).append(", ").append(environment.toString()).append(")");
            return sb.toString();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Result)) {
                return false;
            }

            Result otherResult = (Result) other;
            return state == otherResult.state && environment.equals(otherResult.environment);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + 17 * state;
            result = prime * result + 23 * environment.hashCode();
            return result;
        }

        private final int state;
        private final Environment environment;
    }

    /**
     * A ResultSet is a set of state-environment pairs equipped with facilities for
     * intersection, union and negation.
     */
    public static class ResultSet extends HashSet<Result> {
        /**
         * @return all states included in Results contained in the set
         */
        public Set<Integer> getIncludedStates() {
            Set<Integer> result = new HashSet<>();

            for (Result r : this) {
                result.add(r.getState());
            }

            return result;
        }

        /**
         * Compute the intersection of two ResultSets. This involves computing the greatest
         * lower bound of compatible environments, and rejecting the intersection for conflicting
         * environments.
         * @param s1 First set
         * @param s2 Second set
         * @return the intersection of the two sets
         */
        public static ResultSet intersect(ResultSet s1, ResultSet s2) {
            ResultSet result = new ResultSet();

            for (Result r1 : s1) {
                for (Result r2 : s2) {
                    if (r1.getState() != r2.getState()) {
                        continue;
                    }

                    Environment jointEnvironment = Environment.join(r1.getEnvironment(), r2.getEnvironment());

                    if (jointEnvironment == null) {
                        continue;
                    }

                    result.add(new Result(r1.getState(), jointEnvironment));
                }
            }

            return result;
        }

        /**
         * Compute the union/join of two ResultSets.
         * @param s1 First set
         * @param s2 Second set
         * @return the union of the two sets
         */
        public static ResultSet join(ResultSet s1, ResultSet s2) {
            ResultSet result = new ResultSet();

            result.addAll(s1);
            result.addAll(s2);

            return result;
        }

        /**
         * Compute the negation of a given ResultSet. The negation includes one entry with
         * an empty environment for every state NOT included in the given set, and one entry
         * with a negated environment for every state-environment pair included in the given
         * set.
         * @param model Model
         * @param resultSet ResultSet to negate
         * @return the negation of the given set
         */
        public static ResultSet negate(Model model, ResultSet resultSet) {
            ResultSet negatedResultSet = new ResultSet();
            Set<Integer> includedStates = resultSet.getIncludedStates();

            for (int state : model.getStates()) {
                if (!includedStates.contains(state)) {
                    negatedResultSet.add(new Result(state, new Environment()));
                }
            }

            for (Result result : resultSet) {
                Set<Environment> negatedEnvironment = Environment.negate(result.getEnvironment());

                if (negatedEnvironment == null) {
                    continue;
                }

                for (Environment e : negatedEnvironment) {
                    negatedResultSet.add(new Result(result.getState(), e));
                }
            }

            return negatedResultSet;
        }
    }

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
    public ResultSet getResult() {
        return resultStack.pop();
    }

    /**
     * Computes the set of states that satisfy True, i.e all states.
     * @param element
     */
    @Override
    public void visit(True element) {
        ResultSet resultSet = new ResultSet();

        for (int s : model.getStates()) {
            resultSet.add(new Result(s, new Environment()));
        }

        resultStack.push(resultSet);
    }

    /**
     * Computes the set of states that satisfy "p And q".
     * @param element
     */
    @Override
    public void visit(And element) {
        element.getLhs().accept(this);
        element.getRhs().accept(this);

        ResultSet rightResult = resultStack.pop();
        ResultSet leftResult = resultStack.pop();

        resultStack.push(ResultSet.intersect(leftResult, rightResult));
    }

    /**
     * Computes the set of states that satisfy "p Or q".
     * @param element
     */
    @Override
    public void visit(Or element) {
        element.getLhs().accept(this);
        element.getRhs().accept(this);

        ResultSet rightResult = resultStack.pop();
        ResultSet leftResult = resultStack.pop();

        resultStack.push(ResultSet.join(leftResult, rightResult));
    }

    /**
     * Computes the set of states that satisfy "not p".
     * @param element
     */
    @Override
    public void visit(Neg element) {
        element.getInnerElement().accept(this);
        ResultSet innerResult = resultStack.pop();
        resultStack.push(ResultSet.negate(model, innerResult));
    }

    /**
     * Computes the set of states that satisfy "p", for some predicate p.
     * @param element
     */
    @Override
    public void visit(Predicate element) {
        ResultSet resultSet = new ResultSet();

        for (int s : model.getStates()) {
            for (Label label : model.getLabels(s)) {
                if (label.matches(element)) {
                    Map<String, Object> params = label.getMatchedParameters();
                    Environment environment = new Environment();

                    if (params != null) {
                        environment.putAll(params);
                    }

                    label.reset();

                    resultSet.add(new Result(s, environment));
                    break;
                }
            }
        }

        resultStack.push(resultSet);
    }

    /**
     * Computes the set of states that satisfy "EX p".
     * @param element
     */
    @Override
    public void visit(ExistsNext element) {
        element.getInnerElement().accept(this);
        ResultSet innerResult = resultStack.pop();

        ResultSet resultSet = new ResultSet();

        Set<Integer> canTransition = ModelChecker.preExists(model, innerResult.getIncludedStates());

        for (Result r : innerResult) {
            for (int s : canTransition) {
                resultSet.add(new Result(s, r.getEnvironment()));
            }
        }

        resultStack.push(resultSet);
    }

    /**
     * Computes the set of states that satisfy "AX p".
     * @param element
     */
    @Override
    public void visit(AllNext element) {
        element.getInnerElement().accept(this);
        ResultSet innerResult = resultStack.pop();

        ResultSet resultSet = new ResultSet();

        Set<Integer> canOnlyTransition = ModelChecker.preAll(model, innerResult.getIncludedStates());

        for (Result r : innerResult) {
            for (int s : canOnlyTransition) {
                resultSet.add(new Result(s, r.getEnvironment()));
            }
        }

        resultStack.push(resultSet);
    }

    /**
     * Computes the set of states that satisfy "E[p U q]".
     * @param element
     */
    @Override
    public void visit(ExistsUntil element) {
        // find the states that satisfy X in E[X U Y]
        element.getLhs().accept(this);
        ResultSet satphi = resultStack.pop();

        // find the states that satisfy Y in E[X U Y], these also satisfy E[X U Y] for any X
        element.getRhs().accept(this);
        ResultSet resultSet = resultStack.pop();

        while (true) {
            // find the states that can transition into a state known to satisfy E[X U Y]
            Set<Integer> satisfyingStates = resultSet.getIncludedStates();
            Set<Integer> canTransition = ModelChecker.preExists(model, satisfyingStates);

            ResultSet pre = new ResultSet();

            for (Result r : resultSet) {
                for (int s : canTransition) {
                    pre.add(new Result(s, r.getEnvironment()));
                }
            }

            // compute the intersection with states that satisfy X
            pre = ResultSet.intersect(pre, satphi);

            // extend the set of states known to satisfy E[X U Y], until there is no change
            if (!resultSet.addAll(pre)) {
                break;
            }
        }

        resultStack.push(resultSet);
    }

    /**
     * Computes the set of states that satisfy "A[p U q]".
     * @param element
     */
    @Override
    public void visit(AllUntil element) {
        // find the states that satisfy X in A[X U Y]
        element.getLhs().accept(this);
        ResultSet satphi = resultStack.pop();

        // find the states that satisfy Y in A[X U Y], these also satisfy A[X U Y] for any X
        element.getRhs().accept(this);
        ResultSet resultSet = resultStack.pop();

        while (true) {
            // find the states that can ONLY transition into a state known to satisfy A[X U Y]
            Set<Integer> satisfyingStates = resultSet.getIncludedStates();
            Set<Integer> canOnlyTransition = ModelChecker.preAll(model, satisfyingStates);

            ResultSet pre = new ResultSet();

            for (Result r : resultSet) {
                for (int s : canOnlyTransition) {
                    pre.add(new Result(s, r.getEnvironment()));
                }
            }

            // compute the intersection with states that satisfy X
            pre = ResultSet.intersect(pre, satphi);

            // extend the set of states known to satisfy A[X U Y], until there is no change
            if (!resultSet.addAll(pre)) {
                break;
            }
        }

        resultStack.push(resultSet);
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
    private Stack<ResultSet> resultStack;
}
