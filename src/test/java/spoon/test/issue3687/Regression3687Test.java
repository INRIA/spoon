package spoon.test.issue3687;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import spoon.Launcher;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;

public class Regression3687Test {

    /*
    This test is the regression mentioned in Issue 3687
    https://github.com/INRIA/spoon/issues/3687
     */
    @Tag("Regression")
    @Test
    public void testSnippetReplacement_ReplaceMethodCallSnippetAfterComment_shouldNotThrowError(){
        String initialClass = "package some; class A { public int mult(int a, int b){return a * b;}";

        CtClass<?> containingClass = Launcher.parseClass(initialClass);
        String methodName = "vanishingMethodCall";

        CtMethod methodToAlter= containingClass.getMethodsByName("mult").get(0);

        // Build an empty method, return void
        CtMethod emptyMethod = containingClass.getFactory().createMethod();
        emptyMethod.setSimpleName(methodName);
        emptyMethod.setParent(containingClass);
        emptyMethod.setType(emptyMethod.getFactory().Type().VOID_PRIMITIVE);
        emptyMethod.addModifier(ModifierKind.PRIVATE);
        emptyMethod.setBody(emptyMethod.getFactory().createBlock());

        containingClass.addMethod(emptyMethod);

        containingClass.compileAndReplaceSnippets();

        methodToAlter.getBody().addStatement(0,
                containingClass.getFactory().createCodeSnippetStatement(methodName+"()"));

        methodToAlter.getBody()
                .addStatement(0,containingClass.getFactory().createInlineComment("I seem to break the test"));

        // This creates the double return
        containingClass.compileAndReplaceSnippets();
        // This fails the test, as the second return does not compile
        containingClass.compileAndReplaceSnippets();

        return;
    }

    /*
    This test is the regression mentioned in issue 3688
    https://github.com/INRIA/spoon/issues/3688
     */
    @Tag("Regression")
    @Test
    public void testSnippetReplacement_BlockHasIssuesWithNextAfterComment(){
        String initialClass = "package some; class A { public int mult(int a, int b){return a * b;}";

        CtClass<?> containingClass = Launcher.parseClass(initialClass);
        String methodName = "someMethod";

        CtMethod methodToAlter= containingClass.getMethodsByName("mult").get(0);

        // Build an empty method, return void
        CtMethod emptyMethod = containingClass.getFactory().createMethod();
        emptyMethod.setSimpleName(methodName);
        emptyMethod.setParent(containingClass);
        emptyMethod.setType(emptyMethod.getFactory().Type().VOID_PRIMITIVE);
        emptyMethod.addModifier(ModifierKind.PRIVATE);
        emptyMethod.setBody(emptyMethod.getFactory().createBlock());

        containingClass.addMethod(emptyMethod);

        methodToAlter.getBody().addStatement(0,
                containingClass.getFactory().createBlock().addStatement( containingClass.getFactory().createCodeSnippetStatement(methodName+"()"))
        );

        methodToAlter.getBody().addStatement(0,containingClass.getFactory().createComment("I seem to break the test", CtComment.CommentType.BLOCK));

        // This fails the test
        containingClass.compileAndReplaceSnippets();

        return;
    }

}
