package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtConstructor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class C4JSetTextSizeTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
    public static void initializeContext() {
        if (ctx != null) {
            return;
        }

        String smpl = "@@\n" +
                      "expression E;\n" +
                      "@@\n" +
                      "(\n" +
                      "- E.setTextSize(LARGEST);\n" +
                      "+ E.setTextZoom(200);\n" +
                      "|\n" +
                      "- E.setTextSize(LARGER);\n" +
                      "+ E.setTextZoom(150);\n" +
                      "|\n" +
                      "- E.setTextSize(NORMAL);\n" +
                      "+ E.setTextZoom(100);\n" +
                      "|\n" +
                      "- E.setTextSize(SMALLER);\n" +
                      "+ E.setTextZoom(75);\n" +
                      "|\n" +
                      "- E.setTextSize(SMALLEST);\n" +
                      "+ E.setTextZoom(50);\n" +
                      ")\n";

        ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JSetTextSize.zip", false);
    }

    private String getLine(String text, int n) {
        return text.split("\n")[n];
    }

    private int getLineNo(String text, int pos) {
        return TestUtils.countOccurrences(text.substring(0, pos), "\n");
    }

    @Test
    public void testCustomWebView() {

        // contract: each clause of the patch should transform exactly one statement in the target constructor

        CtConstructor<?> constructor = null;

        String wantedSignature = "views.CustomWebView(com.powerpoint45.lucidbrowser.MainActivity,android.util.AttributeSet,java.lang.String)";

        for (CtConstructor<?> c : ctx.getClassFromModel("views.CustomWebView").getConstructors()) {
            if (c.getSignature().equals(wantedSignature)) {
                constructor = c;
                break;
            }
        }

        if (constructor == null) {
            fail("Could not find the correct constructor");
        }

        String constructorCode = constructor.toString();

        int pos1 = constructorCode.indexOf("this.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);");
        int pos2 = constructorCode.indexOf("this.getSettings().setTextSize(WebSettings.TextSize.SMALLER);");
        int pos3 = constructorCode.indexOf("this.getSettings().setTextSize(WebSettings.TextSize.NORMAL);");
        int pos4 = constructorCode.indexOf("this.getSettings().setTextSize(WebSettings.TextSize.LARGER);");
        int pos5 = constructorCode.indexOf("this.getSettings().setTextSize(WebSettings.TextSize.LARGEST);");

        assertTrue(pos1 > 0);
        assertTrue(pos2 > 0);
        assertTrue(pos3 > 0);
        assertTrue(pos4 > 0);
        assertTrue(pos5 > 0);

        ctx.testExecutable(constructor);

        String newConstructorCode = constructor.toString();

        assertFalse(newConstructorCode.contains("this.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);"));
        assertFalse(newConstructorCode.contains("this.getSettings().setTextSize(WebSettings.TextSize.SMALLER);"));
        assertFalse(newConstructorCode.contains("this.getSettings().setTextSize(WebSettings.TextSize.NORMAL);"));
        assertFalse(newConstructorCode.contains("this.getSettings().setTextSize(WebSettings.TextSize.LARGER);"));
        assertFalse(newConstructorCode.contains("this.getSettings().setTextSize(WebSettings.TextSize.LARGEST);"));

        assertTrue(getLine(newConstructorCode, getLineNo(constructorCode, pos1)).contains("this.getSettings().setTextZoom(50);"));
        assertTrue(getLine(newConstructorCode, getLineNo(constructorCode, pos2)).contains("this.getSettings().setTextZoom(75);"));
        assertTrue(getLine(newConstructorCode, getLineNo(constructorCode, pos3)).contains("this.getSettings().setTextZoom(100);"));
        assertTrue(getLine(newConstructorCode, getLineNo(constructorCode, pos4)).contains("this.getSettings().setTextZoom(150);"));
        assertTrue(getLine(newConstructorCode, getLineNo(constructorCode, pos5)).contains("this.getSettings().setTextZoom(200);"));
    }
}
