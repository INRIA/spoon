package spoon.smpl.formula;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A Branch Predicate contains a parameterized match pattern for a branch statement.
 */
public class Branch extends CodeElementPredicate {
	/**
	 * Create a new Branch predicate.
	 *
	 * @param codeElement Branch statement element
	 */
	public Branch(CtElement codeElement) {
		this(codeElement, null);
	}

	/**
	 * Create a new Branch Predicate.
	 *
	 * @param codeElement Branch code element
	 * @param metavars    Metavariables names and their corresponding constraints
	 */
	public Branch(CtElement codeElement, Map<String, MetavariableConstraint> metavars) {
		super(null, metavars);
		branchType = codeElement.getClass();

		if (codeElement instanceof CtIf) {
			setCodeElement(((CtIf) codeElement).getCondition());
		} else {
			throw new IllegalArgumentException("unsupported element " + codeElement.getClass().toString());
		}
	}

	/**
	 * Get the branch type.
	 *
	 * @return the type of the branch statement element
	 */
	public Class<? extends CtElement> getBranchType() {
		return branchType;
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
		StringBuilder sb = new StringBuilder();
		sb.append("Branch<").append(branchType.getSimpleName()).append(">(").append(getCodeElementStringRepresentation()).append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Branch && other.hashCode() == hashCode());
	}

	/**
	 * The type of the branch statement element.
	 */
	private Class<? extends CtElement> branchType;
}
