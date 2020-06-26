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
                      "- T onConsoleMessage(String p1, int p2, String p3) {\n" +
                      "+ T onConsoleMessage(ConsoleMessage cs) {\n" +
                      "<...\n" +
                      "(\n" +
                      "- p1\n" +
                      "+ cs.message()\n" +
                      "|\n" +
                      "- p2\n" +
                      "+ cs.lineNumber()\n" +
                      "|\n" +
                      "- p3\n" +
                      "+ cs.sourceId()\n" +
                      ")\n" +
                      "...>\n" +
                      "}\n";

        ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JOnConsoleMessage.zip", false);
    }

    @Test
    public void testLoadFileContent() {
        CtMethod<?> outerMethod = ctx.getMethod("me.sheimi.sgit.activities.CommitDiffActivity::loadFileContent");

        CtInvocation<?> invocation = outerMethod.getBody().getStatement(4);
        CtClass<?> innerClass = ((CtNewClass<?>) invocation.getArguments().get(0)).getAnonymousClass();

        CtMethod<?> innerMethod = innerClass.getMethodsByName("onConsoleMessage").get(0);

        assertTrue(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
        assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));

        ctx.testMethod(innerMethod);
        assertFalse(innerMethod.toString().contains("public void onConsoleMessage(java.lang.String message, int lineNumber, java.lang.String sourceID) {"));
        assertTrue(innerMethod.toString().contains("public void onConsoleMessage(ConsoleMessage cs) {"));

        assertFalse(innerMethod.toString().contains("Log.d(\"MyApplication\", (((message + \" -- From line \") + lineNumber) + \" of \") + sourceID);"));
        assertTrue(innerMethod.toString().contains("Log.d(\"MyApplication\", (((cs.message() + \" -- From line \") + cs.lineNumber()) + \" of \") + cs.sourceId());"));
    }
}
