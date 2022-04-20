/**
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

import org.junit.jupiter.api.Test;
import spoon.smpl.formula.ExistsVar;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.FormulaScanner;
import spoon.smpl.formula.Predicate;
import static spoon.smpl.TestUtils.*;

import java.util.Stack;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This suite is intentionally left very sparse as the current idea is that FormulaCompiler
 * will be thoroughly tested by the end-to-end SmPL patch application tests.
 * <p>
 * Tests for bugs specific to the FormulaCompiler should go in this suite.
 */
public class FormulaCompilerTest {
	@Test
	public void testParentIdGuardGeneratedForDotsInMethodRootBug() {

		// contract: the formula compiler should not generate the "parent id" guard for dots operators that live in root scope of the method body

		SmPLMethodCFG cfg = methodCfg(parseMethod("void m() {\n" +
												  "  a();\n" +
												  "  " + SmPLJavaDSL.getDotsStatementElementName() + "();\n" +
												  "  b();\n" +
												  "}\n"));

		FormulaCompiler compiler = new FormulaCompiler(cfg, makeMetavars(), new AnchoredOperationsMap());
		assertFalse(compiler.compileFormula().toString().contains("Metadata(parent->__parent-1__)"));
	}

	@Test
	public void testUnquantifiedMetavarsBug() {

		// contract: all metavariables should be quantified in all formula branches

		Stack<String> quantifiedVariables = new Stack<>();

		BiFunction<String, String, Boolean> varMaybeUsed = (code, var) -> {
			Pattern p = Pattern.compile("((?<![A-Za-z0-9_]))" + var + "(?![A-Za-z0-9_])");
			return p.matcher(code).find();
		};

		FormulaScanner quantifiedMetavarScanner = new FormulaScanner() {
			@Override
			public void enter(Formula element) {
				if (element instanceof Predicate) {
					Predicate predicate = (Predicate) element;
					if (predicate.getMetavariables() != null) {
						for (String metavarname : predicate.getMetavariables().keySet()) {
							// FIXME: use better method of testing for a metavariable being used
							if (!varMaybeUsed.apply(predicate.toString(), metavarname)) {
								continue;
							}

							if (!quantifiedVariables.contains(metavarname)) {
								fail("metavariable " + metavarname + " encountered without being quantified");
							}
						}
					}
				} else if (element instanceof ExistsVar) {
					quantifiedVariables.push(((ExistsVar) element).getVarName());
				}
			}

			public void exit(Formula element) {
				if (element instanceof ExistsVar) {
					quantifiedVariables.pop();
				}
			}
		};

		String smpl = "@@\n" +
					  "identifier v1;\n" +
					  "constant C;\n" +
					  "@@\n" +
					  "  if (input > 0) {\n" +
					  "  ...\n" +
					  "  }\n" +
					  "- v1 = C;\n";

		SmPLParser.parse(smpl).getFormula().accept(quantifiedMetavarScanner);
	}
}
