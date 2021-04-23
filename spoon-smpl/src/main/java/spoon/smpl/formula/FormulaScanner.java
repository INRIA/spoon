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
 * FormulaScanner provides a bare implementation of a depth-first search on a Formula.
 * <p>
 * The idea is very similar to CtScanner, providing a base class for quickly implementing
 * tree scanning algorithms over Formulas.
 */
public class FormulaScanner implements FormulaVisitor {
	/**
	 * Called upon first encountering a Formula (sub-)element.
	 *
	 * @param element Formula element
	 */
	public void enter(Formula element) {

	}

	/**
	 * Called as notification of considering processing of a Formula (sub-)element as being finished.
	 *
	 * @param element Formula element
	 */
	public void exit(Formula element) {

	}

	/**
	 * Scan a given formula.
	 *
	 * @param phi Formula to scan
	 */
	public void scan(Formula phi) {
		phi.accept(this);
	}

	/**
	 * Visit a True element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(True element) {
		enter(element);
		exit(element);
	}

	/**
	 * Visit an And element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(And element) {
		enter(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
		exit(element);
	}

	/**
	 * Visit an Or element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(Or element) {
		enter(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
		exit(element);
	}

	/**
	 * Visit a Not element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(Not element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}

	/**
	 * Visit a Predicate element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(Predicate element) {
		enter(element);
		exit(element);
	}

	/**
	 * Visit an ExistsNext element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(ExistsNext element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}

	/**
	 * Visit an AllNext element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(AllNext element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}

	/**
	 * Visit an ExistsUntil element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(ExistsUntil element) {
		enter(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
		exit(element);
	}

	/**
	 * Visit an AllUntil element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(AllUntil element) {
		enter(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
		exit(element);
	}

	/**
	 * Visit an ExistsVar element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(ExistsVar element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}

	/**
	 * Visit a SetEnv element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(SetEnv element) {
		enter(element);
		exit(element);
	}

	/**
	 * Visit a SequentialOr element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(SequentialOr element) {
		enter(element);
		element.forEach(e -> e.accept(this));
		exit(element);
	}

	/**
	 * Visit an Optional element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(Optional element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}

	/**
	 * Visit an InnerAnd element.
	 *
	 * @param element Formula element
	 */
	@Override
	public void visit(InnerAnd element) {
		enter(element);
		element.getInnerElement().accept(this);
		exit(element);
	}
}
