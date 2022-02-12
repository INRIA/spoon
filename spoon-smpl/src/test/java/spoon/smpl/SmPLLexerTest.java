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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmPLLexerTest {
	@Test
	public void testTokenPosition() {

		// contract: SmPLLexer should record sensible source positions for tokens

		String smpl = "@@ identifier x; @@\n" +
					  "a();\n" +
					  "    b();\n" +
					  "\t\tc();\n" +
					  "\n";

		List<SmPLLexer.Token> tokens = SmPLLexer.lex(smpl);

		for (SmPLLexer.Token token : tokens) {
			if (token.toString().contains("x")) {
				assertEquals(1, token.getPosition().getLine());
				assertEquals(15, token.getPosition().getColumn());
			} else if (token.toString().contains("a()")) {
				assertEquals(2, token.getPosition().getLine());
				assertEquals(1, token.getPosition().getColumn());
			} else if (token.toString().contains("b()")) {
				assertEquals(3, token.getPosition().getLine());
				assertEquals(5, token.getPosition().getColumn());
			} else if (token.toString().contains("c()")) {
				assertEquals(4, token.getPosition().getLine());
				assertEquals(9, token.getPosition().getColumn());
			}
		}
	}
}
