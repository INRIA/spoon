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

import spoon.smpl.formula.AllNext;
import spoon.smpl.formula.AllUntil;
import spoon.smpl.formula.And;
import spoon.smpl.formula.ExistsNext;
import spoon.smpl.formula.ExistsUntil;
import spoon.smpl.formula.ExistsVar;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.FormulaVisitor;
import spoon.smpl.formula.InnerAnd;
import spoon.smpl.formula.Not;
import spoon.smpl.formula.Optional;
import spoon.smpl.formula.Or;
import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.SequentialOr;
import spoon.smpl.formula.SetEnv;
import spoon.smpl.formula.True;

import java.util.List;
import java.util.Stack;

/**
 * FormulaOptimizer is a Formula visitor that traverses a given Formula and produces an
 * optimized Formula.
 * <p>
 * Optimizations:
 * 1) Operation-capturing formulas "And(LHS, ExistsVar("_v", SetEnv("_v", List<Operation>)))"
 * with empty operation lists are replaced by LHS.
 */
public class FormulaOptimizer implements FormulaVisitor {
	/**
	 * Repeatedly optimize a formula until there is no change.
	 *
	 * @param phi Formula to optimize
	 * @return Optimized formula
	 */
	public static Formula optimizeFully(Formula phi) {
		if (phi == null) {
			return null;
		}

		String prevStr = "";

		while (!prevStr.equals(phi.toString())) {
			prevStr = phi.toString();

			FormulaOptimizer optimizer = new FormulaOptimizer(phi);
			phi = optimizer.getResult();
		}

		return phi;
	}

	/**
	 * Optimize a Formula.
	 *
	 * @param phi Formula to optimize
	 */
	public FormulaOptimizer(Formula phi) {
		if (phi != null) {
			resultStack = new Stack<>();
			phi.accept(this);
		}
	}

	/**
	 * Get the optimized Formula.
	 *
	 * @return Optimized formula
	 */
	public Formula getResult() {
		return resultStack.pop();
	}

	/**
	 * Optimize Formula elements of type True.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(True element) {
		resultStack.push(element);
	}

	/**
	 * Optimize Formula elements of type True.
	 * <p>
	 * Direct optimizations:
	 * 1) (And(T, phi) | And(phi, T)) -> phi
	 * 2) Operation-capturing formulas "And(LHS, ExistsVar("_v", SetEnv("_v", List<Operation>)))"
	 * with empty operation lists are replaced by LHS.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(And element) {
		if (element.getLhs() instanceof True) {
			resultStack.push(element.getRhs());
		} else if (element.getRhs() instanceof True) {
			resultStack.push(element.getLhs());
		} else if (element.getRhs() instanceof ExistsVar
					&& ((ExistsVar) element.getRhs()).getVarName().equals("_v")
					&& ((ExistsVar) element.getRhs()).getInnerElement() instanceof SetEnv
					&& ((SetEnv) ((ExistsVar) element.getRhs()).getInnerElement()).getValue() instanceof List
					&& ((List<?>) ((SetEnv) ((ExistsVar) element.getRhs()).getInnerElement()).getValue()).size() == 0) {
			resultStack.push(element.getLhs());
		} else {
			element.getRhs().accept(this);
			element.getLhs().accept(this);
			resultStack.push(new And(resultStack.pop(), resultStack.pop()));
		}
	}

	/**
	 * Optimize Formula elements of type Or.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(Or element) {
		element.getRhs().accept(this);
		element.getLhs().accept(this);
		resultStack.push(new Or(resultStack.pop(), resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type Not.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(Not element) {
		element.getInnerElement().accept(this);
		resultStack.push(new Not(resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type Predicate.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(Predicate element) {
		resultStack.push(element);
	}

	/**
	 * Optimize Formula elements of type ExistsNext.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(ExistsNext element) {
		element.getInnerElement().accept(this);
		resultStack.push(new ExistsNext(resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type AllNext.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(AllNext element) {
		element.getInnerElement().accept(this);
		resultStack.push(new AllNext(resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type ExistsUntil.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(ExistsUntil element) {
		element.getRhs().accept(this);
		element.getLhs().accept(this);
		resultStack.push(new ExistsUntil(resultStack.pop(), resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type AllUntil.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(AllUntil element) {
		element.getRhs().accept(this);
		element.getLhs().accept(this);
		resultStack.push(new AllUntil(resultStack.pop(), resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type ExistsVar.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(ExistsVar element) {
		element.getInnerElement().accept(this);
		resultStack.push(new ExistsVar(element.getVarName(), resultStack.pop()));
	}

	/**
	 * Optimize Formula elements of type SetEnv.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(SetEnv element) {
		resultStack.push(element);
	}

	/**
	 * Optimize Formula elements of type SequentialOr.
	 * <p>
	 * Direct optimizations: none.
	 *
	 * @param element Formula to optimize
	 */
	@Override
	public void visit(SequentialOr element) {
		SequentialOr result = new SequentialOr();

		for (Formula formula : element) {
			formula.accept(this);
			result.add(resultStack.pop());
		}

		resultStack.push(result);
	}

	@Override
	public void visit(Optional element) {
		element.getInnerElement().accept(this);
		resultStack.push(new Optional(resultStack.pop()));
	}

	@Override
	public void visit(InnerAnd element) {
		element.getInnerElement().accept(this);
		resultStack.push(new InnerAnd(resultStack.pop()));
	}

	/**
	 * Internal stack of results.
	 */
	private Stack<Formula> resultStack;
}
