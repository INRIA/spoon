/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
