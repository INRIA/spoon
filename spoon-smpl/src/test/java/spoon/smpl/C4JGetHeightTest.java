package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class C4JGetHeightTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
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
