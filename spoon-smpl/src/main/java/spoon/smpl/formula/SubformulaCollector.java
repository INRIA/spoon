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
 * A SubformulaCollector is a Formula visitor that collects every sub-Formula element into a list.
 */
public class SubformulaCollector implements FormulaVisitor {
	/**
	 * Create a new SubformulaCollector.
	 */
	public SubformulaCollector() {
		subformulas = new ArrayList<>();
	}

	/**
	 * Get the collected sub-Formulas.
	 *
	 * @return List of sub-Formulas
	 */
	public List<Formula> getResult() {
		return subformulas;
	}

	@Override
	public void visit(True element) {
		subformulas.add(element);
	}

	@Override
	public void visit(And element) {
		subformulas.add(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
	}

	@Override
	public void visit(Or element) {
		subformulas.add(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
	}

	@Override
	public void visit(Not element) {
		subformulas.add(element);
		element.getInnerElement().accept(this);
	}

	@Override
	public void visit(Predicate element) {
		subformulas.add(element);
	}

	@Override
	public void visit(ExistsNext element) {
		subformulas.add(element);
		element.getInnerElement().accept(this);
	}

	@Override
	public void visit(AllNext element) {
		subformulas.add(element);
		element.getInnerElement().accept(this);
	}

	@Override
	public void visit(ExistsUntil element) {
		subformulas.add(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
	}

	@Override
	public void visit(AllUntil element) {
		subformulas.add(element);
		element.getLhs().accept(this);
		element.getRhs().accept(this);
	}

	@Override
	public void visit(ExistsVar element) {
		subformulas.add(element);
		element.getInnerElement().accept(this);
	}

	@Override
	public void visit(SetEnv element) {
		subformulas.add(element);
	}

	@Override
	public void visit(SequentialOr element) {
		subformulas.add(element);

		for (Formula phi : element) {
			phi.accept(this);
		}
	}

	@Override
	public void visit(Optional element) {
		subformulas.add(element);

		element.getInnerElement().accept(this);
	}

	@Override
	public void visit(InnerAnd element) {
		subformulas.add(element);
		element.getInnerElement().accept(this);
	}

	/**
	 * List of collected sub-Formulas.
	 */
	private List<Formula> subformulas;
}
