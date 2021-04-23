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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Collection of static debug utility methods.
 */
public class DebugUtils {
	/**
	 * Hide utility class constructor.
	 */
	private DebugUtils() { }

	/**
	 * Given a CtClass of an SmPL rule in the SmPL Java DSL, pretty-print format the rule method adding line
	 * numbers to statements.
	 *
	 * @param ctClass SmPL rule in SmPL Java DSL
	 * @return Pretty-print formatted string
	 */
	public static String printRuleMethodWithLines(CtClass<?> ctClass) {
		CtMethod<?> ruleMethod = SmPLJavaDSL.getRuleMethod(ctClass);
		return printRuleMethodWithLinesInner(ruleMethod.getBody(), 0).toString();
	}

	/**
	 * Recursive helper method for printRuleMethodWithLines.
	 *
	 * @param e      Element to pretty-print format
	 * @param indent Current indentation level
	 * @return Pretty-print formatted string
	 */
	private static StringBuilder printRuleMethodWithLinesInner(CtElement e, int indent) {
		StringBuilder sb = new StringBuilder();
		String lineStr;

		if (e instanceof CtBlock) {
			sb.append("{\n");

			for (CtStatement stmt : ((CtBlock<?>) e).getStatements()) {
				sb.append(printRuleMethodWithLinesInner(stmt, indent + 4));
			}

			for (int n = 0; n < indent; ++n) {
				sb.append(" ");
			}
			sb.append("}\n");
		} else if (e instanceof CtIf) {
			lineStr = Integer.toString(e.getPosition().getLine());
			sb.append(lineStr);

			for (int n = 0; n < indent - lineStr.length(); ++n) {
				sb.append(" ");
			}

			sb.append("if (")
				.append(((CtIf) e).getCondition().toString())
				.append(") ");

			sb.append(printRuleMethodWithLinesInner(((CtIf) e).getThenStatement(), indent));

			if (((CtIf) e).getElseStatement() != null) {
				for (int n = 0; n < indent; ++n) {
					sb.append(" ");
				}
				sb.append("else ").append(printRuleMethodWithLinesInner(((CtIf) e).getElseStatement(), indent));
			}
		} else {
			lineStr = Integer.toString(e.getPosition().getLine());
			sb.append(lineStr);
			for (int n = 0; n < indent - lineStr.length(); ++n) {
				sb.append(" ");
			}
			sb.append(e.toString()).append("\n");
		}

		return sb;
	}

	/**
	 * Add indentation to text in unindented C-like syntax.
	 *
	 * @param text Text to format
	 * @return Formatted text
	 */
	public static String prettifyCLike(String text) {
		return prettifyCLike(text, '{', '}', 4, false);
	}

	/**
	 * Add indentation to text in unindented C-like syntax.
	 *
	 * @param text        Text to format
	 * @param open        Indentation-increasing character
	 * @param close       Indentation-decreasing character
	 * @param indentSize  Indentation size
	 * @param addNewlines Add newlines after indentation-altering characters?
	 * @return Formatted text
	 */
	public static String prettifyCLike(String text, char open, char close, int indentSize, boolean addNewlines) {
		StringBuilder result = new StringBuilder();

		int indent = 0;
		boolean doIndent = false;

		for (char c : text.toCharArray()) {
			if (c == close) {
				indent -= 1;

				if (addNewlines) {
					result.append('\n');
					doIndent = true;
				}
			}

			if (doIndent) {
				doIndent = false;

				for (int i = 0; i < indent; ++i) {
					for (int j = 0; j < indentSize; ++j) {
						result.append(" ");
					}
				}
			}

			result.append(c);

			if (c == '\n') {
				doIndent = true;
			}

			if (c == open) {
				indent += 1;

				if (addNewlines) {
					result.append('\n');
					doIndent = true;
				}
			}
		}

		return result.toString();
	}

	/**
	 * Produce a pretty-printed String of a given formula.
	 *
	 * @param phi Formula to pretty-print
	 * @return Pretty-printed String
	 */
	public static String prettifyFormula(Formula phi) {
		return prettifyFunctionlike(phi.toString());
	}

	/**
	 * Reformat a given String, adding line breaks after commas and indenting the content following a comma such
	 * that it aligns with the appropriate enclosing opening parenthesis.
	 * <p>
	 * Example:
	 * input: "And(First, Or(Second, Third))"
	 * output: "And(First,
	 * Or(Second,
	 * Third))
	 *
	 * @param str String to reformat
	 * @return Reformatted String
	 */
	public static String prettifyFunctionlike(String str) {
		StringBuilder sb = new StringBuilder();

		Stack<Integer> indentStack = new Stack<>();
		indentStack.push(0);

		int offset = 0;
		boolean doIndent = false;

		for (char c : str.toCharArray()) {
			if (doIndent) {
				doIndent = false;
				sb.append("\n");

				for (int i = 0; i < indentStack.peek(); ++i) {
					sb.append(" ");
				}

				offset = indentStack.peek();
			}
			if (c == '(') {
				indentStack.push(offset);
			} else if (c == ')') {
				indentStack.pop();
			} else if (c == ',') {
				doIndent = true;
			}

			sb.append(c);
			++offset;
		}

		return sb.toString();
	}

	/**
	 * Produce a pretty-printed String of a given Model.
	 *
	 * @param model Model to pretty-print
	 * @return Pretty-printed string of Model
	 */
	public static String prettifyModel(Model model) {
		StringBuilder sb = new StringBuilder();

		List<Integer> states = model.getStates();
		Collections.sort(states);

		sb.append("Model(states=")
			.append(states)
			.append(", ")
			.append("successors={");

		for (int state : states) {
			for (int next : model.getSuccessors(state)) {
				sb.append(state).append("->").append(next).append(", ");
			}
		}

		sb.delete(sb.length() - 2, sb.length());
		sb.append("}, labels={");

		for (int state : states) {
			if (model.getLabels(state).size() > 0) {
				sb.append(state).append(": [");

				for (Label label : model.getLabels(state)) {
					sb.append(label.toString()).append(", ");
				}

				sb.delete(sb.length() - 2, sb.length());
				sb.append("], ");
			} else {
				sb.append(state).append(": [], ");
			}
		}

		sb.delete(sb.length() - 2, sb.length());
		sb.append("})");

		return sb.toString();
	}
}
