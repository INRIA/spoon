package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class C4JShouldVibrateTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
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

        ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JShouldVibrate.zip", false);
    }

    @Test
    public void testShouldVibrateOld() {

        // contract: patch lines 18-23 should match target method, perform replacement (patch lines 20-21) and add new method (patch lines 6-16) to parent class of target method.

        CtMethod<?> method = ctx.getMethod("org.thoughtcrime.securesms.webrtc.audio.IncomingRinger::shouldVibrateOld");
        assertTrue(method.toString().contains("return vibrate && audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);"));

        ctx.testExecutable(method);
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
                     "}",  ctx.getMethod("org.thoughtcrime.securesms.webrtc.audio.IncomingRinger::shouldVibrate").toString());
    }
}
