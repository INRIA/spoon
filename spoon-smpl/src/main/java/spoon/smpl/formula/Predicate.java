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
package spoon.smpl.formula;

import java.util.Map;

/**
 * A Predicate is a Formula that can match state labels of a CTL model.
 * <p>
 * Semantically, the set of states that satisfy a Predicate are the states for which the
 * predicate matches one or more of the states' labels.
 */
public interface Predicate extends Formula {
	/**
	 * Get the metavariables (and their constraints) associated with the predicate.
	 *
	 * @return Metavariables
	 */
	Map<String, MetavariableConstraint> getMetavariables();

	/**
	 * Validate and potentially modify metavariable bindings.
	 *
	 * @param parameters Mutable map of metavariable bindings
	 * @return True if bindings could be validated (potentially by modification), false otherwise
	 */
	boolean processMetavariableBindings(Map<String, Object> parameters);
}
