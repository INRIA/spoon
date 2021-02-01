package spoon.smpl.formula;

/**
 * ExistsVar represents the existentially quantified variable logical connective of CTL-V(W).
 * <p>
 * Semantically, "E(v, p)" selects the states that satisfy the formula "p" while also removing
 * the binding for the metavariable "v" from the environment, if such a binding exists.
 */
public class ExistsVar implements Formula {
	/**
	 * Create a new existentially quantified variable logical connective.
	 *
	 * @param varName      Variable name
	 * @param innerElement The Formula that should hold in some successor
	 */
	public ExistsVar(String varName, Formula innerElement) {
		this.varName = varName;
		this.innerElement = innerElement;
	}

	/**
	 * Get the name of the quantified variable.
	 *
	 * @return The name of the quantified variable
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * Get the inner formula.
	 *
	 * @return The inner formula
	 */
	public Formula getInnerElement() {
		return innerElement;
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
		return "E(" + varName + ", " + innerElement.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof ExistsVar && other.hashCode() == hashCode());
	}

	/**
	 * Name of quantified variable.
	 */
	private String varName;

	/**
	 * Inner formula.
	 */
	private Formula innerElement;
}
