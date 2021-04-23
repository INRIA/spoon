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
package spoon.smpl;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A LabelMatchResult is a record of a match between a Predicate and a Label, recording the specific (sub-)element
 * that matched and any metavariable bindings involved in establishing the match.
 */
public interface LabelMatchResult {
	/**
	 * Get the specifically matched code (sub-)element of the matching Label, if applicable.
	 *
	 * @return Matched code (sub-)element, or null if not applicable
	 */
	CtElement getMatchedElement();

	/**
	 * Get the metavariable bindings involved in the match, if any.
	 *
	 * @return Metavariable bindings, or null if there are none
	 */
	Map<String, Object> getMetavariableBindings();
}
