package spoon.test.prettyprinter;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.DiffGenerator;

import java.io.File;

public class TestDiffPrinter {

    @Test
    public void diffPrinterWithoutChangesTest() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/Validation.java");
        launcher.buildModel();
        DiffGenerator printer = new DiffGenerator(launcher.getEnvironment(), new File("").getAbsolutePath());
        Factory f = launcher.getFactory();

        String output = printer.diff(f.getModel());

        Assert.assertEquals("", output);
    }

    @Test
    public void diffPrinterChangeConditionTest() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/Validation.java");
        launcher.buildModel();

        DiffGenerator printer = new DiffGenerator(launcher.getEnvironment(), new File("").getAbsolutePath());

        Factory f = launcher.getFactory();

        final CtClass<?> ctClass = f.Class().get("spoon.test.prettyprinter.testclasses.Validation");
        CtIf ctIf = ctClass.getElements(new TypeFilter<>(CtIf.class)).get(0);
        ctIf.setCondition(f.createLiteral(true));

        String output = printer.diff(f.getModel());

        Assert.assertEquals("--- src/test/java/spoon/test/prettyprinter/testclasses/Validation.java\n" +
                "+++ src/test/java/spoon/test/prettyprinter/testclasses/Validation.java\n" +
                "@@ -41,3 +41,3 @@\n" +
                " \tstatic {\n" +
                "-\t\tif (IS_SECURITY_ENABLED) {\n" +
                "+\t\tif (true) {\n" +
                " \t\t\tSKIP_IDENTIFIER_CHECK = AccessController.doPrivileged(\n", output);
    }

    @Test
    public void diffPrinterAddAndRemoveClassTest() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/AClass.java");
        launcher.buildModel();

        DiffGenerator printer = new DiffGenerator(launcher.getEnvironment(), new File("").getAbsolutePath());
        Factory f = launcher.getFactory();

        f.Class().create("test.Test");

        final CtClass<?> ctClass = f.Class().get("spoon.test.prettyprinter.testclasses.AClass");
        ctClass.getPackage().removeType(ctClass);

        String output = printer.diff(f.getModel());

        Assert.assertEquals("--- src/test/java/spoon/test/prettyprinter/testclasses/AClass.java\n" +
                "+++ src/test/java/spoon/test/prettyprinter/testclasses/AClass.java\n" +
                "@@ -1,14 +0,0 @@\n" +
                "-package spoon.test.prettyprinter.testclasses;\n" +
                "-\n" +
                "-import java.util.ArrayList;\n" +
                "-import java.util.List;\n" +
                "-\n" +
                "-public class AClass {\n" +
                "-\tpublic List<?> aMethod() {\n" +
                "-\t\treturn new ArrayList<>();\n" +
                "-\t}\n" +
                "-\n" +
                "-\tpublic List<? extends ArrayList> aMethodWithGeneric() {\n" +
                "-\t\treturn new ArrayList<>();\n" +
                "-\t}\n" +
                "-}\n" +
                "--- spooned/test/Test.java\n" +
                "+++ spooned/test/Test.java\n" +
                "@@ -0,0 +1,2 @@\n" +
                "+package test;\n" +
                "+class Test {}\n", output);
    }
}
