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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import static spoon.smpl.TestUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariableUseScannerTest {
	@Test
	public void testScanExplicitVariableDeclaration() {

		// contract: VariableUseScanner should find variable names used in variable declarations

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x = 1;\n" +
										 "    int y = 2;\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertEquals("int x = 1", result.get("x").toString());
		assertEquals("int y = 2", result.get("y").toString());
	}

	@Test
	public void testScanExplicitVariableAccesses() {

		// contract: VariableUseScanner should find variable names used in variable accesses

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    int x;\n" +
										 "    x = 10;\n" +
										 "    y = 20;\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertEquals("x", result.get("x").toString());
		assertEquals("y", result.get("y").toString());
	}

	@Test
	public void testScanTypeNameWithoutKnownVariables() {

		// contract: VariableUseScanner should NOT find type names not indicated to be known variable names

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    A x;\n" +
										 "    bar(B);\n" +
										 "}\n");

		Map<String, CtElement> result = new VariableUseScanner(method).getResult();

		assertEquals(2, result.keySet().size());
		assertTrue(result.containsKey("x"));
		assertTrue(result.containsKey("B"));
	}

	@Test
	public void testScanTypeNameUsingKnownVariables() {

		// contract: VariableUseScanner should find type names indicated to be known variable names

		CtMethod<?> method = parseMethod("void foo() {\n" +
										 "    A x;\n" +
										 "    bar(B);\n" +
										 "}\n");

		List<String> knownVariables = new ArrayList<>();
		knownVariables.add("A");
		knownVariables.add("B");

		Map<String, CtElement> result = new VariableUseScanner(method, knownVariables).getResult();

		assertEquals(3, result.keySet().size());
		assertEquals("A x", result.get("x").toString());
		assertEquals("A x", result.get("A").toString());
		assertEquals("B", result.get("B").toString());
	}
}
