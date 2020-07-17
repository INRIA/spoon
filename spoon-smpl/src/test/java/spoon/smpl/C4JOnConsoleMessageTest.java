package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class C4JOnConsoleMessageTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
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

        ctx.testExecutable(innerMethod);
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

        ctx.testExecutable(innerMethod);
        assertFalse(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
        assertTrue(innerMethod.toString().contains("public void onConsoleMessage(ConsoleMessage cs) {"));

        assertFalse(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));
        assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((cs.message() + \" -- From line \") + cs.lineNumber()) + \" of \") + cs.sourceId());"));
    }
}
