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

public class C4JShouldVibrateTest {
	private static ZippedCodeBaseTestContext ctx = null;

	@BeforeAll
	public static void initializeContext() {
		if (ctx != null) {
			return;
		}

		String smpl = "@@\n" +
					  "type T;\n" +
					  "identifier am, f, ctx;\n" +
					  "expression vibrate_type;\n" +
					  "@@\n" +
				/*  6 */      "+ boolean shouldVibrate(AudioManager am, Context ctx, int vibrateType) {\n" +
				/*  7 */      "+     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {\n" +
				/*  8 */      "+         Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);\n" +
				/*  9 */      "+         if (vibrator == null || !vibrator.hasVibrator()) {\n" +
				/* 10 */      "+                 return false;\n" +
				/* 11 */      "+         }\n" +
				/* 12 */      "+         return am.getRingerMode() != AudioManager.RINGER_MODE_SILENT;\n" +
				/* 13 */      "+     } else {\n" +
				/* 14 */      "+         return audioManager.shouldVibrate(vibrateType);\n" +
				/* 15 */      "+     }\n" +
				/* 16 */      "+ }\n" +
				/* 17 */      "\n" +
				/* 18 */      "T f(..., Context ctx, ...) {\n" +
				/* 19 */      "...\n" +
				/* 20 */      "- am.shouldVibrate(vibrate_type)\n" +
				/* 21 */      "+ shouldVibrate(am, ctx, vibrate_type)\n" +
				/* 22 */      "...\n" +
				/* 23 */      "}\n";

		ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JShouldVibrate.zip", true);
	}

	@Test
	public void testShouldVibrateOld() {

		// contract: patch lines 18-23 should match target method, perform replacement (patch lines 20-21) and add new method (patch lines 6-16) to parent class of target method.

		CtMethod<?> method = ctx.getMethod("org.thoughtcrime.securesms.webrtc.audio.IncomingRinger::shouldVibrateOld");
		assertTrue(method.toString().contains("return vibrate && audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("return vibrate && audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);"));
		assertTrue(method.toString().contains("return vibrate && shouldVibrate(audioManager, context, AudioManager.VIBRATE_TYPE_RINGER);"));

		assertEquals("boolean shouldVibrate(AudioManager am, Context ctx, int vibrateType) {\n" +
					 "    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {\n" +
					 "        Vibrator vibrator = ((Vibrator) (ctx.getSystemService(Context.VIBRATOR_SERVICE)));\n" +
					 "        if ((vibrator == null) || (!vibrator.hasVibrator())) {\n" +
					 "            return false;\n" +
					 "        }\n" +
					 "        return am.getRingerMode() != AudioManager.RINGER_MODE_SILENT;\n" +
					 "    } else {\n" +
					 "        return audioManager.shouldVibrate(vibrateType);\n" +
					 "    }\n" +
					 "}", ctx.getMethod("org.thoughtcrime.securesms.webrtc.audio.IncomingRinger::shouldVibrate").toString());
	}
}
