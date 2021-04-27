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

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

// TODO: merge PatternMatcher, DotsExtPatternMatcher and SubElemPatternMatcher into a single class?

/**
 * PatternMatcher implements the matching of one pattern against another.
 * <p>
 * This class is initialized with a given rule pattern that may include parameter nodes,
 * and is able to match the rule pattern against other input patterns that may NOT include
 * parameter nodes.
 */
public class PatternMatcher implements PatternNodeVisitor {
	/**
	 * Create a new PatternNode given a rule pattern which may include parameter nodes.
	 *
	 * @param pattern Rule pattern
	 */
	public PatternMatcher(PatternNode pattern) {
		this.initialPattern = pattern;
		reset();
	}

	/**
	 * Reset the matcher.
	 */
	public void reset() {
		patternStack = new Stack<>();
		parameters = new HashMap<>();
		result = null;

		patternStack.push(initialPattern);
	}

	/**
	 * Get the result of the most recent match attempt.
	 *
	 * @return True if the input pattern matched, false otherwise.
	 */
	public boolean getResult() {
		if (result == null) {
			throw new IllegalStateException();
		}

		return result;
	}

	/**
	 * Retrieve the parameters bound in the most recent successful pattern match.
	 *
	 * @return Map from parameter names to bound values
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * Attempt to match the top of the rule pattern stack to a given input element pattern.
	 *
	 * @param otherNode Input pattern to attempt to match
	 */
	@Override
	public void visit(ElemNode otherNode) {
		PatternNode myNode = patternStack.pop();

		if (myNode instanceof ElemNode) {
			ElemNode myElemNode = (ElemNode) myNode;

			// TODO: why not use ElemNode::equals?
			if (!myElemNode.matchStr.equals(otherNode.matchStr)) {
				result = false;
				return;
			}

			for (String k : myElemNode.subPatterns.keySet()) {
				if (otherNode.subPatterns.containsKey(k)) {
					patternStack.push(myElemNode.subPatterns.get(k));
					otherNode.subPatterns.get(k).accept(this);

					if (result == false) {
						return;
					}
				}
			}

			result = true;
		} else if (myNode instanceof ParamNode) {
			result = bindParameter(((ParamNode) myNode).name, otherNode.elem);
		} else {
			result = false;
		}
	}

	/**
	 * An input pattern may not contain parameter nodes, so matching against such nodes is not supported.
	 *
	 * @param node Input pattern to attempt to match
	 */
	@Override
	public void visit(ParamNode node) {
		throw new IllegalArgumentException("Not supported");
	}

	/**
	 * Attempt to match the top of the rule pattern stack to a given input value pattern.
	 *
	 * @param otherNode Input pattern to attempt to match
	 */
	@Override
	public void visit(ValueNode otherNode) {
		PatternNode myNode = patternStack.pop();

		if (myNode instanceof ValueNode) {
			result = myNode.equals(otherNode);
		} else if (myNode instanceof ParamNode) {
			result = bindParameter(((ParamNode) myNode).name, (CtElement) otherNode.heldValue);
		} else {
			result = false;
		}
	}

	/**
	 * Try to bind a given value to a given parameter name. The binding succeeds if the parameter is
	 * presently unbound or bound to a value equal to the given value, or if both the existing bound
	 * value and the given value can be narrowed to an equal value.
	 *
	 * @param name  Parameter name to bind
	 * @param value Value to bind to given parameter name
	 * @return True if the binding was successful, false otherwise
	 */
	protected boolean bindParameter(String name, CtElement value) {
		if (!parameters.containsKey(name)) {
			parameters.put(name, value);
			return true;
		} else {
			if (parameters.get(name).equals(value)) {
				return true;
			} else {
				return tryNarrowingParameter(name, (CtElement) parameters.get(name), value);
			}
		}
	}

	/**
	 * Try to narrow a pair of parameter bindings. At present, the only implemented narrowing is
	 * from a CtVariableAccess to the enclosed CtVariableReference.
	 *
	 * @param name Parameter name to bind
	 * @param e1   Existing or proposed bound value
	 * @param e2   Existing or proposed bound value
	 * @return True if the pair of values could be narrowed and bound, false otherwise
	 */
	protected boolean tryNarrowingParameter(String name, CtElement e1, CtElement e2) {
		if (e1 instanceof CtVariableAccess<?> && e2 instanceof CtVariableAccess<?>) {
			CtVariableAccess<?> va1 = (CtVariableAccess<?>) e1;
			CtVariableAccess<?> va2 = (CtVariableAccess<?>) e2;

			if (va1.getVariable().equals(va2.getVariable())) {
				parameters.put(name, va1.getVariable());
				return true;
			}
		}

		return false;
	}

	/**
	 * Full rule pattern used as the starting point for the rule pattern stack.
	 */
	protected PatternNode initialPattern;

	/**
	 * Rule pattern stack.
	 */
	protected Stack<PatternNode> patternStack;

	/**
	 * Parameter bindings.
	 */
	protected Map<String, Object> parameters;

	/**
	 * Overall result of most recent pattern match attempt.
	 */
	protected Boolean result;
}
