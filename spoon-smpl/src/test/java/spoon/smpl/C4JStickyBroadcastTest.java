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
import static spoon.smpl.TestUtils.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * This test cherry picks and tests patch application on each true positive from the C4J patch "sticky_broadcast"
 */
public class C4JStickyBroadcastTest {
	private static ZippedCodeBaseTestContext ctx = null;

	@BeforeAll
	public static void initializeContext() {
		if (ctx != null) {
			return;
		}

		String smpl = "@@\n" +
					  "Intent intent;\n" +
					  "@@\n" +
					  "(\n" +
					  "- sendStickyBroadcast(intent);\n" +
					  "+ sendBroadcast(intent);\n" +
					  "|\n" +
					  "- removeStickyBroadcast(intent);\n" +
					  ")\n";

		ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JSendStickyBroadcast.zip", false);
	}

	@Test
	public void testSendBroadcastUploadsAdded() {

		// contract: first clause of patch should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadsAdded");
		assertTrue(method.toString().contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));
		assertTrue(method.toString().contains("    start.setPackage(getPackageName());\n    sendBroadcast(start);"));
	}

	@Test
	public void testSendBroadcastUploadStarted() {

		// contract: first clause of patch should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadStarted");
		assertTrue(method.toString().contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("    start.setPackage(getPackageName());\n    sendStickyBroadcast(start);"));
		assertTrue(method.toString().contains("    start.setPackage(getPackageName());\n    sendBroadcast(start);"));
	}

	@Test
	public void testSendBroadcastUploadFinished() {

		// contract: first clause of patch should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.owncloud.android.files.services.FileUploader::sendBroadcastUploadFinished");
		assertTrue(method.toString().contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));
		assertTrue(method.toString().contains("    end.setPackage(getPackageName());\n    sendBroadcast(end);"));
	}

	@Test
	public void testSendBroadcastDownloadFinished() {

		// contract: first clause of patch should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.owncloud.android.files.services.FileDownloader::sendBroadcastDownloadFinished");
		assertTrue(method.toString().contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("    end.setPackage(getPackageName());\n    sendStickyBroadcast(end);"));
		assertTrue(method.toString().contains("    end.setPackage(getPackageName());\n    sendBroadcast(end);"));
	}

	@Test
	public void testSendBroadcastNewDownload() {

		// contract: first clause of patch should perform replacement in target method

		CtMethod<?> method = ctx.getMethod("com.owncloud.android.files.services.FileDownloader::sendBroadcastNewDownload");
		assertTrue(method.toString().contains("    added.setPackage(getPackageName());\n    sendStickyBroadcast(added);"));

		ctx.applySmplPatch(method);
		assertFalse(method.toString().contains("    added.setPackage(getPackageName());\n    sendStickyBroadcast(added);"));
		assertTrue(method.toString().contains("    added.setPackage(getPackageName());\n    sendBroadcast(added);"));
	}

	@Test
	public void testOnReceive1() {

		// contract: second clause of patch should perform deletion in target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.owncloud.android.ui.preview.PreviewImageActivity$DownloadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = ctx.applySmplPatch(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive2() {

		// contract: second clause of patch should perform deletion in target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$SyncBroadcastReceiver::onReceive");
		assertEquals(2, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = ctx.applySmplPatch(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive3() {

		// contract: second clause of patch should perform deletion in target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$UploadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = ctx.applySmplPatch(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive4() {

		// contract: second clause of patch should perform deletion in target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.owncloud.android.ui.activity.FileDisplayActivity$DownloadFinishReceiver::onReceive");
		assertEquals(1, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = ctx.applySmplPatch(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}

	@Test
	public void testOnReceive5() {

		// contract: second clause of patch should perform deletion in target method

		CtMethod<?> method = ctx.getMethodFromInnerClass("com.owncloud.android.ui.activity.FolderPickerActivity$SyncBroadcastReceiver::onReceive");
		assertEquals(2, countOccurrences(method.toString(), "removeStickyBroadcast(intent);"));

		String result = ctx.applySmplPatch(method);
		assertEquals(0, countOccurrences(result, "removeStickyBroadcast(intent);"));
	}
}
