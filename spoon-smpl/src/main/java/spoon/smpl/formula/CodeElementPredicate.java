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

import spoon.reflect.declaration.CtElement;
import spoon.smpl.pattern.PatternBuilder;
import spoon.smpl.pattern.PatternNode;

import java.util.ArrayList;
import java.util.Map;

/**
 * CodeElementPredicate provides an abstract base class for Predicates that contain a
 * parameterized match pattern for a code element.
 */
public abstract class CodeElementPredicate extends ParameterizedPredicate {
	/**
	 * Create a new CodeElementPredicate.
	 *
	 * @param codeElement Code element to use
	 */
	public CodeElementPredicate(CtElement codeElement) {
		this(codeElement, null);
	}

	/**
	 * Create a new code element predicate.
	 *
	 * @param codeElement The code element to match
	 * @param metavars    Metavariable names and their corresponding constraints
	 */
	public CodeElementPredicate(CtElement codeElement, Map<String, MetavariableConstraint> metavars) {
		super(metavars);
		setCodeElement(codeElement);
	}

	/**
	 * Get the match pattern corresponding to the code element.
	 *
	 * @return The match pattern corresponding to the code element
	 */
	public PatternNode getPattern() {
		return pattern;
	}

	/**
	 * Set the contained code element to a given element.
	 *
	 * @param codeElement Element to set as code element
	 */
	protected void setCodeElement(CtElement codeElement) {
		if (codeElement == null) {
			return;
		}

		stringRep = codeElement.toString();

		ArrayList<String> metavarnames = new ArrayList<>();
		Map<String, MetavariableConstraint> metavars = getMetavariables();

		if (metavars != null) {
			metavarnames.addAll(metavars.keySet());
		}

		PatternBuilder patternBuilder = new PatternBuilder(metavarnames);
		codeElement.accept(patternBuilder);

		pattern = patternBuilder.getResult();
	}

	/**
	 * Get the String representation of the contained code element.
	 *
	 * @return String representation of code element
	 */
	protected String getCodeElementStringRepresentation() {
		return stringRep;
	}

	/**
	 * String representation of code element.
	 */
	private String stringRep;

	/**
	 * The match pattern corresponding to the code element.
	 */
	private PatternNode pattern;
}
