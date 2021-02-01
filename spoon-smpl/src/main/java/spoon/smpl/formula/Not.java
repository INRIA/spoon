package spoon.smpl.formula;

/**
 * Not represents negation, the classical "NOT" logical connective.
 */
public class Not extends UnaryConnective {
	/**
	 * Create a new "NOT" logical connective.
	 *
	 * @param innerElement
	 */
	public Not(Formula innerElement) {
		super(innerElement);
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
		return "Not(" + getInnerElement().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Not && other.hashCode() == hashCode());
	}
}
