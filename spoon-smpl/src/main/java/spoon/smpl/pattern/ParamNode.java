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
 * ParamNode is a pattern node that represents the pattern-matching task of binding an arbitrary,
 * structurally-corresponding element found in the "target" pattern. A "target" pattern is a pattern
 * containing only concrete value nodes (no ParamNodes), and it becomes the "target" when we try
 * to match an arbitrary pattern (which may include ParamNodes) against it.
 * <p>
 * The concept of this class roughly corresponds to spoon.pattern.internal.node.ParameterNode
 */
public class ParamNode implements PatternNode {
	/**
	 * Create a new ParamNode with a given name.
	 *
	 * @param name Name of the parameter represented by this node
	 */
	public ParamNode(String name) {
		this.name = name;
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
		if (other instanceof ParamNode) {
			return name.equals(((ParamNode) other).name);
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
		return "Param(" + name + ")";
	}

	/**
	 * Name of the parameter represented by this node.
	 */
	public String name;
}
