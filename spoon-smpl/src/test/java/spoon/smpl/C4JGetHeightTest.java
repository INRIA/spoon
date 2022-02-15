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
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class C4JGetHeightTest {
	private static ZippedCodeBaseTestContext ctx = null;

	@BeforeAll
	public static void initializeContext() {
		if (ctx != null) {
			return;
		}

		String smpl = "@rule1@\n" +
					  "Display display;\n" +
					  "identifier p;\n" +
					  "type T;\n" +
					  "@@\n" +
				/*  6 */      "(\n" +
				/*  7 */      "- p = new Point(display.getWidth(), display.getHeight());\n" +
				/*  8 */      "+ p = new Point();\n" +
				/*  9 */      "+ display.getSize(p);\n" +
				/* 10 */      "|\n" +
				/* 11 */      "- T p = new Point(display.getWidth(), display.getHeight());\n" +
				/* 12 */      "+ T p = new Point();\n" +
				/* 13 */      "+ display.getSize(p);\n" +
				/* 14 */      ")\n" +
				/* 15 */      "<...\n" +
				/* 16 */      "(\n" +
				/* 17 */      "- display.getHeight()\n" +
				/* 18 */      "+ p.y\n" +
				/* 19 */      "|\n" +
				/* 20 */      "- display.getWidth()\n" +
				/* 21 */      "+ p.x\n" +
				/* 22 */      ")\n" +
				/* 23 */      "...>\n";

		ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JGetHeight.zip", false);
	}

	@Test
	public void testGetDisplayDimens() {

		// contract: the first clause of the first disjunction (patch lines 7-9) should match and transform the target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.bumptech.glide.request.target.ViewTarget$SizeDeterminer::getDisplayDimens");

		assertTrue(method.toString().contains("new android.graphics.Point(display.getWidth(), display.getHeight());"));
		assertEquals(1, TestUtils.countOccurrences(method.toString(), "display.getSize(displayDimens);"));

		ctx.applySmplPatch(method);

		assertFalse(method.toString().contains("new android.graphics.Point(display.getWidth(), display.getHeight());"));
		assertEquals(2, TestUtils.countOccurrences(method.toString(), "display.getSize(displayDimens);"));
	}
}
