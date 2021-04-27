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

import spoon.reflect.declaration.CtExecutable;
import spoon.smpl.Label;
import spoon.smpl.LabelMatchResult;
import spoon.smpl.LabelMatchResultImpl;
import spoon.smpl.MethodHeaderModel;
import spoon.smpl.Model;
import spoon.smpl.ModelChecker;
import spoon.smpl.formula.MethodHeaderPredicate;
import spoon.smpl.formula.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: replace with simpler approach, a full inner CTL model + formula is overkill and leads to e.g verifyResultAndCollectMetavars
// TODO: rename since its not just methods but rather executables?

/**
 * A MethodHeaderLabel is used to associate a state of a CTL model with an inner MethodHeaderModel that
 * can be matched with a MethodHeaderPredicate.
 * <p>
 * The label requires the result of matching the inner formula of a MethodHeaderPredicate to be a single
 * result containing a single branch of witnesses, and when this is the case collects all metavariable
 * binding from said branch.
 * <p>
 * For example, the following result would be accepted as a match:
 * [r(some-state, some-env, [w(some-state, mv1, x, [w(some-state, mv2, y, [])])])]
 * r(...) a result, w(...) a witness
 * <p>
 * The following results would not be accepted:
 * [r(some-state, some-env, [w(some-state, mv1, x, []), w(some-state, mv2, y, [])])]
 * (two branches of witnesses)
 * [r(some-state, some-env, [w(some-state, mv1, x, [])]), r(some-state, some-env, [w(some-state, mv2, y, [])])]
 * (two results)
 */
public class MethodHeaderLabel implements Label {
	/**
	 * Create a new MethodHeaderLabel using a given method element.
	 *
	 * @param executable Executable to generate header label for
	 */
	public MethodHeaderLabel(CtExecutable<?> executable) {
		// TODO: test potential performance benefit of lazy initialization of the header model
		//this.method = method;
		headerModel = new MethodHeaderModel(executable);
		stringRep = executable.toString().replaceFirst("(?s)\\s*\\{.+", "");

		reset();
	}

	/**
	 * Test whether this label matches a given predicate.
	 *
	 * @param predicate Predicate to test
	 * @return True if the predicate is a MethodHeaderPredicate with formula matching (under additional constraints) the header model of the label, false otherwise.
	 */
	@Override
	public boolean matches(Predicate predicate) {
		if (predicate instanceof MethodHeaderPredicate) {
			// For lazy initialization
			//if (headerModel == null) {
			//    headerModel = new MethodHeaderModel(method);
			//}

			ModelChecker checker = new ModelChecker(headerModel);
			((MethodHeaderPredicate) predicate).getHeaderFormula().accept(checker);

			ModelChecker.ResultSet result = checker.getResult();

			HashMap<String, Object> metavarBindings = new HashMap<>();

			if (verifyResultAndCollectMetavars(result, metavarBindings)) {
				matchResults.add(new LabelMatchResultImpl(metavarBindings));
				return true;
			} else {
				reset();
				return false;
			}
		} else {
			// there is no point in supporting VariableUsePredicates here since no statement-level
			//   dots should be able to traverse the method header state

			return false;
		}
	}

	/**
	 * Get the match results produced for the most recently matched Predicate.
	 *
	 * @return List of results
	 */
	@Override
	public List<LabelMatchResult> getMatchResults() {
		return matchResults;
	}

	/**
	 * Reset/clear metavariable bindings.
	 */
	@Override
	public void reset() {
		matchResults = new ArrayList<>();
	}

	@Override
	public String toString() {
		return stringRep;
	}

	/**
	 * Remove matched-element witnesses from a given witness set.
	 *
	 * @param witnesses Witness set to process
	 * @return Input set with matched-element witnesses excluded.
	 */
	private static Set<ModelChecker.Witness> withoutMatchedElements(Set<ModelChecker.Witness> witnesses) {
		return witnesses.stream().filter(x -> !x.metavar.equals("_e")).collect(Collectors.toSet());
	}

	/**
	 * Verify that the given result set contains a single branch of witnesses and collect all the
	 * bindings on the path of that branch, storing each binding in the given map.
	 *
	 * @param result   Result set to verify and collect bindings from
	 * @param bindings Map where metavariable bindings should be stored
	 * @return True if the result set contains a single branch of witnesses, false otherwise
	 */
	private static boolean verifyResultAndCollectMetavars(ModelChecker.ResultSet result, Map<String, Object> bindings) {
		if (result.size() != 1) {
			return false;
		}

		Set<ModelChecker.Witness> witnesses = withoutMatchedElements(result.getAllWitnesses());

		switch (witnesses.size()) {
			case 0:
				return true;
			case 1:
				return verifyResultAndCollectMetavars(witnesses.iterator().next(), bindings);
			default:
				return false;
		}
	}

	/**
	 * Verify that the given witness contains a single-branch witness forest and collect all the
	 * bindings on the path of that branch, storing each binding in the given map.
	 *
	 * @param witness  Witness to verify and collect bindings from
	 * @param bindings Map where metavariable bindings should be stored
	 * @return True if the result set contains a single branch of witnesses, false otherwise
	 */
	private static boolean verifyResultAndCollectMetavars(ModelChecker.Witness witness, Map<String, Object> bindings) {
		bindings.put(witness.metavar, witness.binding);

		Set<ModelChecker.Witness> witnesses = withoutMatchedElements(witness.witnesses);

		if (witnesses.size() == 0) {
			return true;
		} else if (witnesses.size() == 1) {
			return verifyResultAndCollectMetavars(witnesses.iterator().next(), bindings);
		} else {
			return false;
		}
	}

	/**
	 * String representation of method header.
	 */
	private final String stringRep;

	///**
	// * Method element stored for enabling lazy initialization of the model.
	// */
	//private CtMethod<?> method;

	/**
	 * Model for the method header (a MethodHeaderModel, accessed under a more general interface).
	 */
	private final Model headerModel;

	/**
	 * List of match results produced from the most recently matching Predicate.
	 */
	private List<LabelMatchResult> matchResults;
}
