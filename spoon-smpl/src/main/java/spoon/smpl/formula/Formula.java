package spoon.smpl.formula;

/**
 * The base interface of CTL formula elements.
 */
public interface Formula {
	/**
	 * Accept a visitor according to the Visitor pattern.
	 *
	 * @param visitor Visitor to accept
	 */
	void accept(FormulaVisitor visitor);
}
