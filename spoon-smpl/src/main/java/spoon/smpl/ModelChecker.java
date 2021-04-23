/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import spoon.smpl.formula.AllNext;
import spoon.smpl.formula.AllUntil;
import spoon.smpl.formula.And;
import spoon.smpl.formula.ExistsNext;
import spoon.smpl.formula.ExistsUntil;
import spoon.smpl.formula.ExistsVar;
import spoon.smpl.formula.FormulaVisitor;
import spoon.smpl.formula.InnerAnd;
import spoon.smpl.formula.Not;
import spoon.smpl.formula.Optional;
import spoon.smpl.formula.Or;
import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.SequentialOr;
import spoon.smpl.formula.SetEnv;
import spoon.smpl.formula.True;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

// TODO: separate/extract the inner classes (witnesses, results)
// TODO: add formal logic specifications to all Formula-visiting methods, e.g SAT(M, True) def= {s | s \in M.s}

/**
 * ModelChecker implements the CTL-VW (with additional extensions) model-checking algorithm.
 */
public class ModelChecker implements FormulaVisitor {
	/**
	 * A Witness is a record of a state and metavariable binding along with the set of all other Witness
	 * records (a Witness forest) that were already present when the new record was produced.
	 */
	public static class Witness {
		/**
		 * Create a new Witness.
		 *
		 * @param state     State in which the binding was recorded
		 * @param metavar   Name of bound metavariable
		 * @param binding   Value bound to metavariable
		 * @param witnesses Witness forest of previously established Witnesses
		 */
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

			return state == otherWitness.state
					&& metavar.equals(otherWitness.metavar)
					&& binding.equals(otherWitness.binding)
					&& witnesses.equals(otherWitness.witnesses);
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

		/**
		 * State in which a binding was recorded.
		 */
		public final int state;

		/**
		 * Name of bound metavariable.
		 */
		public final String metavar;

		/**
		 * Value bound to metavariable (usually a CtElement).
		 */
		public final Object binding;

		/**
		 * Previously established Witnesses.
		 */
		public final Set<Witness> witnesses;
	}

	/**
	 * Create an empty Witness forest.
	 *
	 * @return Empty Witness forest
	 */
	public static Set<Witness> emptyWitnessForest() {
		return new HashSet<>();
	}

	/**
	 * Create a new Witness forest containing a single Witness
	 *
	 * @param outermostWitness Witness to include in new Witness forest
	 * @return New Witness forest containing the given Witness
	 */
	public static Set<Witness> newWitnessForest(Witness outermostWitness) {
		return new HashSet<>(Arrays.asList(outermostWitness));
	}

	/**
	 * A Result is a state-environment-witnessforest triple in which some Formula holds.
	 */
	public static class Result {
		/**
		 * Create a new Result.
		 *
		 * @param state       State ID where Formula held true
		 * @param environment Environment required for Formula to hold true
		 * @param witnesses   Witnesses established when Formula held true
		 */
		public Result(int state, Environment environment, Set<Witness> witnesses) {
			this.state = state;
			this.environment = environment;
			this.witnesses = witnesses;
		}

		/**
		 * Get the state ID.
		 *
		 * @return State ID
		 */
		public int getState() {
			return state;
		}

		/**
		 * Get the required Environment.
		 *
		 * @return Required Environment
		 */
		public Environment getEnvironment() {
			return environment;
		}

		/**
		 * Get the established Witnesses.
		 *
		 * @return Witnesses
		 */
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
			return state == otherResult.state
					&& environment.equals(otherResult.environment)
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

		/**
		 * State ID in which a Formula held true.
		 */
		private final int state;

		/**
		 * Environment required for Formula to hold true.
		 */
		private final Environment environment;

		/**
		 * Witnesses established when Formula held true.
		 */
		private final Set<Witness> witnesses;
	}

	/**
	 * A ResultSet is a set of state-environment-witnessforest triples equipped with facilities for
	 * intersection, union and negation.
	 */
	public static class ResultSet extends HashSet<Result> {
		/**
		 * Get the set of state IDs found in all the results in the set.
		 *
		 * @return all state IDs found in the result set
		 */
		public Set<Integer> getIncludedStates() {
			Set<Integer> result = new HashSet<>();

			for (Result r : this) {
				result.add(r.getState());
			}

			return result;
		}

		/**
		 * Get the joint Witness forests of all results in the set
		 *
		 * @return Joint Witness forest of all results in the set
		 */
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
		 *
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
		 *
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
		 *
		 * @param model     Model
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
	 *
	 * @param model The Model on which formulas should be checked
	 */
	public ModelChecker(Model model) {
		this.model = model;
		this.resultStack = new Stack<>();
	}

	/**
	 * Retrieve the most recently computed result.
	 *
	 * @return Set of states that satisfied the most recently given formula
	 * @throws java.util.EmptyStackException if called out of sequence
	 */
	public ResultSet getResult() {
		return resultStack.pop();
	}

	/**
	 * Computes the set of states that satisfy True, i.e all states.
	 *
	 * @param element TRUE Formula
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
	 *
	 * @param element AND Formula
	 */
	@Override
	public void visit(And element) {
		boolean isPredicateOperationPair = element.getLhs() instanceof Predicate
											&& element.getRhs() instanceof ExistsVar
											&& ((ExistsVar) element.getRhs()).getVarName().equals("_v");

		if (isPredicateOperationPair) {
			recordMatchedElements = true;
		}

		element.getLhs().accept(this);
		element.getRhs().accept(this);

		ResultSet rightResult = resultStack.pop();
		ResultSet leftResult = resultStack.pop();

		resultStack.push(ResultSet.intersect(leftResult, rightResult));

		if (isPredicateOperationPair) {
			ResultSet results = resultStack.pop();

			ResultSet finalResult = new ResultSet();

			for (Result result : results) {
				if (isMatchedElementOperationWitnessPair(result.witnesses)) {
					finalResult.add(new Result(result.state,
												result.environment,
												newWitnessForest(chainMatchedElementOperationWitnessPair(result.getWitnesses()))));
				} else {
					finalResult.add(result);
				}
			}

			resultStack.push(finalResult);
		}
	}

	/**
	 * Computes the set of states that satisfy "p Or q".
	 *
	 * @param element OR Formula
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
	 *
	 * @param element Formula
	 */
	@Override
	public void visit(Not element) {
		element.getInnerElement().accept(this);
		ResultSet innerResult = resultStack.pop();
		resultStack.push(ResultSet.negate(model, innerResult));
	}

	/**
	 * Computes the set of states that satisfy "p", for some predicate p.
	 *
	 * @param element Predicate Formula
	 */
	@Override
	public void visit(Predicate element) {
		ResultSet resultSet = new ResultSet();

		for (int s : model.getStates()) {
			for (Label label : model.getLabels(s)) {
				if (label.matches(element)) {
					List<LabelMatchResult> matchResults = label.getMatchResults();

					for (LabelMatchResult result : matchResults) {
						Environment environment = new Environment();

						if (result.getMetavariableBindings() != null) {
							environment.putAll(result.getMetavariableBindings());
						}

						if (recordMatchedElements && result.getMatchedElement() != null) {
							// TODO: document or make customizable the name "_e" used for indicating a matched (sub-)element.
							resultSet.add(new Result(s, environment, newWitnessForest(new Witness(s, "_e", result.getMatchedElement(), emptyWitnessForest()))));
						} else {
							resultSet.add(new Result(s, environment, emptyWitnessForest()));
						}
					}

					label.reset();
					break; // Only one label per model state is allowed to match
				} else {
					label.reset();
				}
			}
		}

		resultStack.push(resultSet);
		recordMatchedElements = false;
	}

	/**
	 * Computes the set of states that satisfy "EX p".
	 *
	 * @param element EX Formula
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
	 *
	 * @param element AX Formula
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
						// TODO: early break if jointEnvironment becomes null?
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
	 *
	 * @param element EU Formula
	 */
	@Override
	public void visit(ExistsUntil element) {
		// find the states that satisfy X in E[X U Y]
		element.getLhs().accept(this);
		ResultSet satX = resultStack.pop();

		// find the states that satisfy Y in E[X U Y], these also satisfy E[X U Y] for any X
		element.getRhs().accept(this);
		ResultSet resultSet = resultStack.pop();

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
	 *
	 * @param element AU Formula
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

	/**
	 * Creates a Witness record for an Environment binding and drops the binding from the Environment.
	 *
	 * @param element ExVar Formula
	 */
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

	/**
	 * Creates an Environment mapping for an arbitrary key-value pair.
	 *
	 * @param element SetEnv Formula
	 */
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
	 * Compute the set of states that satisfy a "sequential disjunction" of N clauses, where earlier clauses
	 * have priority over later clauses by the construction SeqOr(a, b) = Or(a, And(Not(a), b))
	 *
	 * @param element SeqOr Formula
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

	/**
	 * Select all states, preferentially matching a given inner formula.
	 *
	 * @param element Optional Formula
	 */
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
	 * Compute the set of states that satisfy InnerAnd(phi).
	 *
	 * @param element InnerAnd Formula
	 */
	@Override
	public void visit(InnerAnd element) {
		element.getInnerElement().accept(this);
		ResultSet innerResult = resultStack.pop();

		ResultSet finalResult = new ResultSet();

		// TODO: add comments for the InnerAnd computation procedure
		Map<Integer, Map<String, Set<Object>>> options = getPositiveOptions(innerResult);

		for (int state : innerResult.getIncludedStates()) {
			List<String> varnames = new ArrayList<>(options.get(state).keySet());

			CombinationsGenerator<Object> combos = new CombinationsGenerator<>();

			for (String var : varnames) {
				combos.addWheel(new ArrayList<>(options.get(state).get(var)));
			}

			while (combos.next()) {
				List<Object> bindings = combos.current();

				Environment environment = new Environment();

				for (int i = 0; i < varnames.size(); ++i) {
					environment.put(varnames.get(i), bindings.get(i));
				}

				Set<Witness> jointWitnesses = new HashSet<>();
				boolean foundSome = false;

				for (Result res : innerResult) {
					if (res.getState() == state && Environment.join(res.getEnvironment(), environment) != null) {
						foundSome = true;
						jointWitnesses.addAll(res.getWitnesses());
					}
				}

				if (foundSome) {
					if (jointWitnesses.size() > 0) {
						finalResult.add(new Result(state, environment, jointWitnesses));
					} else {
						finalResult.add(new Result(state, environment, emptyWitnessForest()));
					}
				}
			}
		}

		resultStack.push(finalResult);
	}

	/**
	 * Computes the set of states that have some successor in a given set of target states, i.e
	 * the states that CAN transition into the set of target states.
	 *
	 * @param model        Model
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
	 *
	 * @param model        Model
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
	 * Replace a specifically structured pair (two branches) of witnesses (one "matched-element" witness and one
	 * "transformation" witness) with a single witness branch nesting one of the two witnesses inside the other.
	 *
	 * @param witnesses Matched-element - transformation witness pair
	 * @return Single witness branch nesting the transformation witness inside the matched-element witness
	 */
	private Witness chainMatchedElementOperationWitnessPair(Set<Witness> witnesses) {
		List<Witness> witnessList = new ArrayList<>(witnesses);

		Witness w1 = witnessList.get(0);
		Witness w2 = witnessList.get(1);

		if (w1.metavar.equals("_e")) {
			return new Witness(w1.state, "_e", w1.binding, newWitnessForest(w2));
		} else {
			return new Witness(w2.state, "_e", w2.binding, newWitnessForest(w1));
		}
	}

	/**
	 * Check if a given witness set is a pair (two branches) of one "matched-element" witness and one "transformation"
	 * witness.
	 *
	 * @param witnesses Witness set to inspect
	 * @return True if witness set is a pair of one "matched-element" witness and one "transformation" witness, false otherwise
	 */
	private boolean isMatchedElementOperationWitnessPair(Set<Witness> witnesses) {
		if (witnesses.size() != 2) {
			return false;
		}

		List<Witness> witnessList = new ArrayList<>(witnesses);

		Witness w1 = witnessList.get(0);
		Witness w2 = witnessList.get(1);

		return w1.state == w2.state
				&& ((witnessList.get(0).metavar.equals("_e") && witnessList.get(1).metavar.equals("_v"))
					|| (witnessList.get(0).metavar.equals("_v") && witnessList.get(1).metavar.equals("_e")));
	}

	/**
	 * Given a set of Results, compute for each included state the set of positive environment variable bindings that
	 * occur in results associated with the state.
	 *
	 * @param input Input set of Results
	 * @return Map from state ID to (Map from environment variable name to Set of positive bindings)
	 */
	private static Map<Integer, Map<String, Set<Object>>> getPositiveOptions(ResultSet input) {
		Map<Integer, Map<String, Set<Object>>> result = new HashMap<>();

		for (Result r1 : input) {
			if (!result.containsKey(r1.getState())) {
				result.put(r1.getState(), new HashMap<>());
			}

			for (String key : r1.getEnvironment().keySet()) {
				if (!(r1.getEnvironment().get(key) instanceof Environment.NegativeBinding)) {
					if (!result.get(r1.getState()).containsKey(key)) {
						result.get(r1.getState()).put(key, new HashSet<>());
					}

					result.get(r1.getState()).get(key).add(r1.getEnvironment().get(key));
				}
			}
		}

		return result;
	}

	/**
	 * The Model to check formulas on.
	 */
	private Model model;

	/**
	 * The stack of results.
	 */
	private Stack<ResultSet> resultStack;

	/**
	 * Flag specifying whether "matched-element" witnesses should be created and recorded.
	 */
	private boolean recordMatchedElements;
}
