package spoon.smpl.formula;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A Statement Predicate contains a parameterized match pattern for a non-branching code statement.
 */
public class Statement extends CodeElementPredicate {
	public Statement(CtElement codeElement) {
		super(codeElement);
	}

	/**
	 * Create a new Statement Predicate.
	 *
	 * @param codeElement Statement code element
	 * @param metavars    Metavariable names and their corresponding constraints
	 */
	public Statement(CtElement codeElement, Map<String, MetavariableConstraint> metavars) {
		super(codeElement, metavars);
	}

	/**
	 * Implements the Visitor pattern.
	 *
	 * @param visitor
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Statement(").append(getCodeElementStringRepresentation()).append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Statement && other.hashCode() == hashCode());
	}
}
