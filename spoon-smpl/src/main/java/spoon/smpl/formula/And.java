package spoon.smpl.formula;

/**
 * And represents the classical "AND" logical connective.
 */
public class And extends BinaryConnective {
	/**
	 * Create a new "AND" logical connective.
	 *
	 * @param lhs First Formula that must hold
	 * @param rhs Second Formula that must hold
	 */
	public And(Formula lhs, Formula rhs) {
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
		return "And(" + getLhs().toString() + ", " + getRhs().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof And && other.hashCode() == hashCode());
	}
}
