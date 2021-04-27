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

import spoon.smpl.Label;
import spoon.smpl.LabelMatchResult;
import spoon.smpl.LabelMatchResultImpl;
import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.Proposition;

import java.util.Collections;
import java.util.List;

/**
 * A PropositionLabel is a Label used to associate states with simple, constant proposition strings
 * that can be matched using Proposition Formula elements.
 */
public class PropositionLabel implements Label {
	/**
	 * Create a new PropositionLabel.
	 *
	 * @param label The proposition string
	 */
	public PropositionLabel(String label) {
		this.label = label;
	}

	/**
	 * Test whether the label matches the given predicate.
	 *
	 * @param obj Predicate to test
	 * @return True if the predicate is a Proposition element with matching proposition string, false otherwise.
	 */
	public boolean matches(Predicate obj) {
		if (obj instanceof Proposition) {
			return ((Proposition) obj).getProposition().equals(label);
		} else {
			return false;
		}
	}

	/**
	 * Propositions do not involve the binding of any information, so an informationless match result is always returned.
	 *
	 * @return Singleton list of informationless match result
	 */
	@Override
	public List<LabelMatchResult> getMatchResults() {
		return Collections.singletonList(new LabelMatchResultImpl());
	}

	/**
	 * Propositions do not involve the binding of any information so resets are not necessary.
	 */
	@Override
	public void reset() {
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PropositionLabel)) {
			return false;
		}

		return label.equals(((PropositionLabel) other).label);
	}

	/**
	 * The proposition string.
	 */
	private String label;
}
