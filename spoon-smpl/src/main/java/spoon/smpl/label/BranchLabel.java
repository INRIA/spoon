package spoon.smpl.label;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.LabelMatchResultImpl;
import spoon.smpl.formula.Branch;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.DotsExtPatternMatcher;
import spoon.smpl.pattern.PatternMatcher;

import java.util.Map;


/**
 * A BranchLabel is a Label used to associate states with CtElement code
 * elements that can be matched using Branch Formula elements.
 */
public class BranchLabel extends CodeElementLabel {
	/**
	 * Create a new BranchLabel.
	 *
	 * @param cond Condition element of a branch statement element such as CtIf
	 */
	public BranchLabel(CtElement cond) {
		super(cond);

		CtElement parent = cond.getParent(); // throws ParentNotInitializedException

		boolean ok = false;

		// check for supported type of branch element, more supported types to come
		if (parent instanceof CtIf) {
			ok = true;
		}

		if (!ok) {
			throw new IllegalArgumentException("Invalid condition parent");
		}
	}

	/**
	 * Test whether the label matches the given predicate.
	 *
	 * @param predicate Predicate to test
	 * @return True if the predicate is a Branch element whose Pattern matches the code exactly once, false otherwise.
	 */
	public boolean matches(Predicate predicate) {
		if (predicate instanceof Branch) {
			Branch bp = (Branch) predicate;

			if (!bp.getBranchType().isInstance(codeElement.getParent())) {
				return false;
			}

			PatternMatcher matcher = new DotsExtPatternMatcher(bp.getPattern());
			codePattern.accept(matcher);

			if (matcher.getResult()) {
				Map<String, Object> metavarBindings = matcher.getParameters();

				if (bp.processMetavariableBindings(metavarBindings)) {
					matchResults.add(new LabelMatchResultImpl(codeElement.getParent(), metavarBindings));
					return true;
				}
			}

			return false;
		} else {
			return super.matches(predicate);
		}
	}

	@Override
	public String toString() {
		return "if (" + codeElement.toString() + ")";
	}
}
