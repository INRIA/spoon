package spoon.smpl.formula;

/**
 * ExistsUntil represents the EU logical connective of CTL.
 * <p>
 * Semantically, "E[p U q]" (or "p EU q") selects the states for which there is at least
 * one path where "p" holds on every step until "q" eventually holds.
 */
public class ExistsUntil extends BinaryConnective {
	/**
	 * Create a new EU logical connective.
	 *
	 * @param lhs The Formula that must hold until the second one does
	 * @param rhs The Formula that must eventually hold
	 */
	public ExistsUntil(Formula lhs, Formula rhs) {
		super(lhs, rhs);
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
		return "EU(" + getLhs().toString() + ", " + getRhs().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof ExistsUntil && other.hashCode() == hashCode());
	}
}
