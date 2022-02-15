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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class C4JOnConsoleMessageTest {
	private static ZippedCodeBaseTestContext ctx = null;

	@BeforeAll
	public static void initializeContext() {
		if (ctx != null) {
			return;
		}

		String smpl = "@@\n" +
					  "type T;\n" +
					  "identifier p1, p2, p3;\n" +
					  "@@\n" +
				/*  5 */      "- T onConsoleMessage(String p1, int p2, String p3) {\n" +
				/*  6 */      "+ T onConsoleMessage(ConsoleMessage cs) {\n" +
				/*  7 */      "<...\n" +
				/*  8 */      "(\n" +
				/*  9 */      "- p1\n" +
				/* 10 */      "+ cs.message()\n" +
				/* 11 */      "|\n" +
				/* 12 */      "- p2\n" +
				/* 13 */      "+ cs.lineNumber()\n" +
				/* 14 */      "|\n" +
				/* 15 */      "- p3\n" +
				/* 16 */      "+ cs.sourceId()\n" +
				/* 17 */      ")\n" +
				/* 18 */      "...>\n" +
				/* 19 */      "}\n";

		ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JOnConsoleMessage.zip", false);
	}

	@Test
	public void testLoadFileContent() {

		// contract: the patch should match and perform each of the four replacements (lines 5-6, 9-10, 12-13, 15-16) exactly once

		CtMethod<?> outerMethod = ctx.getMethod("me.sheimi.sgit.activities.CommitDiffActivity::loadFileContent");

		CtInvocation<?> invocation = outerMethod.getBody().getStatement(4);
		CtClass<?> innerClass = ((CtNewClass<?>) invocation.getArguments().get(0)).getAnonymousClass();

		CtMethod<?> innerMethod = innerClass.getMethodsByName("onConsoleMessage").get(0);

		assertTrue(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
		assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));

		ctx.applySmplPatch(innerMethod);
		assertFalse(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
		assertTrue(innerMethod.toString().contains("public void onConsoleMessage(ConsoleMessage cs) {"));

		assertFalse(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));
		assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((cs.message() + \" -- From line \") + cs.lineNumber()) + \" of \") + cs.sourceId());"));
	}

	@Test
	public void testViewFileFragment() {

		// contract: the patch should match and perform each of the four replacements (lines 5-6, 9-10, 12-13, 15-16) exactly once

		CtMethod<?> outerMethod = ctx.getMethod("me.sheimi.sgit.fragments.ViewFileFragment::onCreateView");

		CtInvocation<?> invocation = outerMethod.getBody().getStatement(10);
		CtClass<?> innerClass = ((CtNewClass<?>) invocation.getArguments().get(0)).getAnonymousClass();

		CtMethod<?> innerMethod = innerClass.getMethodsByName("onConsoleMessage").get(0);

		assertTrue(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
		assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));

		ctx.applySmplPatch(innerMethod);
		assertFalse(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
		assertTrue(innerMethod.toString().contains("public void onConsoleMessage(ConsoleMessage cs) {"));

		assertFalse(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));
		assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((cs.message() + \" -- From line \") + cs.lineNumber()) + \" of \") + cs.sourceId());"));
	}
}
