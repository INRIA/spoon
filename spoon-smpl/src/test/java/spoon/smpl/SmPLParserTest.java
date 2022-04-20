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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.smpl.SmPLParser.parse;
import static spoon.smpl.SmPLParser.rewrite;

public class SmPLParserTest {
	String implicitDotsBegin = "if (" + SmPLJavaDSL.getDotsWithOptionalMatchName() + "(" + SmPLJavaDSL.getDotsWhenExistsName() + "())) {";
	String implicitDotsEnd = "}";

	@Test
	public void testRewriteEmptyString() {
		assertThrows(RuntimeException.class, () -> {
			// contract: asking SmPLParser to rewrite the empty string should cause exception
			rewrite("");
		});
	} 

	@Test
	public void testRewriteBad01() {
		assertThrows(RuntimeException.class, () -> {
			// contract: asking SmPLParser to rewrite nonsense string should cause exception
			rewrite("hello");
		});
	} 

	@Test
	public void testRewriteEmptyRule() {

		// contract: SmPLParser can handle an empty rule, producing an empty Java DSL class.

		String result = DebugUtils.prettifyCLike(rewrite("@@@@\n"));
		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  "if (" + SmPLJavaDSL.createImplicitDotsCall() + ") {\n" +
											  "}\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testRewriteSimpleRule() {

		// contract: SmPLParser correctly rewrites very basic SmPL rules

		String result = DebugUtils.prettifyCLike(rewrite(
				"@@\n" +
				"identifier x;\n" +
				"@@\n" +
				"int x = 1;\n" +
				"return x + 1;\n"));

		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "identifier(x);\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  implicitDotsBegin + "\n" +
											  "int x = 1;\n" +
											  "return x + 1;\n" +
											  implicitDotsEnd + "\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testRewriteMultipleIdentifiersSingleLine() {

		// contract: SmPLParser.rewrite correctly rewrites multiple metavariable declarations on a single line

		String result = DebugUtils.prettifyCLike(rewrite(
				"@@\n" +
				"identifier x,y;\n" +
				"@@\n" +
				"int x = 1;\n" +
				"int y = 2;\n" +
				"return x + y;\n"));

		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "identifier(x);\n" +
											  "identifier(y);\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  implicitDotsBegin + "\n" +
											  "int x = 1;\n" +
											  "int y = 2;\n" +
											  "return x + y;\n" +
											  implicitDotsEnd + "\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testRewriteMultipleIdentifiersMultipleLines() {

		// contract: SmPLParser.rewrite correctly rewrites multiple metavariable declarations on multiple lines

		String result = DebugUtils.prettifyCLike(rewrite(
				"@@\n" +
				"identifier x;\n" +
				"identifier y;\n" +
				"@@\n" +
				"int x = 1;\n" +
				"int y = 2;\n" +
				"return x + y;\n"));

		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "identifier(x);\n" +
											  "identifier(y);\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  implicitDotsBegin + "\n" +
											  "int x = 1;\n" +
											  "int y = 2;\n" +
											  "return x + y;\n" +
											  implicitDotsEnd + "\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testRewriteSimpleDots() {

		// contract: SmPLParser.rewrite corrently rewrites a dots statement with a simple constraint

		String result = DebugUtils.prettifyCLike(rewrite(
				"@@\n" +
				"identifier x;\n" +
				"@@\n" +
				"int x = 1;\n" +
				"... when != x\n" +
				"return x + 1;\n"));

		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "identifier(x);\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  implicitDotsBegin + "\n" +
											  "int x = 1;\n" +
											  SmPLJavaDSL.getDotsStatementElementName() + "(" + SmPLJavaDSL.getDotsWhenNotEqualName() + "(" + SmPLJavaDSL.getExpressionMatchWrapperName() + "(x)));\n" +
											  "return x + 1;\n" +
											  implicitDotsEnd + "\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testRewriteReturningConstants() {

		// contract: SmPLParser.rewrite correctly rewrites the remove-locals-used-to-return-constants example

		String result = DebugUtils.prettifyCLike(rewrite(
				"@@\n" +
				"type T;\n" +
				"identifier ret;\n" +
				"constant C;\n" +
				"@@\n" +
				"T ret = C;\n" +
				"... when != ret\n" +
				"return ret;\n"));

		assertEquals(DebugUtils.prettifyCLike("class RewrittenSmPLRule {\n" +
											  "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
											  "type(T);\n" +
											  "identifier(ret);\n" +
											  "constant(C);\n" +
											  "}\n" +
											  SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
											  implicitDotsBegin + "\n" +
											  "T ret = C;\n" +
											  SmPLJavaDSL.getDotsStatementElementName() + "(" + SmPLJavaDSL.getDotsWhenNotEqualName() + "(" + SmPLJavaDSL.getExpressionMatchWrapperName() + "(ret)));\n" +
											  "return ret;\n" +
											  implicitDotsEnd + "\n" +
											  "}\n" +
											  "}\n"), result);
	}

	@Test
	public void testParsingAnonymousRule() {

		// contract: the parser sets null as name for anonymous rules

		SmPLRule rule = parse("@@\n" +
							  "identifier x;\n" +
							  "@@\n" +
							  "return x;\n");

		assertEquals(null, rule.getName());
	}

	@Test
	public void testParsingRuleName() {

		// contract: the parser correctly parses and sets the rule name

		SmPLRule rule;

		rule = parse("@ myrule @\n" +
					 "identifier x;\n" +
					 "@@\n" +
					 "return x;\n");

		assertEquals("myrule", rule.getName());
	}

	@Test
	public void testDisappearingFieldReadTargetBug() {

		// contract: the fieldread target of the expression "foo.x" with no further context for "foo" should not disappear when parsing a patch

		String stuff = SmPLParser.parse("@@\n" +
										"@@\n" +
										"print(foo.x);\n").getFormula().toString();

		if (!stuff.contains("print(foo.x)")) {
			fail("did not contain \"print(foo.x)\"");
		}
	}

	@Test
	public void testRewriteProducingExtraClosingBraceBug() {

		// contract: the rewrite method of the SmPL parser should not produce a superfluous closing brace for patches that explicitly match on the method header

		String smpl = "@@ @@\n" +
					  "void m() {\n" +
					  "foo();\n" +
					  "}\n";

		assertEquals("class RewrittenSmPLRule {\n" +
					 "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
					 "}\n" +
					 "void m() {\n" +
					 "foo();\n" +
					 "}\n" +
					 "}\n", SmPLParser.rewrite(smpl));
	}

	private static PrintStream err;

	private void disableStderr() {
		err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int i) throws IOException {
			}
		}));
	}

	private void enableStderr() {
		System.setErr(err);
	}

	@Test
	public void testLeadingSuperfluousStatementDotsOperatorThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: a superfluous statement dots operator at the top of a patch that does not match on the method header should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("...\n" + "\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testLeadingSuperfluousOptDotsOperatorThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: a superfluous optdots operator at the top of a patch that does not match on the method header should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("<...\n" + "...>\n" + "\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testConsecutiveStatementtDotsOperatorThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: consecutive statement dots operators should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("void m() {\n" + "...\n" + "...\n" + "}\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testStatementDotsInDisjunctionThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: statement dots in a pattern disjunction should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("(\n" + "a()\n" + "|\n" + "...\n" + ")\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testOptDotsInDisjunctionThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: an optdots block in a pattern disjunction should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("(\n" + "a()\n" + "|\n" + "<...\n" + "b();\n" + "...>\n" + ")\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testStatementDotsInAdditionThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: a statement dots operator present in an addition should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("anchor();\n" + "+ ... when any\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 

	@Test
	public void testArgumentDotsInAdditionThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> {
			// contract: a statement dots operator present in an addition should cause SmPLParser to throw an exception
			String smpl = "@@ @@\n" + ("anchor();\n" + "+ Arrays.asList(1,2,...);\n");
			disableStderr();
			try {
				SmPLParser.parse(smpl);
			} finally {
				enableStderr();
			}
		});
	} 
}

