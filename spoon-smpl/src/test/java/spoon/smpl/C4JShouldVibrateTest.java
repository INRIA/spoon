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
                      "+ boolean shouldVibrate(AudioManager am, Context ctx, int vibrateType) {\n" +
                      "+     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {\n" +
                      "+         Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);\n" +
                      "+         if (vibrator == null || !vibrator.hasVibrator()) {\n" +
                      "+                 return false;\n" +
                      "+         }\n" +
                      "+         return am.getRingerMode() != AudioManager.RINGER_MODE_SILENT;\n" +
                      "+     } else {\n" +
                      "+         return audioManager.shouldVibrate(vibrateType);\n" +
                      "+     }\n" +
                      "+ }\n" +
                      "\n" +
                      "T f(..., Context ctx, ...) {\n" +
                      "...\n" +
                      "- am.shouldVibrate(vibrate_type)\n" +
                      "+ shouldVibrate(am, ctx, vibrate_type)\n" +
                      "...\n" +
                      "}\n";

        ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JShouldVibrate.zip", false);
    }

    @Test
    public void testShouldVibrateOld() {
        CtMethod<?> method = ctx.getMethod("org.thoughtcrime.securesms.webrtc.audio.IncomingRinger::shouldVibrateOld");
        assertTrue(method.toString().contains("return vibrate && audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);"));

        ctx.testMethod(method);
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
