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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class C4JGetColorTest {
	private static ZippedCodeBaseTestContext ctx = null;

	@BeforeAll
	public static void initializeContext() {
		if (ctx != null) {
			return;
		}
		// extracted from the Coccinelle4J test suite
		// changes one call to a instance method to a call method
		String smpl = "@@\n" +
					  "Context ctx;\n" +
					  "expression E;\n" +
					  "@@\n" +
					  "- ctx.getResources().getColor(E)\n" +
					  "+ ContextCompat.getColor(ctx , E)\n" +
					  "\n";

		// the source tree of the example is unzipped
		ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JGetColor.zip", false);
	}

	@Test
	public void testNotificationBuilder() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.PushNotifications::notificationBuilder");
		// check the content of the code, the method looked for is here
		assertTrue(method.toString().contains("context.getResources().getColor(R.color.green)"));

		ctx.applySmplPatch(method);
		// the code to be removed is not there anymore
		assertFalse(method.toString().contains("context.getResources().getColor(R.color.green)"));
		// the code to be added is here now
		assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.green)"));
	}

	@Test
	public void testPrimaryColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::primaryColor");
		assertTrue(method.toString().contains("context.getResources().getColor(R.color.discovery_primary)"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("context.getResources().getColor(R.color.discovery_primary)"));
		assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.discovery_primary)"));
	}

	@Test
	public void testSecondaryColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::secondaryColor");
		assertTrue(method.toString().contains("context.getResources().getColor(R.color.discovery_secondary)"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("context.getResources().getColor(R.color.discovery_secondary)"));
		assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.discovery_secondary)"));
	}

	@Test
	public void testOverlayTextColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::overlayTextColor", 1);
		assertTrue(method.toString().contains("context.getResources().getColor(color)"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("context.getResources().getColor(color)"));
		assertTrue(method.toString().contains("ContextCompat.getColor(context, color)"));
	}

	@Test
	public void testDarkColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::darkColor");
		assertTrue(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));
		assertTrue(method.toString().contains("ContextCompat.getColor(context, com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));
	}

	@Test
	public void testLightColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::lightColor");
		assertTrue(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));
		assertTrue(method.toString().contains("ContextCompat.getColor(context, com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));
	}

	@Test
	public void testForegroundColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::foregroundColor");
		assertTrue(method.toString().contains("return context.getResources().getColor(colorId)"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("return context.getResources().getColor(colorId)"));
		assertTrue(method.toString().contains("return ContextCompat.getColor(context, colorId)"));
	}

	@Test
	public void testCategorySecondaryColor() {

		// contract: patch (lines 5-6) should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.kickstarter.models.Category::secondaryColor");
		assertTrue(method.toString().contains("return context.getResources().getColor(identifier)"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("return context.getResources().getColor(identifier)"));
		assertTrue(method.toString().contains("return ContextCompat.getColor(context, identifier)"));
	}
}
