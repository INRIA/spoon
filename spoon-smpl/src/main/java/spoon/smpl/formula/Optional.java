package spoon.smpl.formula;

/**
 * Optional(phi) selects all states while preferentially matching phi:
 * SAT(phi) UNION {(s, ...) in SAT(True) | (s, ...) not in SAT(phi)}
 */
public class Optional extends UnaryConnective {
	/**
	 * Create a new Optional logical connective.
	 *
	 * @param phi Formula
	 */
	public Optional(Formula phi) {
		super(phi);
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
		return "Optional(" + getInnerElement().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Optional && other.hashCode() == hashCode());
	}
}
