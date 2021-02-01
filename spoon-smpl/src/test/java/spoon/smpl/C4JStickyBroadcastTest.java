package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static spoon.smpl.TestUtils.*;


/**
 * This test cherry picks and tests patch application on each true positive from the C4J patch "sticky_broadcast"
 */
public class C4JStickyBroadcastTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
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
