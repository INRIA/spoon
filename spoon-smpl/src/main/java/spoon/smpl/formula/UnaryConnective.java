package spoon.smpl.formula;

/**
 * UnaryConnective provides an abstract base class for connectives that
 * take a single operand, such as negation and the temporal connectives EX, AX etc.
 */
public abstract class UnaryConnective implements Formula {
	/**
	 * Create a new binary connective.
	 *
	 * @param innerElement The operand
	 */
	public UnaryConnective(Formula innerElement) {
		this.innerElement = innerElement;
	}

	/**
	 * Get the operand.
	 *
	 * @return The operand
	 */
	public Formula getInnerElement() {
		return innerElement;
	}

	/**
	 * The operand.
	 */
	private Formula innerElement;
}
