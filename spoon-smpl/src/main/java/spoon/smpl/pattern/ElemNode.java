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

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ElemNode is a pattern node corresponding to a metamodel element.
 * <p>
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ElementNode
 */
public class ElemNode implements PatternNode {
	/**
	 * Create a new ElemNode holding a given element that matches on the combination of the literal
	 * pretty-printed String representation of the element together with any sub-patterns.
	 *
	 * @param elem Element to use
	 */
	public ElemNode(CtElement elem) {
		this(elem, elem.getClass().toString());
	}

	/**
	 * Create a new ElemNode holding a given element that matches on the combination of a given
	 * literal String together with any sub-patterns.
	 *
	 * @param elem     Element to use
	 * @param matchStr Literal String to match
	 */
	public ElemNode(CtElement elem, String matchStr) {
		this.elem = elem;
		this.matchStr = matchStr;
		this.subPatterns = new HashMap<>();
	}

	/**
	 * Visitor pattern dispatch.
	 *
	 * @param visitor Visitor to accept
	 */
	@Override
	public void accept(PatternNodeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Compare this pattern node to a given pattern node.
	 *
	 * @param other Pattern node to compare against
	 * @return True if nodes match, false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof ElemNode) {
			return matchStr.equals(((ElemNode) other).matchStr) && subPatterns.equals(((ElemNode) other).subPatterns);
		} else {
			return false;
		}
	}

	/**
	 * Get a String representation of this pattern node.
	 *
	 * @return String representation
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Elem(");
		sb.append(matchStr);
		sb.append(", ");

		List<String> subKeys = new ArrayList<>(subPatterns.keySet());
		Collections.sort(subKeys);

		for (String key : subKeys) {
			sb.append(key);
			sb.append("=");
			sb.append(subPatterns.get(key).toString());
			sb.append(", ");
		}

		sb.delete(sb.length() - 2, sb.length());

		sb.append(")");

		return sb.toString();
	}

	/**
	 * Held metamodel element.
	 */
	public final CtElement elem;

	/**
	 * Literal String to match against other ElemNodes.
	 */
	public final String matchStr;

	/**
	 * Sub-patterns.
	 */
	public final Map<String, PatternNode> subPatterns;
}
