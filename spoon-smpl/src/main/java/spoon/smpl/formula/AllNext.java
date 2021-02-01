package spoon.smpl.formula;

/**
 * AllNext represents the AX logical connective of CTL.
 * <p>
 * Semantically, "AX p" selects the states for which all successors satisfy "p".
 */
public class AllNext extends UnaryConnective {
	/**
	 * Create a new AX logical connective.
	 *
	 * @param innerElement The Formula that should hold in all successors
	 */
	public AllNext(Formula innerElement) {
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
		return "AX(" + getInnerElement().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof AllNext && other.hashCode() == hashCode());
	}
}
