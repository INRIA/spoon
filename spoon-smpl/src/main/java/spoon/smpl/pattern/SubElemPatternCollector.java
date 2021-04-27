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
package spoon.smpl.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * SubElemPatternCollector implements the building of a list of all ElemNodes sub-patterns contained in a
 * given pattern.
 */
public class SubElemPatternCollector implements PatternNodeVisitor {
	/**
	 * Create a new SubElemPatternCollector.
	 */
	public SubElemPatternCollector() {
		subPatterns = new ArrayList<>();
	}

	/**
	 * Get the list of ElemNode sub-patterns.
	 *
	 * @return List of ElemNode sub-patterns
	 */
	public List<ElemNode> getResult() {
		return subPatterns;
	}

	/**
	 * Collect an element pattern and all of its element sub-patterns.
	 *
	 * @param node Pattern to collect
	 */
	@Override
	public void visit(ElemNode node) {
		subPatterns.add(node);

		for (String key : node.subPatterns.keySet()) {
			node.subPatterns.get(key).accept(this);
		}
	}

	/**
	 * Parameter patterns are ignored.
	 *
	 * @param node Pattern to collect
	 */
	@Override
	public void visit(ParamNode node) {
	}

	/**
	 * Value patterns are ignored.
	 *
	 * @param node Pattern to collect
	 */
	@Override
	public void visit(ValueNode node) {
	}

	/**
	 * Storage for resulting list of ElemNode sub-patterns.
	 */
	private List<ElemNode> subPatterns;
}
