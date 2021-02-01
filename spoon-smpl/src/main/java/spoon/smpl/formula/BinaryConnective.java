package spoon.smpl.formula;

/**
 * BinaryConnective provides an abstract base class for connectives that
 * take two operands, such as "AND" and "OR".
 */
public abstract class BinaryConnective implements Formula {
	/**
	 * Create a new binary connective.
	 *
	 * @param lhs Left operand
	 * @param rhs Right operand
	 */
	public BinaryConnective(Formula lhs, Formula rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	/**
	 * Get the first operand.
	 *
	 * @return The first operand
	 */
	public Formula getLhs() {
		return lhs;
	}

	/**
	 * Get the second operand.
	 *
	 * @return The second operand
	 */
	public Formula getRhs() {
		return rhs;
	}

	/**
	 * The first operand.
	 */
	private Formula lhs;

	/**
	 * The second operand.
	 */
	private Formula rhs;
}
