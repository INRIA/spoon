package spoon.smpl;

import spoon.smpl.formula.*;
import spoon.smpl.formula.Optional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ModelChecker implements the CTL model-checking algorithm.
 */
public class ModelChecker implements FormulaVisitor {
    public static class Witness {
        public Witness(int state, String metavar, Object binding, Set<Witness> witnesses) {
            this.state = state;
            this.metavar = metavar;
            this.binding = binding;
            this.witnesses = witnesses;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("<")
              .append(state).append(", ")
              .append(metavar).append(", ")
              .append(binding).append(", ")
              .append(witnesses)
              .append(">");

            return sb.toString();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Witness)) {
                return false;
            }

            Witness otherWitness = (Witness) other;

            return state == otherWitness.state &&
                   metavar.equals(otherWitness.metavar) &&
                   binding.equals(otherWitness.binding) &&
                   witnesses.equals(otherWitness.witnesses);
        }

        @Override
        public int hashCode() {
            final int prime = 37;
            int result = 1;
            result = prime * result + 17 * state;
            result = prime * result + 19 * metavar.hashCode();
            result = prime * result + 23 * binding.hashCode();
            result = prime * result + 29 * witnesses.hashCode();
            return result;
        }

        public final int state;
        public final String metavar;
        public final Object binding;
        public final Set<Witness> witnesses;
    }

    public static Set<Witness> emptyWitnessForest() {
        return new HashSet<>();
    }

    public static Set<Witness> newWitnessForest(Witness outermostWitness) {
        return new HashSet<>(Arrays.asList(outermostWitness));
    }

    /**
     * A Result is a state-environment pair in which some formula holds.
     */
    public static class Result {
        public Result(int state, Environment environment, Set<Witness> witnesses) {
            this.state = state;
            this.environment = environment;
            this.witnesses = witnesses;
        }

        public int getState() {
            return state;
        }

        public Environment getEnvironment() {
            return environment;
        }

        public Set<Witness> getWitnesses() {
            return witnesses;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("(").append(state).append(", ")
                          .append(environment.toString()).append(", ")
                          .append(witnesses.toString())
                          .append(")");

            return sb.toString();
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Result)) {
                return false;
            }

            Result otherResult = (Result) other;
            return state == otherResult.state && environment.equals(otherResult.environment)
                   && witnesses.equals(otherResult.witnesses);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + 17 * state;
            result = prime * result + 23 * environment.hashCode();
            result = prime * result + 29 * witnesses.hashCode();
            return result;
        }

        private final int state;
        private final Environment environment;
        private final Set<Witness> witnesses;
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

        public Set<Witness> getAllWitnesses() {
            Set<Witness> witnesses = new HashSet<>();

            for (Result r : this) {
                witnesses.addAll(r.getWitnesses());
            }

            return witnesses;
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

                    Set<Witness> jointWitnesses = emptyWitnessForest();

                    jointWitnesses.addAll(r1.getWitnesses());
                    jointWitnesses.addAll(r2.getWitnesses());

                    result.add(new Result(r1.getState(), jointEnvironment, jointWitnesses));
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
                    negatedResultSet.add(new Result(state, new Environment(), emptyWitnessForest()));
                }
            }

            for (Result result : resultSet) {
                Set<Environment> negatedEnvironmentSet = Environment.negate(result.getEnvironment());

                if (negatedEnvironmentSet == null) {
                    continue;
                }

                for (Environment negatedEnvironment : negatedEnvironmentSet) {
                    negatedResultSet.add(new Result(result.getState(), negatedEnvironment, emptyWitnessForest()));
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

    // TODO: add logic syntax specifications to all Formula-visiting methods

    /**
     * Computes the set of states that satisfy True, i.e all states.
     * @param element
     */
    @Override
    public void visit(True element) {
        ResultSet resultSet = new ResultSet();

        for (int s : model.getStates()) {
            resultSet.add(new Result(s, new Environment(), emptyWitnessForest()));
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
    public void visit(Not element) {
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
                    Map<String, Object> bindings = label.getMetavariableBindings();

                    if (bindings == null) {
                        resultSet.add(new Result(s, new Environment(), emptyWitnessForest()));
                    } else {
                        Environment environment = new Environment();
                        environment.putAll(bindings);
                        resultSet.add(new Result(s, environment, emptyWitnessForest()));
                    }

                    label.reset();
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

        for (int s : canTransition) {
            List<Integer> successors = model.getSuccessors(s);

            for (Result r : innerResult) {
                if (successors.contains(r.getState())) {
                    resultSet.add(new Result(s, r.getEnvironment(), r.getWitnesses()));
                }
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
        ResultSet innerResultSet = resultStack.pop();

        ResultSet resultSet = new ResultSet();

        Set<Integer> canOnlyTransition = ModelChecker.preAll(model, innerResultSet.getIncludedStates());

        for (int s : canOnlyTransition) {
            List<Integer> successors = model.getSuccessors(s);
            Map<Integer, List<Result>> successorResultsMap = new HashMap<>();

            successors.forEach((n) -> {
                successorResultsMap.put(n, innerResultSet.stream()
                                                         .filter((result) -> result.getState() == n)
                                                         .collect(Collectors.toList()));
            });

            if (successorResultsMap.size() == 1) {
                for (Result r : successorResultsMap.get(successorResultsMap.keySet().iterator().next())) {
                    resultSet.add(new Result(s, r.getEnvironment(), r.getWitnesses()));
                }
            } else {
                CombinationsGenerator<Result> combos = new CombinationsGenerator<>();

                for (Integer key : successorResultsMap.keySet()) {
                    combos.addWheel(successorResultsMap.get(key));
                }

                while (combos.next()) {
                    Environment jointEnvironment = new Environment();
                    Set<Witness> jointWitnesses = new HashSet<>();

                    for (Result r : combos.current()) {
                        jointEnvironment = Environment.join(jointEnvironment, r.getEnvironment());
                        jointWitnesses.addAll(r.getWitnesses());
                    }

                    if (jointEnvironment != null) {
                        resultSet.add(new Result(s, jointEnvironment, jointWitnesses));
                    }
                }
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
        ResultSet satX = resultStack.pop();

        // find the states that satisfy Y in E[X U Y], these also satisfy E[X U Y] for any X
        element.getRhs().accept(this);
        ResultSet resultSet  = resultStack.pop();

        while (true) {
            // find the states that CAN transition into a state known to satisfy E[X U Y]
            Set<Integer> satisfyingStates = resultSet.getIncludedStates();

            Set<Integer> canTransition = ModelChecker.preExists(model, satisfyingStates);

            ResultSet pre = new ResultSet();

            for (int s : canTransition) {
                List<Integer> successors = model.getSuccessors(s);

                for (Result r : resultSet) {
                    if (successors.contains(r.getState())) {
                        pre.add(new Result(s, r.getEnvironment(), r.getWitnesses()));
                    }
                }
            }

            // compute the intersection with states that satisfy X
            pre = ResultSet.intersect(pre, satX);

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
        ResultSet satX = resultStack.pop();

        // find the states that satisfy Y in A[X U Y], these also satisfy A[X U Y] for any X
        element.getRhs().accept(this);
        ResultSet resultSet = resultStack.pop();

        while (true) {
            // find the states that can ONLY transition into a state known to satisfy A[X U Y]
            Set<Integer> satisfyingStates = resultSet.getIncludedStates();

            Set<Integer> canOnlyTransition = ModelChecker.preAll(model, satisfyingStates);

            ResultSet pre = new ResultSet();

            for (int s : canOnlyTransition) {
                List<Integer> successors = model.getSuccessors(s);
                Map<Integer, List<Result>> successorResultsMap = new HashMap<>();

                successors.forEach((n) -> {
                    successorResultsMap.put(n, resultSet.stream()
                                                        .filter((result) -> result.getState() == n)
                                                        .collect(Collectors.toList()));
                });

                if (successorResultsMap.size() == 1) {
                    for (Result r : successorResultsMap.get(successorResultsMap.keySet().iterator().next())) {
                        pre.add(new Result(s, r.getEnvironment(), r.getWitnesses()));
                    }
                } else {
                    CombinationsGenerator<Result> combos = new CombinationsGenerator<>();

                    for (Integer key : successorResultsMap.keySet()) {
                        combos.addWheel(successorResultsMap.get(key));
                    }

                    while (combos.next()) {
                        Environment jointEnvironment = new Environment();
                        Set<Witness> jointWitnesses = new HashSet<>();

                        for (Result r : combos.current()) {
                            jointEnvironment = Environment.join(jointEnvironment, r.getEnvironment());
                            jointWitnesses.addAll(r.getWitnesses());
                        }

                        if (jointEnvironment != null) {
                            pre.add(new Result(s, jointEnvironment, jointWitnesses));
                        }
                    }
                }
            }

            // compute the intersection with states that satisfy X
            pre = ResultSet.intersect(pre, satX);

            // extend the set of states known to satisfy A[X U Y], until there is no change
            if (!resultSet.addAll(pre)) {
                break;
            }
        }

        resultStack.push(resultSet);
    }

    @Override
    public void visit(ExistsVar element) {
        element.getInnerElement().accept(this);
        ResultSet innerResultSet = resultStack.pop();

        ResultSet resultSet = new ResultSet();

        for (Result result : innerResultSet) {
            Environment changedEnvironment = result.getEnvironment().clone();
            changedEnvironment.remove(element.getVarName());
            resultSet.add(new Result(result.getState(),
                                     changedEnvironment,
                                     newWitnessForest(new Witness(result.getState(),
                                                                  element.getVarName(),
                                                                  result.getEnvironment().get(element.getVarName()),
                                                                  result.getWitnesses()))));
        }

        resultStack.push(resultSet);
    }

    @Override
    public void visit(SetEnv element) {
        ResultSet resultSet = new ResultSet();

        Environment environment = new Environment();
        environment.put(element.getMetavariableName(), element.getValue());

        // TODO: could probably optimize this by e.g letting the state "-1" intersect with any other state, so we would need just one result here
        for (int s : model.getStates()) {
            resultSet.add(new Result(s, environment, emptyWitnessForest()));
        }

        resultStack.push(resultSet);
    }

    /**
     * Compute the set of states that satisfy a "sequential disjunction" of N clauses.
     *
     * @param element Sequential disjunction of N clauses
     */
    @Override
    public void visit(SequentialOr element) {
        if (!element.isValid()) {
            throw new IllegalArgumentException("invalid disjunction");
        }

        element.get(0).accept(this);
        ResultSet r0 = resultStack.pop();

        ResultSet notr0 = ResultSet.negate(model, r0);

        element.get(1).accept(this);
        ResultSet r1pre = resultStack.pop();

        ResultSet r1 = ResultSet.intersect(notr0, r1pre);

        ResultSet result = ResultSet.join(r0, r1);

        for (int i = 2; i < element.size(); ++i) {
            element.get(i).accept(this);
            r1pre = resultStack.pop();
            r1 = ResultSet.intersect(ResultSet.negate(model, result), r1pre);
            result = ResultSet.join(result, r1);
        }

        resultStack.push(result);
    }

    @Override
    public void visit(Optional element) {
        element.getInnerElement().accept(this);
        ResultSet leftResults = resultStack.pop();

        new True().accept(this);
        ResultSet rightResults = resultStack.pop();

        Set<Integer> resultsToCopy = rightResults.getIncludedStates();
        resultsToCopy.removeAll(leftResults.getIncludedStates());

        for (Result result : rightResults) {
            if (resultsToCopy.contains(result.getState())) {
                leftResults.add(result);
            }
        }

        resultStack.push(leftResults);
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
