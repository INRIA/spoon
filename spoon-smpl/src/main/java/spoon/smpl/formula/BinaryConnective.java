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

/**
 * BinaryConnective provides an abstract base class for connectives that
 * take two operands, such as "AND" and "OR".
 */
public abstract class BinaryConnective implements Formula {
	/**
	 * Create a new binary connective.
	 *
	 * @param lhs Left operand
	 * @param rhs Right operand
	 */
	public BinaryConnective(Formula lhs, Formula rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	/**
	 * Get the first operand.
	 *
	 * @return The first operand
	 */
	public Formula getLhs() {
		return lhs;
	}

	/**
	 * Get the second operand.
	 *
	 * @return The second operand
	 */
	public Formula getRhs() {
		return rhs;
	}

	/**
	 * The first operand.
	 */
	private Formula lhs;

	/**
	 * The second operand.
	 */
	private Formula rhs;
}
