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

/**
 * ValueNode is a pattern node corresponding to some concrete flat (containing no sub-patterns) value
 * such as a String or an Integer.
 * <p>
 * A ValueNode contains two values; one value to match, and one value to "hold". The idea is that some
 * matching tasks over complex values can be made simpler or more flexible by using a separate arbitrary
 * match value (such as a String), while the original complex value can be stored and retrieved in the
 * form of the "held" value. For simple matching tasks the two values will typically be identical.
 * <p>
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ConstantNode
 */
public class ValueNode implements PatternNode {
	/**
	 * Create a new ValueNode using a given Object to match, and a given Object to hold.
	 *
	 * @param matchValue Value to match
	 * @param heldValue  Value to hold
	 */
	public ValueNode(Object matchValue, Object heldValue) {
		this.matchValue = matchValue;
		this.heldValue = heldValue;
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
		if (other instanceof ValueNode) {
			return matchValue.equals(((ValueNode) other).matchValue);
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

		sb.append("Val(");

		if (matchValue == null) {
			sb.append("null");
		} else {
			sb.append(matchValue.getClass().getSimpleName());
			sb.append(":");
			sb.append(matchValue.toString());
		}

		sb.append("; ");

		if (heldValue == null) {
			sb.append("null");
		} else {
			sb.append(heldValue.getClass().getSimpleName());
			sb.append(":");
			sb.append(heldValue.toString());
		}

		sb.append(")");
		return sb.toString();
	}

	/**
	 * Value to match.
	 */
	public final Object matchValue;

	/**
	 * Value to hold.
	 */
	public final Object heldValue;
}
