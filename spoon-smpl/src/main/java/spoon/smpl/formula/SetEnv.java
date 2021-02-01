package spoon.smpl.formula;

/**
 * SetEnv represents an instruction for a model checker to set a specific entry in
 * the environment to a specific value. This is used together with ExistsVar to
 * associate instructions with a model-matching formula such that the instructions are included
 * in the set of witnesses for states matching the model-matching formula.
 * <p>
 * As an example, consider the following formula:
 * And(Proposition("foo"), ExistsVar("_v", SetEnv("_v", "delete")))
 * <p>
 * Model checking this formula on a model that contains a state S labeled with the proposition
 * "foo" will return a result for S containing a witness in which the variable "_v" is bound to
 * the value "delete".
 */
public class SetEnv implements Formula {
	/**
	 * Create a new SetEnv instruction.
	 *
	 * @param metavar Target variable name
	 * @param value   Value to assign
	 */
	public SetEnv(String metavar, Object value) {
		this.metavar = metavar;
		this.value = value;
	}

	/**
	 * Get target variable name.
	 *
	 * @return Target variable name
	 */
	public String getMetavariableName() {
		return metavar;
	}

	/**
	 * Get value to assign.
	 *
	 * @return Value to assign
	 */
	public Object getValue() {
		return value;
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
		return "SetEnv(" + metavar + " = " + value.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof SetEnv && other.hashCode() == hashCode());
	}

	/**
	 * Target variable name.
	 */
	private final String metavar;

	/**
	 * Value to assign.
	 */
	private final Object value;
}
