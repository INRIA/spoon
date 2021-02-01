package spoon.smpl.formula;

import java.util.ArrayList;
import java.util.List;

/**
 * SequentialOr represents the "sequential OR" pattern disjunction operator of CTL-VW.
 * <p>
 * Semantically, the set of states that satisfy a SequentialOr of two clauses C1 and C2
 * are the states that either satisfy C1 or that satisfy And(Not(C1), C2), meaning that
 * C2 will not be considered for any states that already satisfied C1. This scheme is similar
 * to the evaluation order of disjunction clauses in many programming languages.
 */
public class SequentialOr extends ArrayList<Formula> implements Formula {
	/**
	 * Create a new empty "sequential OR" logical connective. The instance will be seen as
	 * invalid until clauses have been added.
	 */
	public SequentialOr() {
		super();
	}

	/**
	 * Create a new "sequential OR" logical connective using a given list of clauses.
	 *
	 * @param stuff Formulas of clauses
	 */
	public SequentialOr(List<Formula> stuff) {
		super(stuff);
	}

	/**
	 * At least two clauses are required for the disjunction to be considered valid.
	 *
	 * @return True if this instance represents a valid disjunction, false otherwise
	 */
	public boolean isValid() {
		return size() >= 2;
	}

	/**
	 * Implements the Visitor pattern.
	 *
	 * @param visitor Visitor to accept
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "SeqOr(" + super.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof SequentialOr && other.hashCode() == hashCode());
	}
}
