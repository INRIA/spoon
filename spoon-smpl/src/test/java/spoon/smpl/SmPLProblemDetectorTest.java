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
import spoon.smpl.SmPLProblemDetector.Problem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmPLProblemDetectorTest {
	@Test
	public void testDetectLeadingSuperfluousStatementDotsOperator() {

		// contract: a superfluous statement dots operator at the top of a patch using implicit dots (not matching on method header) should be reported as an error

		String smpl = "@@ @@\n" +
					  "...\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Superfluous dots operator"));
	}

	@Test
	public void testDetectLeadingSuperfluousOptDotsOperator() {

		// contract: a superfluous optdots operator at the top of a patch using implicit dots (not matching on method header) should be reported as an error

		String smpl = "@@ @@\n" +
					  "<...\n" +
					  "...>\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Superfluous dots operator"));
	}

	@Test
	public void testDetectConsecutiveStatementDotsOperatorsPlain() {

		// contract: consecutive statement dots operators should be reported as an error

		String smpl = "@@ @@\n" +
					  "void m() {\n" +
					  "...\n" +
					  "...\n" +
					  "}\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
	}

	@Test
	public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsUpper() {

		// contract: consecutive statement dots operators should be reported as an error

		String smpl = "@@ @@\n" +
					  "void m() {\n" +
					  "... when any\n" +
					  "...\n" +
					  "}\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
	}

	@Test
	public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsLower() {

		// contract: consecutive statement dots operators should be reported as an error

		String smpl = "@@ @@\n" +
					  "void m() {\n" +
					  "...\n" +
					  "... when exists\n" +
					  "}\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
	}

	@Test
	public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsBoth() {

		// contract: consecutive statement dots operators should be reported as an error

		String smpl = "@@ @@\n" +
					  "void m() {\n" +
					  "... when any\n" +
					  "... when exists\n" +
					  "}\n" +
					  "\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
	}

	@Test
	public void testDetectStatementDotsInDisjunction() {

		// contract: statement dots operators inside a pattern disjunction should be reported as an error

		String smpl = "@@ @@\n" +
					  "(\n" +
					  "foo();\n" +
					  "|\n" +
					  "...\n" +
					  ")\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Dots operator in pattern disjunction"));
	}

	@Test
	public void testDetectOptDotsInDisjunction() {

		// contract: optdots blocks inside a pattern disjunction should be reported as an error

		String smpl = "@@ @@\n" +
					  "(\n" +
					  "foo();\n" +
					  "|\n" +
					  "<...\n" +
					  "bar();\n" +
					  "...>\n" +
					  ")\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Dots operator in pattern disjunction"));
	}

	@Test
	public void testDetectStatementDotsInAddition() {

		// contract: statement dots in an addition line should be reported as an error

		String smpl = "@@ @@\n" +
					  "anchor();\n" +
					  "+ ...\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Dots operator in addition at"));
	}

	@Test
	public void testDetectArgumentDotsInAddition() {

		// contract: argument dots in an addition line should be reported as an error

		String smpl = "@@ @@\n" +
					  "anchor();\n" +
					  "+ System.out.println(...);\n";

		List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
		assertTrue(problems.toString().contains("Error: Dots operator in addition at"));
	}
}
