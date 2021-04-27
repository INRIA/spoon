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

import java.util.ArrayList;
import java.util.List;

/**
 * SequentialOr represents the "sequential OR" pattern disjunction operator of CTL-VW.
 * <p>
 * Semantically, the set of states that satisfy a SequentialOr of two clauses C1 and C2
 * are the states that either satisfy C1 or that satisfy And(Not(C1), C2), meaning that
 * C2 will not be considered for any states that already satisfied C1. This scheme is similar
 * to the evaluation order of disjunction clauses in many programming languages.
 */
public class SequentialOr extends ArrayList<Formula> implements Formula {
	/**
	 * Create a new empty "sequential OR" logical connective. The instance will be seen as
	 * invalid until clauses have been added.
	 */
	public SequentialOr() {
		super();
	}

	/**
	 * Create a new "sequential OR" logical connective using a given list of clauses.
	 *
	 * @param stuff Formulas of clauses
	 */
	public SequentialOr(List<Formula> stuff) {
		super(stuff);
	}

	/**
	 * At least two clauses are required for the disjunction to be considered valid.
	 *
	 * @return True if this instance represents a valid disjunction, false otherwise
	 */
	public boolean isValid() {
		return size() >= 2;
	}

	/**
	 * Implements the Visitor pattern.
	 *
	 * @param visitor Visitor to accept
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "SeqOr(" + super.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof SequentialOr && other.hashCode() == hashCode());
	}
}
