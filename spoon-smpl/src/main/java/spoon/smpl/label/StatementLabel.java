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

//import spoon.pattern.Match;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.LabelMatchResultImpl;
import spoon.smpl.formula.Statement;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.DotsExtPatternMatcher;
import spoon.smpl.pattern.PatternMatcher;

import java.util.Map;

/**
 * A StatementLabel is a Label used to associate states with CtElement code
 * elements that can be matched using Statement Formula elements.
 * <p>
 * The intention is for a StatementLabel to contain code that corresponds
 * to a Java statement, but the current implementation does not enforce this.
 */
public class StatementLabel extends CodeElementLabel {
	/**
	 * Create a new StatementLabel.
	 *
	 * @param codeElement Code element
	 */
	public StatementLabel(CtElement codeElement) {
		super(codeElement);
	}

	/**
	 * Test whether the label matches the given predicate.
	 *
	 * @param predicate Predicate to test
	 * @return True if the predicate is a Statement element whose Pattern matches the code, false otherwise.
	 */
	public boolean matches(Predicate predicate) {
		if (predicate instanceof Statement) {
			Statement sp = (Statement) predicate;
			PatternMatcher matcher = new DotsExtPatternMatcher(sp.getPattern());
			codePattern.accept(matcher);

			if (matcher.getResult()) {
				Map<String, Object> metavarBindings = matcher.getParameters();

				if (sp.processMetavariableBindings(metavarBindings)) {
					matchResults.add(new LabelMatchResultImpl(codeElement, metavarBindings));
					return true;
				}
			}

			return false;
		} else {
			return super.matches(predicate);
		}
	}
}
