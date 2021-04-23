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
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VariableUseScanner is a CtScanner that scans a given AST and records the set of variable names that are used in
 * some manner.
 * <p>
 * In order to support the needs of clients in an SmPL context, the scanner also records occurrences of type names
 * indicated to be known variable names. Type names that can be seen as variables in an SmPL context can occur in
 * declarations (e.g "T varname;").
 * <p>
 * Note that if a variable name occurs more than once in different forms, only the most recently encountered form
 * will be recorded in the result. For example:
 * scanning "{ int x; }"           will result in the map {x -> CtLocalVariable instance}
 * scanning "{ int x; x = x + 1; } will result in the map {x -> CtLocalVariableReference instance}
 */
public class VariableUseScanner extends CtScanner {
	/**
	 * Create a new VariableUseScanner using no known variable names.
	 *
	 * @param element AST to scan
	 */
	public VariableUseScanner(CtElement element) {
		this(element, new ArrayList<>());
	}

	/**
	 * Create a new VariableUseScanner using a list of known variable names.
	 *
	 * @param element            AST to scan
	 * @param knownVariableNames List of known variable names
	 */
	public VariableUseScanner(CtElement element, List<String> knownVariableNames) {
		this.knownVariableNames = knownVariableNames;
		this.result = new HashMap<>();

		scan(element);
	}

	/**
	 * Get the variables found to be used in the scanned AST.
	 *
	 * @return Map of variable names to the CtElements in which they appeared
	 */
	public Map<String, CtElement> getResult() {
		return result;
	}

	/**
	 * Scanner implementation.
	 *
	 * @param e AST to scan
	 */
	@Override
	protected void enter(CtElement e) {
		if (e instanceof CtVariable) {
			String varname = ((CtVariable<?>) e).getReference().getSimpleName();
			String typename = ((CtVariable<?>) e).getType().getSimpleName();

			result.put(varname, e);

			if (knownVariableNames.contains(typename)) {
				result.put(typename, e);
			}
		} else if (e instanceof CtVariableReference) {
			result.put(((CtVariableReference<?>) e).getSimpleName(), e);
		}
	}

	/**
	 * List of known variable names. The scanner will collect type names only if they are present in this list.
	 */
	private List<String> knownVariableNames;

	/**
	 * Map from used variable name to parent element of usage.
	 */
	private Map<String, CtElement> result;
}
