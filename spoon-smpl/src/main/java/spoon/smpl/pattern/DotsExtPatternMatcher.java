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

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.smpl.SmPLJavaDSL;

import java.util.Arrays;

// TODO: merge PatternMatcher, DotsExtPatternMatcher and SubElemPatternMatcher into a single class?

/**
 * DotsExtPatternMatcher extends the basic PatternMatcher with support for the SmPL dots operator in
 * invocation argument lists.
 */
public class DotsExtPatternMatcher extends PatternMatcher {
	/**
	 * Create a new DotsExtPatternMatcher using a given rule pattern.
	 *
	 * @param pattern Rule pattern to use
	 */
	public DotsExtPatternMatcher(PatternNode pattern) {
		super(pattern);
	}

	/**
	 * Attempt to match the top of the rule pattern stack to a given input element pattern.
	 *
	 * @param otherNode Input pattern to attempt to match
	 */
	@Override
	public void visit(ElemNode otherNode) {
		PatternNode myNode = patternStack.pop();

		if (myNode instanceof ElemNode && isInvocationWithDots((ElemNode) myNode)) {
			ElemNode myElemNode = (ElemNode) myNode;

			if (!myElemNode.matchStr.equals(otherNode.matchStr)) {
				result = false;
				return;
			}

			matchInvocationWithDots(myElemNode, otherNode);
		} else {
			patternStack.push(myNode);
			super.visit(otherNode);
		}
	}

	/**
	 * Get the value held in the "numargs" sub-pattern of the given invocation element pattern.
	 *
	 * @param node Invocation element pattern
	 * @return Value held in "numargs" sub-pattern of invocation pattern
	 */
	private static int numArgs(ElemNode node) {
		return (int) ((ValueNode) node.subPatterns.get("numargs")).heldValue;
	}

	/**
	 * Get the nth argument sub-pattern of the given invocation element pattern.
	 *
	 * @param node Invocation element pattern
	 * @param n    Index of argument to retrieve
	 * @return Sub-pattern of nth argument in given invocation pattern
	 */
	private static PatternNode nthArg(ElemNode node, int n) {
		return node.subPatterns.get("arg" + Integer.toString(n));
	}

	/**
	 * Determine whether a given pattern represents an SmPL argument-or-parameter-list dots operator.
	 *
	 * @param node Pattern to inspect
	 * @return True if pattern represents a dots operator, false otherwise
	 */
	private static boolean isDots(PatternNode node) {
		return node instanceof ElemNode
				&& ((ElemNode) node).elem instanceof CtVariableRead<?>
				&& ((CtVariableRead<?>) ((ElemNode) node).elem).getVariable().getSimpleName().equals(SmPLJavaDSL.getDotsParameterOrArgumentElementName());
	}

	/**
	 * Determine whether a given element pattern represents an invocation that uses one or more SmPL dots
	 * operators in its argument list.
	 *
	 * @param node Pattern to inspect
	 * @return True if pattern represents an invocation using dots, false otherwise
	 */
	private static boolean isInvocationWithDots(ElemNode node) {
		if (!(node.elem instanceof CtAbstractInvocation<?>) || ((CtAbstractInvocation<?>) node.elem).getArguments().size() < 1) {
			return false;
		}

		int numargs = numArgs(node);

		for (int i = 0; i < numargs; ++i) {
			if (isDots(node.subPatterns.get("arg" + Integer.toString(i)))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Find the first non-dots argument in a given invocation pattern.
	 *
	 * @param invocationNode Invocation pattern to inspect
	 * @param numargs        Number of argument in invocation
	 * @param start          Search start offset
	 * @return Index of first non-dots argument in respect to given offset, or -1 if no such argument is present
	 */
	private static int firstNonDotsArgIndex(ElemNode invocationNode, int numargs, int start) {
		for (int n = start; n < numargs; ++n) {
			if (!isDots(nthArg(invocationNode, n))) {
				return n;
			}
		}

		return -1;
	}

	/**
	 * Attempt to match a rule invocation pattern using the dots operator (a pattern node representing an
	 * invocation where one or more argument nodes represent the SmPL dots operator and in which parameter
	 * nodes are allowed) against a target invocation pattern (a pattern node representing an invocation in
	 * which parameter nodes are NOT allowed).
	 * <p>
	 * The result of the match attempt is written to the "result" instance field.
	 *
	 * @param myElemNode Rule invocation pattern
	 * @param otherNode  Target invocation pattern
	 */
	private void matchInvocationWithDots(ElemNode myElemNode, ElemNode otherNode) {
		for (String key : Arrays.asList("executable", "target")) {
			if (myElemNode.subPatterns.containsKey(key) && otherNode.subPatterns.containsKey(key)) {
				patternStack.push(myElemNode.subPatterns.get(key));
				otherNode.subPatterns.get(key).accept(this);

				if (!result) {
					return;
				}
			}
		}

		int myNumArgs = numArgs(myElemNode);

		if (myNumArgs == 1) {
			result = true;
			return;
		}

		int otherNumArgs = numArgs(otherNode);

		int i = firstNonDotsArgIndex(myElemNode, myNumArgs, 0);
		boolean activeDots = (i != 0);

		for (int j = 0; j < otherNumArgs; ++j) {
			patternStack.push(nthArg(myElemNode, i));
			nthArg(otherNode, j).accept(this);

			if (result == true) {
				activeDots = (i + 1) < myNumArgs && isDots(nthArg(myElemNode, i + 1));
				i = firstNonDotsArgIndex(myElemNode, myNumArgs, i + 1);

				if (activeDots && i == -1) {
					return;
				}
			} else if (!activeDots) {
				return;
			}
		}

		if (i != -1) {
			result = false;
		}
	}
}
