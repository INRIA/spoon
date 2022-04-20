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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmPLGrepTest {
	private SmPLGrep.Pattern getPattern(String smpl) {
		return SmPLGrep.buildPattern(SmPLParser.parse(smpl));
	}

	@Test
	public void testBuildPatternBasics01() {

		// contract: the strings 'int', 'foo', 'float', '42.3f' and 'return' should be found while 'x' should be excluded as it is a metavariable

		String smpl = "@@ identifier x; @@" +
					  "int foo() {\n" +
					  "  float x = 42.3f;\n" +
					  "  return x;\n" +
					  "}\n";

		String pattern = getPattern(smpl).toString().toLowerCase();

		assertTrue(pattern.contains("int"));
		assertTrue(pattern.contains("foo"));
		assertTrue(pattern.contains("float"));
		assertTrue(pattern.contains("42.3f"));
		assertTrue(pattern.contains("return"));
	}

	@Test
	public void testBuildPatternWithDisjunction() {

		// contract: this example should generate the pattern (foo & int & (a | b))

		String smpl = "@@ identifier x; @@" +
					  "int foo() {\n" +
					  "(\n" +
					  "  a();\n" +
					  "|\n" +
					  "  b();\n" +
					  ")\n" +
					  "}\n";

		SmPLGrep.Pattern pattern = getPattern(smpl);

		assertFalse(pattern.matches("int foo"));
		assertTrue(pattern.matches("int foo a"));
		assertTrue(pattern.matches("b foo int"));
		assertTrue(pattern.matches("int foo() { return b(); }"));
	}

	@Test
	public void testMatchIgnoresCase() {

		// contract: SmPLGrep should ignore case when matching strings against patterns

		String smpl = "@@ @@" +
					  "- WebSettings.getFontSize();\n";

		SmPLGrep.Pattern pattern = getPattern(smpl);

		assertTrue(pattern.matches("websettings GetFontSize"));
	}

	@Test
	public void testEmptyDisjunctionRemovalBug() {

		// contract: SmPLGrep.Pattern should remove empty disjunctions upon calling exitDisjunction

		SmPLGrep.Pattern pattern = new SmPLGrep.Pattern();

		pattern.addString("a");
		pattern.addString("b");
		pattern.enterDisjunction();
		pattern.exitDisjunction();

		assertEquals("(a & b)", pattern.toString());
	}
}
