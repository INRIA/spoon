package spoon.smpl.metavars;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.MetavariableConstraint;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A RegexConstraint is a generic constraint that contains an inner constraint. A RegexConstraint is applied by
 * checking if the textual representation (toString()) of the value produced by the inner constraint (if not null)
 * matches a regular expression.
 */
public class RegexConstraint implements MetavariableConstraint {
	/**
	 * Create a new RegexConstraint using a given regular expression and inner constraint.
	 *
	 * @param pattern         Regular expression
	 * @param innerConstraint Inner constraint
	 */
	public RegexConstraint(String pattern, MetavariableConstraint innerConstraint) {
		this.pattern = pattern;
		this.matchPredicate = Pattern.compile(pattern).asMatchPredicate();
		this.innerConstraint = innerConstraint;
	}

	/**
	 * Apply the constraint by checking that the value produced by the inner constraint matches the regex.
	 *
	 * @param value Value bound to metavariable
	 * @return Value produced by inner constraint if said value is not null and matches regex, null otherwise
	 */
	@Override
	public CtElement apply(CtElement value) {
		CtElement innerResult = innerConstraint.apply(value);
		return innerResult != null && matchPredicate.test(value.toString()) ? value : null;
	}

	@Override
	public String toString() {
		return innerConstraint.toString() + " + Regex(" + pattern + ")";
	}

	/**
	 * Plain text regex pattern.
	 */
	private final String pattern;

	/**
	 * Regex match predicate.
	 */
	private final Predicate<String> matchPredicate;

	/**
	 * Inner constraint.
	 */
	private final MetavariableConstraint innerConstraint;
}
